package modi.backend.interfaces.bookmark;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import modi.backend.application.bookmark.BookmarkCriteria;
import modi.backend.application.bookmark.BookmarkFacade;
import modi.backend.application.bookmark.BookmarkResult;
import modi.backend.interfaces.auth.Authentication;
import modi.backend.interfaces.auth.LoginUser;
import modi.backend.interfaces.bookmark.dto.BookmarkDto;
import modi.backend.interfaces.common.dto.CursorResponse;
import modi.backend.interfaces.exhibition.dto.ExhibitionDto;
import modi.backend.support.response.ApiResponse;

/**
 * 관심 전시(북마크) API(북마크 6.1~6.3). 등록/해제는 전시 리소스 하위 경로, 목록은 내 프로필 하위 경로 —
 * 서로 다른 리소스 경로를 한 컨트롤러에 메서드별 전체 경로로 매핑한다(클래스 레벨 @RequestMapping 없음).
 * 모두 인증 필수. (컨벤션: 성공 200)
 */
@RestController
@RequiredArgsConstructor
public class BookmarkV1Controller implements BookmarkV1ApiSpec {

	private final BookmarkFacade bookmarkFacade;

	/** 관심 등록(6.1, 멱등). 없는 전시면 404. */
	@Override
	@PostMapping("/api/v1/exhibitions/{exhibitionId}/bookmark")
	public ResponseEntity<ApiResponse<BookmarkDto.ToggleResponse>> add(
			@PathVariable Long exhibitionId,
			@Authentication LoginUser user) {
		BookmarkResult.Toggle result = bookmarkFacade.add(
				new BookmarkCriteria.Toggle(user.userId(), exhibitionId));
		return ResponseEntity.ok(ApiResponse.success(BookmarkDto.ToggleResponse.from(result)));
	}

	/** 관심 해제(6.2, 멱등). 없는 전시면 404. */
	@Override
	@DeleteMapping("/api/v1/exhibitions/{exhibitionId}/bookmark")
	public ResponseEntity<ApiResponse<BookmarkDto.ToggleResponse>> remove(
			@PathVariable Long exhibitionId,
			@Authentication LoginUser user) {
		BookmarkResult.Toggle result = bookmarkFacade.remove(
				new BookmarkCriteria.Toggle(user.userId(), exhibitionId));
		return ResponseEntity.ok(ApiResponse.success(BookmarkDto.ToggleResponse.from(result)));
	}

	/** 관심 전시 목록(6.3). sort=latest|ending + 커서 페이지네이션. */
	@Override
	@GetMapping("/api/v1/users/me/bookmarks")
	public ResponseEntity<ApiResponse<CursorResponse<ExhibitionDto.ListItemResponse>>> list(
			@RequestParam(defaultValue = "latest") String sort,
			@RequestParam(required = false) String cursor,
			@RequestParam(required = false) Integer size,
			@Authentication LoginUser user) {
		BookmarkResult.ListPage result = bookmarkFacade.list(
				new BookmarkCriteria.List(user.userId(), sort, cursor, size));
		CursorResponse<ExhibitionDto.ListItemResponse> data = CursorResponse.of(
				result.content().stream().map(ExhibitionDto.ListItemResponse::from).toList(),
				result.nextCursor(), result.hasNext(), result.totalCount());
		return ResponseEntity.ok(ApiResponse.success(data));
	}
}
