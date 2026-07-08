package modi.backend.application.venue;

/**
 * 전시관 유스케이스 입력 모음. (Request →[Controller] Criteria → Facade)
 */
public final class VenueCriteria {

	private VenueCriteria() {
	}

	/** 전시관명 자동완성 검색 입력. keyword 공백/미입력이면 빈 결과(에러 아님). */
	public record Search(String keyword) {
	}
}
