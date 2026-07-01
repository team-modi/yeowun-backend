package modi.backend.application.exhibition;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import modi.backend.domain.exhibition.CatalogExhibitionData;
import modi.backend.domain.exhibition.Exhibition;
import modi.backend.domain.exhibition.ExhibitionCatalogClient;
import modi.backend.domain.exhibition.ExhibitionCategory;
import modi.backend.domain.exhibition.ExhibitionErrorCode;
import modi.backend.domain.exhibition.ExhibitionQuery;
import modi.backend.domain.exhibition.ExhibitionRegion;
import modi.backend.domain.exhibition.ExhibitionRepository;
import modi.backend.support.error.CoreException;
import modi.backend.support.error.ErrorType;

/**
 * 전시 유스케이스 조율(03_전시.md). load·조율·save만 하고, 상태 변경·규칙 판단은 {@link Exhibition} 메서드에 위임한다.
 * CATALOG는 외부 API({@link ExhibitionCatalogClient})로 동기화해 DB에 upsert하고, 조회/등록은 DB만 본다
 * (실시간 외부 호출 대신 수집-적재 방식 — 공공데이터 리뷰의 "야간 배치 동기화" 아키텍처).
 */
@Service
@RequiredArgsConstructor
public class ExhibitionFacade {

	private final ExhibitionRepository exhibitionRepository;
	private final ExhibitionCatalogClient catalogClient;

	/** 목록/탐색(3.3.1). 필터 미지정 시 오늘 진행 중인 전시를 기본 노출한다. 비로그인은 CATALOG만. */
	@Transactional(readOnly = true)
	public Page<ExhibitionResult.ListItem> search(ExhibitionCriteria.Search criteria, Pageable pageable) {
		ExhibitionRegion region = criteria.region() == null ? null : ExhibitionRegion.from(criteria.region());
		ExhibitionCategory category = criteria.category() == null ? null
				: ExhibitionCategory.from(criteria.category());
		LocalDate ongoingOn = resolveOngoingOn(criteria.date(), criteria.keyword(), region, category);

		ExhibitionQuery query = new ExhibitionQuery(criteria.keyword(), ongoingOn, region, category,
				criteria.requesterId());
		return exhibitionRepository.search(query, pageable).map(ExhibitionResult.ListItem::from);
	}

	/** 상세(3.3.2). 없으면 404, 타인의 CUSTOM이면 403. */
	@Transactional(readOnly = true)
	public ExhibitionResult.Detail getDetail(ExhibitionCriteria.Detail criteria) {
		Exhibition exhibition = exhibitionRepository.findById(criteria.exhibitionId())
				.orElseThrow(() -> new CoreException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));
		if (!exhibition.isAccessibleBy(criteria.requesterId())) {
			throw new CoreException(ErrorType.FORBIDDEN, "타인의 개인 전시 접근: " + criteria.exhibitionId());
		}
		return ExhibitionResult.Detail.from(exhibition);
	}

	/** 개인 전시 등록(3.3.3). 제목 필수·기간 검증은 Entity에서 수행한다. */
	@Transactional
	public ExhibitionResult.Created registerCustom(ExhibitionCriteria.CustomCreate criteria) {
		ExhibitionRegion region = criteria.region() == null ? null : ExhibitionRegion.from(criteria.region());
		ExhibitionCategory category = criteria.category() == null ? null
				: ExhibitionCategory.from(criteria.category());
		Exhibition exhibition = Exhibition.createCustom(criteria.ownerId(), criteria.title(), criteria.place(),
				criteria.startDate(), criteria.endDate(), region, category, criteria.posterUrl());
		return ExhibitionResult.Created.from(exhibitionRepository.save(exhibition));
	}

	/**
	 * 외부 전시 API 수집 → DB upsert(externalId 기준). 이미 있으면 카탈로그 필드 갱신, 없으면 신규 적재.
	 * 인증키 미설정 시 수집 목록이 비어 0을 반환한다(외부 호출 없음).
	 *
	 * @return 이번 동기화로 적재/갱신된 전시 수
	 */
	@Transactional
	public int syncCatalog() {
		List<CatalogExhibitionData> collected = catalogClient.fetchAll();
		for (CatalogExhibitionData data : collected) {
			exhibitionRepository.findByExternalId(data.externalId())
					.ifPresentOrElse(existing -> refresh(existing, data), () -> create(data));
		}
		return collected.size();
	}

	private void refresh(Exhibition existing, CatalogExhibitionData data) {
		existing.refreshCatalog(data.title(), data.place(), data.startDate(), data.endDate(), data.region(),
				data.category(), data.posterUrl(), null, null, null, data.detailUrl(), data.serviceName(),
				data.gpsX(), data.gpsY());
		exhibitionRepository.save(existing);
	}

	private void create(CatalogExhibitionData data) {
		exhibitionRepository.save(Exhibition.createCatalog(data.externalId(), data.title(), data.place(),
				data.startDate(), data.endDate(), data.region(), data.category(), data.posterUrl(), null, null,
				null, data.detailUrl(), data.serviceName(), data.gpsX(), data.gpsY()));
	}

	/** date 지정 시 그 날짜, 아무 필터 없으면 오늘(랜딩 기본), 그 외(keyword/region/category만)엔 기간 제한 없음. */
	private LocalDate resolveOngoingOn(LocalDate date, String keyword, ExhibitionRegion region,
			ExhibitionCategory category) {
		if (date != null) {
			return date;
		}
		boolean noOtherFilter = (keyword == null || keyword.isBlank()) && region == null && category == null;
		return noOtherFilter ? LocalDate.now() : null;
	}
}
