package modi.backend.application.bookmark;

/**
 * 관심 전시(북마크) 유스케이스 입력 모음. (Request →[Controller] Criteria → Facade)
 * userId는 인증(필수)에서 채운다.
 */
public final class BookmarkCriteria {

	private BookmarkCriteria() {
	}

	/** 관심 전시 토글(등록/해제) 입력. */
	public record Toggle(Long userId, Long exhibitionId) {
	}

	/**
	 * 관심 전시 목록 조회 입력(커서 페이지네이션).
	 *
	 * @param sort   latest(등록 최신순, 기본) | ending(종료 임박순)
	 * @param cursor opaque 커서(첫 페이지는 null)
	 * @param size   페이지 크기(기본 20, 최대 50)
	 */
	public record List(Long userId, String sort, String cursor, Integer size) {
	}
}
