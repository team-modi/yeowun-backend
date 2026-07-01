package modi.backend.infra.exhibition;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import modi.backend.domain.exhibition.Exhibition;
import modi.backend.domain.exhibition.ExhibitionQuery;
import modi.backend.domain.exhibition.ExhibitionType;

/**
 * {@link ExhibitionQuery} → JPA {@link Specification} 변환. (03_전시.md 3.3.1 처리 로직)
 * 살아있는 행만, CUSTOM 노출 규칙, keyword/ongoingOn/region/category 필터를 조합한다.
 */
final class ExhibitionSpecifications {

	private ExhibitionSpecifications() {
	}

	static Specification<Exhibition> from(ExhibitionQuery query) {
		return (root, cq, cb) -> {
			List<Predicate> predicates = new ArrayList<>();

			// soft delete 제외
			predicates.add(cb.isNull(root.get("deletedAt")));

			// 노출 범위: CATALOG는 공개, CUSTOM은 요청자 본인 것만. 비로그인이면 CATALOG만.
			Predicate isCatalog = cb.equal(root.get("type"), ExhibitionType.CATALOG);
			if (query.requesterId() == null) {
				predicates.add(isCatalog);
			} else {
				Predicate ownCustom = cb.and(
						cb.equal(root.get("type"), ExhibitionType.CUSTOM),
						cb.equal(root.get("ownerId"), query.requesterId()));
				predicates.add(cb.or(isCatalog, ownCustom));
			}

			// keyword: 전시명·전시장명 부분 일치(대소문자 무시). (작가명은 원천 미보유 — 04_전시_구현.md 참고)
			if (query.keyword() != null && !query.keyword().isBlank()) {
				String like = "%" + query.keyword().trim().toLowerCase() + "%";
				predicates.add(cb.or(
						cb.like(cb.lower(root.get("title")), like),
						cb.like(cb.lower(cb.coalesce(root.get("place"), "")), like)));
			}

			// ongoingOn: 해당 날짜 진행 중. 시작/종료일이 없으면 각각 "이미 시작"/"아직 진행"으로 관대하게 취급.
			if (query.ongoingOn() != null) {
				predicates.add(cb.or(
						cb.isNull(root.get("startDate")),
						cb.lessThanOrEqualTo(root.get("startDate"), query.ongoingOn())));
				predicates.add(cb.or(
						cb.isNull(root.get("endDate")),
						cb.greaterThanOrEqualTo(root.get("endDate"), query.ongoingOn())));
			}

			if (query.region() != null) {
				predicates.add(cb.equal(root.get("region"), query.region()));
			}
			if (query.category() != null) {
				predicates.add(cb.equal(root.get("category"), query.category()));
			}

			return cb.and(predicates.toArray(Predicate[]::new));
		};
	}
}
