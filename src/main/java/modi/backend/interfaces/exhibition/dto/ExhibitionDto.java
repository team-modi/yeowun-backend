package modi.backend.interfaces.exhibition.dto;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import modi.backend.application.exhibition.ExhibitionResult;

/**
 * 전시 API 요청/응답 DTO 모음(파일 수 절감을 위해 중첩 record로 묶음).
 * 형식 검증은 Bean Validation, 날짜/enum 변환·도메인 규칙은 Controller·Facade·Entity에서 판단한다.
 */
public final class ExhibitionDto {

	private ExhibitionDto() {
	}

	/**
	 * 개인 전시(CUSTOM) 등록 요청. 제목만 필수. 날짜는 {@code YYYY-MM-DD} 문자열로 받아 Controller에서 파싱한다
	 * (파싱 실패 → 400 INVALID_INPUT). region/category 코드 검증은 Facade에서 수행한다.
	 */
	public record CustomCreateRequest(
			@NotBlank @Size(max = 100) String title,
			@Size(max = 200) String place,
			@Schema(example = "2026-06-20") String startDate,
			@Schema(example = "2026-06-30") String endDate,
			@Schema(example = "SEOUL") String region,
			@Schema(example = "PHOTO") String category,
			@Size(max = 2048) String posterUrl) {
	}

	/** 목록 항목(3.3.1 content[]). */
	public record ListItemResponse(Long exhibitionId, String type, String title, String posterUrl,
			LocalDate startDate, LocalDate endDate, String place, String region, String category) {

		public static ListItemResponse from(ExhibitionResult.ListItem result) {
			return new ListItemResponse(result.exhibitionId(), result.type(), result.title(), result.posterUrl(),
					result.startDate(), result.endDate(), result.place(), result.region(), result.category());
		}
	}

	/** 전시 상세(3.3.2). description·operatingHours·price·artists·keywords는 데이터 있을 때만(없으면 null/빈 배열). */
	public record DetailResponse(Long exhibitionId, String type, String title, String posterUrl,
			LocalDate startDate, LocalDate endDate, String place, String region, String category,
			String description, String operatingHours, String price, List<String> artists, List<String> keywords,
			String serviceName, String detailUrl, Double gpsX, Double gpsY) {

		public static DetailResponse from(ExhibitionResult.Detail result) {
			return new DetailResponse(result.exhibitionId(), result.type(), result.title(), result.posterUrl(),
					result.startDate(), result.endDate(), result.place(), result.region(), result.category(),
					result.description(), result.operatingHours(), result.price(), result.artists(),
					result.keywords(), result.serviceName(), result.detailUrl(), result.gpsX(), result.gpsY());
		}
	}

	/** 개인 전시 등록 결과(3.3.3). */
	public record CreatedResponse(Long exhibitionId, String type) {

		public static CreatedResponse from(ExhibitionResult.Created result) {
			return new CreatedResponse(result.exhibitionId(), result.type());
		}
	}
}
