package modi.backend.interfaces;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.JsonPath;

import modi.backend.TestcontainersConfiguration;
import modi.backend.infra.auth.KakaoApi;

/**
 * 전시관 검색(전시 5.5) API end-to-end 검증.
 * 외부 카카오 HTTP({@link KakaoApi})만 목으로 두고, 로그인으로 실제 토큰을 발급받아
 * GET /api/v1/venues 를 실제 컨트롤러·DB(Testcontainers)로 태운다.
 * 시드 전시관은 V13 마이그레이션에서 삽입되므로 Testcontainers DB에 존재한다.
 */
@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class VenueIntegrationTest {

	private static final String REDIRECT_URI = "http://localhost:3000/login"; // application.yaml 화이트리스트

	@Autowired
	MockMvc mockMvc;

	@MockitoBean
	KakaoApi kakaoApi;

	/** 카카오 로그인으로 실제 access 토큰을 발급받는다(providerUserId로 유저 격리). */
	private String loginAndGetAccessToken(long providerUserId, String nickname) throws Exception {
		given(kakaoApi.getToken(any())).willReturn(Map.of("access_token", "kakao-access-token"));
		given(kakaoApi.getUserInfo(anyString())).willReturn(Map.of(
				"id", providerUserId,
				"kakao_account", Map.of("profile", Map.of("nickname", nickname))));
		MvcResult login = mockMvc.perform(post("/api/v1/auth/login/kakao")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"code\":\"auth-code\",\"redirectUri\":\"" + REDIRECT_URI + "\"}"))
				.andExpect(status().isOk())
				.andReturn();
		return JsonPath.read(login.getResponse().getContentAsString(), "$.data.accessToken");
	}

	@Test
	@DisplayName("GET /venues?keyword=아 — 로그인 사용자, 200 + '아'로 시작하는 시드 전시관(≤20, 필드 존재)")
	void 전시관_검색_정상() throws Exception {
		// Arrange
		String accessToken = loginAndGetAccessToken(7000001L, "검색유저");

		// Act & Assert
		mockMvc.perform(get("/api/v1/venues").param("keyword", "아")
						.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.meta.result").value("SUCCESS"))
				.andExpect(jsonPath("$.data.venues").isArray())
				.andExpect(jsonPath("$.data.venues", Matchers.not(Matchers.empty())))
				.andExpect(jsonPath("$.data.venues.length()", Matchers.lessThanOrEqualTo(20)))
				// 시드에 '아리랑 문화관' 존재
				.andExpect(jsonPath("$.data.venues[*].name", Matchers.hasItem("아리랑 문화관")))
				.andExpect(jsonPath("$.data.venues[0].venueId").isNumber())
				.andExpect(jsonPath("$.data.venues[0].name").isString())
				.andExpect(jsonPath("$.data.venues[0].region").value("SEOUL"));
	}

	@Test
	@DisplayName("GET /venues?keyword= — 빈 키워드, 200 + venues 빈 배열")
	void 빈_키워드_빈배열() throws Exception {
		// Arrange
		String accessToken = loginAndGetAccessToken(7000002L, "빈키워드유저");

		// Act & Assert
		mockMvc.perform(get("/api/v1/venues").param("keyword", "")
						.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.meta.result").value("SUCCESS"))
				.andExpect(jsonPath("$.data.venues").isArray())
				.andExpect(jsonPath("$.data.venues").isEmpty());
	}

	@Test
	@DisplayName("GET /venues — 키워드 없음(미입력), 200 + venues 빈 배열")
	void 키워드_미입력_빈배열() throws Exception {
		// Arrange
		String accessToken = loginAndGetAccessToken(7000003L, "무키워드유저");

		// Act & Assert
		mockMvc.perform(get("/api/v1/venues")
						.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.venues").isArray())
				.andExpect(jsonPath("$.data.venues").isEmpty());
	}

	@Test
	@DisplayName("미인증 — 401 NO_ACCESS_TOKEN")
	void 미인증_401() throws Exception {
		mockMvc.perform(get("/api/v1/venues").param("keyword", "아"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.meta.result").value("FAIL"))
				.andExpect(jsonPath("$.meta.errorCode").value("NO_ACCESS_TOKEN"));
	}
}
