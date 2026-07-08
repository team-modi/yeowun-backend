package modi.backend.interfaces.bookmark.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import modi.backend.application.bookmark.BookmarkResult;

/**
 * 관심 전시(북마크) API 응답 DTO 모음. 목록은 전시 목록과 동일 스키마({@code ExhibitionDto.ListItemResponse})를
 * {@code CursorResponse}로 감싸 재사용하므로 여기엔 토글 응답만 둔다.
 */
public final class BookmarkDto {

	private BookmarkDto() {
	}

	/** 토글(등록/해제) 응답 — { exhibitionId, bookmarked }. */
	public record ToggleResponse(
			@Schema(description = "대상 전시 ID", example = "51") Long exhibitionId,
			@Schema(description = "현재 관심 등록 여부(등록 true / 해제 false)", example = "true") boolean bookmarked) {

		public static ToggleResponse from(BookmarkResult.Toggle result) {
			return new ToggleResponse(result.exhibitionId(), result.bookmarked());
		}
	}
}
