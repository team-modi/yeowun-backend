package modi.backend.interfaces.venue;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import modi.backend.interfaces.auth.LoginUser;
import modi.backend.interfaces.venue.dto.VenueDto;
import modi.backend.support.response.ApiResponse;

/**
 * 전시관 API Swagger 스펙. (MVC 어노테이션은 {@link VenueV1Controller})
 */
@Tag(name = "Venue", description = "전시관 검색(자동완성)")
public interface VenueV1ApiSpec {

	@Operation(summary = "전시관 검색(자동완성)",
			description = "전시관명 부분 일치(대소문자 무시) 자동완성. 상위 20개 고정. "
					+ "keyword 공백/미입력이면 빈 목록. access 토큰 필요.")
	@SecurityRequirement(name = "bearerAuth")
	ResponseEntity<ApiResponse<VenueDto.SearchResponse>> search(
			@Parameter(hidden = true) LoginUser user,
			@Parameter(description = "전시관명 검색어(부분 일치, 대소문자 무시)", example = "아") String keyword);
}
