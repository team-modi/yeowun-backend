package modi.backend.interfaces.bookmark;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import modi.backend.interfaces.auth.LoginUser;
import modi.backend.interfaces.bookmark.dto.BookmarkDto;
import modi.backend.interfaces.common.dto.CursorResponse;
import modi.backend.interfaces.exhibition.dto.ExhibitionDto;

/**
 * 관심 전시(북마크) API Swagger 스펙(북마크 6.1~6.3). MVC 어노테이션은 {@link BookmarkV1Controller}.
 * 등록/해제는 멱등이며, 목록은 전시 목록과 동일 항목 스키마(bookmarked 항상 true)를 커서로 반환한다.
 */
@Tag(name = "Bookmark", description = "관심 전시(북마크) 등록/해제/목록. 모두 인증 필수.")
public interface BookmarkV1ApiSpec {

	@Operation(summary = "관심 전시 등록", description = "전시를 관심 등록한다(멱등 — 이미 등록돼 있어도 200). access 토큰 필요.")
	@SecurityRequirement(name = "bearerAuth")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "등록 성공(멱등)", content = @Content(
					mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = BookmarkDto.ToggleResponse.class),
					examples = @ExampleObject(value = """
							{
							  "meta": { "result": "SUCCESS", "errorCode": null, "message": null },
							  "data": { "exhibitionId": 51, "bookmarked": true }
							}
							"""))),
			@ApiResponse(responseCode = "404", description = "NOT_FOUND — 없는 전시", content = @Content(
					mediaType = MediaType.APPLICATION_JSON_VALUE,
					examples = @ExampleObject(value = """
							{ "meta": { "result": "FAIL", "errorCode": "NOT_FOUND", "message": "요청한 전시를 찾을 수 없습니다." }, "data": null }
							"""))),
			@ApiResponse(responseCode = "401", description = "UNAUTHORIZED — 미인증", content = @Content(
					mediaType = MediaType.APPLICATION_JSON_VALUE)),
	})
	ResponseEntity<modi.backend.support.response.ApiResponse<BookmarkDto.ToggleResponse>> add(
			@Parameter(description = "전시 ID", example = "51") Long exhibitionId,
			@Parameter(hidden = true) LoginUser user);

	@Operation(summary = "관심 전시 해제", description = "전시 관심을 해제한다(멱등 — 이미 해제 상태여도 200). access 토큰 필요.")
	@SecurityRequirement(name = "bearerAuth")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "해제 성공(멱등)", content = @Content(
					mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = BookmarkDto.ToggleResponse.class),
					examples = @ExampleObject(value = """
							{
							  "meta": { "result": "SUCCESS", "errorCode": null, "message": null },
							  "data": { "exhibitionId": 51, "bookmarked": false }
							}
							"""))),
			@ApiResponse(responseCode = "404", description = "NOT_FOUND — 없는 전시", content = @Content(
					mediaType = MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = "401", description = "UNAUTHORIZED — 미인증", content = @Content(
					mediaType = MediaType.APPLICATION_JSON_VALUE)),
	})
	ResponseEntity<modi.backend.support.response.ApiResponse<BookmarkDto.ToggleResponse>> remove(
			@Parameter(description = "전시 ID", example = "51") Long exhibitionId,
			@Parameter(hidden = true) LoginUser user);

	@Operation(summary = "관심 전시 목록", description = """
			프로필 > 관심 전시. 전시 목록과 동일한 항목 스키마(bookmarked 항상 true)를 커서 페이지네이션으로 반환한다.
			sort=latest(등록 최신순, 기본) | ending(종료 임박순). access 토큰 필요.""")
	@SecurityRequirement(name = "bearerAuth")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(
					mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = CursorResponse.class),
					examples = @ExampleObject(value = """
							{
							  "meta": { "result": "SUCCESS", "errorCode": null, "message": null },
							  "data": {
							    "content": [
							      {
							        "exhibitionId": 51, "type": "CATALOG", "title": "희박한 공기",
							        "posterUrl": null, "startDate": "2026-07-01", "endDate": "2026-08-15",
							        "place": "동작아트갤러리", "region": "SEOUL", "category": "PAINTING",
							        "artistSummary": null, "dDay": 5, "free": true, "bookmarked": true
							      }
							    ],
							    "nextCursor": null, "hasNext": false, "totalCount": 10
							  }
							}
							"""))),
			@ApiResponse(responseCode = "400", description = "INVALID_CURSOR — 정렬 변경 후 이전 커서 재사용 등 커서-조건 불일치",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = "401", description = "UNAUTHORIZED — 미인증", content = @Content(
					mediaType = MediaType.APPLICATION_JSON_VALUE)),
	})
	ResponseEntity<modi.backend.support.response.ApiResponse<CursorResponse<ExhibitionDto.ListItemResponse>>> list(
			@Parameter(description = "정렬. latest=등록 최신순(기본), ending=종료 임박순", example = "latest",
					schema = @Schema(allowableValues = { "latest", "ending" })) String sort,
			@Parameter(description = "다음 페이지 조회용 opaque 커서(첫 페이지는 생략)") String cursor,
			@Parameter(description = "페이지 크기(기본 20, 최대 50)", example = "20") Integer size,
			@Parameter(hidden = true) LoginUser user);
}
