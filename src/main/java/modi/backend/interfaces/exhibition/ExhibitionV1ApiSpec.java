package modi.backend.interfaces.exhibition;

import java.util.Optional;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import modi.backend.interfaces.auth.LoginUser;
import modi.backend.interfaces.exhibition.dto.ExhibitionDto;
import modi.backend.interfaces.record.dto.PageResponse;
import modi.backend.support.response.ApiResponse;

/**
 * 전시 API Swagger 스펙(03_전시.md). MVC 어노테이션은 {@link ExhibitionV1Controller}.
 */
@Tag(name = "Exhibition", description = "전시 목록/탐색 · 상세 · 개인 전시 등록")
public interface ExhibitionV1ApiSpec {

	@Operation(summary = "전시 목록/탐색", description = """
			필터·정렬·페이지로 전시를 조회한다. 필터 미지정 시 오늘 진행 중인 전시를 기본 노출한다.
			비로그인은 CATALOG만, 로그인은 CATALOG + 본인 CUSTOM을 함께 본다. 인증 선택(토큰 있으면 반영).""")
	ResponseEntity<ApiResponse<PageResponse<ExhibitionDto.ListItemResponse>>> list(
			@Parameter(description = "전시명·전시장명 부분 일치", example = "모네") String keyword,
			@Parameter(description = "해당 날짜에 진행 중인 전시(YYYY-MM-DD)", example = "2026-06-30") String date,
			@Parameter(description = "지역 코드(SEOUL·GYEONGGI·BUSAN·DAEGU·ETC)", example = "SEOUL") String region,
			@Parameter(description = "카테고리 코드(PAINTING·PHOTO·MEDIA·SCULPTURE·ETC)", example = "PHOTO") String category,
			@ParameterObject Pageable pageable,
			@Parameter(hidden = true) Optional<LoginUser> loginUser);

	@Operation(summary = "전시 상세", description = "CATALOG는 공개, CUSTOM은 등록자 본인만 조회 가능. 인증 선택.")
	ResponseEntity<ApiResponse<ExhibitionDto.DetailResponse>> detail(
			@Parameter(description = "전시 ID", example = "51") Long exhibitionId,
			@Parameter(hidden = true) Optional<LoginUser> loginUser);

	@Operation(summary = "개인 전시 등록", description = "카탈로그에 없는 개인 전시를 직접 등록한다. access 토큰 필요.")
	@SecurityRequirement(name = "bearerAuth")
	ResponseEntity<ApiResponse<ExhibitionDto.CreatedResponse>> registerCustom(
			@Parameter(hidden = true) LoginUser user,
			ExhibitionDto.CustomCreateRequest request);
}
