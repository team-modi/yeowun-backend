package modi.backend.infra.exhibition;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import modi.backend.config.PublicDataProperties;
import modi.backend.domain.exhibition.CatalogExhibitionData;
import modi.backend.domain.exhibition.ExhibitionCategory;
import modi.backend.domain.exhibition.ExhibitionCatalogClient;
import modi.backend.domain.exhibition.ExhibitionErrorCode;
import modi.backend.domain.exhibition.ExhibitionRegion;
import modi.backend.support.error.CoreException;

/**
 * {@link ExhibitionCatalogClient} 어댑터 — 한눈에보는문화정보 API 호출·파싱을 담당한다(DIP).
 * 외부 응답 포맷(YYYYMMDD 날짜·자유 텍스트 지역/분야)을 도메인 {@link CatalogExhibitionData}로 정규화한다.
 * 통신 실패·비정상 응답은 {@link ExhibitionErrorCode#EXTERNAL_API_UNAVAILABLE}로 변환한다(HTTP·라이브러리 예외 누수 차단).
 */
@Component
@RequiredArgsConstructor
public class CultureExhibitionClient implements ExhibitionCatalogClient {

	private static final Logger log = LoggerFactory.getLogger(CultureExhibitionClient.class);
	private static final DateTimeFormatter YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");

	private final WebClient cultureWebClient;
	private final PublicDataProperties properties;

	@Override
	public List<CatalogExhibitionData> fetchAll() {
		if (!properties.isConfigured()) {
			// 인증키 미설정: 외부 호출을 시도하지 않고 스킵한다(데모는 시드 데이터로 동작 — 04_전시_구현.md).
			log.info("공공데이터 인증키(CULTURE_API_KEY) 미설정 — 외부 전시 동기화 스킵");
			return List.of();
		}
		int numOfRows = properties.numOfRows();
		List<CatalogExhibitionData> collected = new ArrayList<>();
		for (int pageNo = 1; pageNo <= properties.maxPages(); pageNo++) {
			List<CultureApiResponse.PerforItem> items = fetchPage(pageNo, numOfRows);
			items.stream().map(this::toData).filter(CatalogExhibitionData::isPersistable).forEach(collected::add);
			if (items.size() < numOfRows) {
				break; // 마지막 페이지
			}
		}
		return collected;
	}

	private List<CultureApiResponse.PerforItem> fetchPage(int pageNo, int numOfRows) {
		CultureApiResponse body = call(pageNo, numOfRows);
		if (!body.isSuccess()) {
			throw new CoreException(ExhibitionErrorCode.EXTERNAL_API_UNAVAILABLE,
					"외부 전시 API 비정상 응답: " + Optional.ofNullable(body.response()).map(Object::toString).orElse("null"));
		}
		return body.items();
	}

	private CultureApiResponse call(int pageNo, int numOfRows) {
		try {
			return cultureWebClient.get()
					.uri(builder -> builder
							.queryParam("serviceKey", properties.serviceKey())
							.queryParam("numOfRows", numOfRows)
							.queryParam("pageNo", pageNo)
							.queryParam("_type", "json")
							.build())
					.retrieve()
					.bodyToMono(CultureApiResponse.class)
					.block();
		} catch (RuntimeException e) {
			log.warn("외부 전시 API 호출 실패 (pageNo={}): {}", pageNo, e.getMessage());
			throw new CoreException(ExhibitionErrorCode.EXTERNAL_API_UNAVAILABLE, "외부 전시 API 호출 실패", e);
		}
	}

	private CatalogExhibitionData toData(CultureApiResponse.PerforItem item) {
		return new CatalogExhibitionData(
				item.seq(),
				item.title(),
				blankToNull(item.place()),
				parseDate(item.startDate()),
				parseDate(item.endDate()),
				ExhibitionRegion.fromAreaText(item.area()),
				ExhibitionCategory.fromRealmName(item.realmName()),
				blankToNull(item.thumbnail()),
				blankToNull(item.url()),
				blankToNull(item.serviceName()),
				parseCoordinate(item.gpsX()),
				parseCoordinate(item.gpsY()));
	}

	private static String blankToNull(String value) {
		return value == null || value.isBlank() ? null : value.trim();
	}

	/** YYYYMMDD 8자리만 파싱, 그 외/결측은 null. */
	private static LocalDate parseDate(String value) {
		String text = blankToNull(value);
		if (text == null || text.length() != 8) {
			return null;
		}
		try {
			return LocalDate.parse(text, YYYYMMDD);
		} catch (RuntimeException e) {
			return null;
		}
	}

	private static Double parseCoordinate(String value) {
		String text = blankToNull(value);
		if (text == null) {
			return null;
		}
		try {
			return Double.parseDouble(text);
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
