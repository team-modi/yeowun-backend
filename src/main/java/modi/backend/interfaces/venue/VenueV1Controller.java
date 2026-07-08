package modi.backend.interfaces.venue;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import modi.backend.application.venue.VenueCriteria;
import modi.backend.application.venue.VenueFacade;
import modi.backend.application.venue.VenueResult;
import modi.backend.interfaces.auth.Authentication;
import modi.backend.interfaces.auth.LoginUser;
import modi.backend.interfaces.venue.dto.VenueDto;
import modi.backend.support.response.ApiResponse;

/**
 * 전시관 검색 API(전시 5.5). 개인 전시 직접 등록 시 전시관명 자동완성.
 * (컨벤션: 성공 200, 인증 필요 — 대상 유저는 @Authentication으로 주입.)
 */
@RestController
@RequestMapping("/api/v1/venues")
@RequiredArgsConstructor
public class VenueV1Controller implements VenueV1ApiSpec {

	private final VenueFacade venueFacade;

	/** 전시관명 자동완성(상위 20개). keyword 공백/미입력이면 빈 목록. */
	@Override
	@GetMapping
	public ResponseEntity<ApiResponse<VenueDto.SearchResponse>> search(
			@Authentication LoginUser user,
			@RequestParam(name = "keyword", required = false) String keyword) {
		VenueResult.Search result = venueFacade.search(new VenueCriteria.Search(keyword));
		return ResponseEntity.ok(ApiResponse.success(VenueDto.SearchResponse.from(result)));
	}
}
