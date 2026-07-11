package modi.backend.interfaces.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import modi.backend.application.auth.AuthFacade;
import modi.backend.config.AdminProperties;
import modi.backend.domain.auth.AuthErrorCode;
import modi.backend.domain.auth.TokenClaims;
import modi.backend.support.error.CoreException;
import modi.backend.support.error.ErrorType;

/** 관리자 게이트 인터셉터 — 토큰/화이트리스트에 따른 통과·401·403 순수 단위 검증(Mockito). */
class AdminAuthInterceptorTest {

	private final AuthFacade authFacade = mock(AuthFacade.class);
	private final AdminProperties adminProperties = new AdminProperties(List.of(1L));
	private final AdminAuthInterceptor interceptor = new AdminAuthInterceptor(authFacade, adminProperties);
	private final MockHttpServletResponse response = new MockHttpServletResponse();
	private final Object handler = new Object();

	private static TokenClaims claims(long userId) {
		return new TokenClaims(userId, "access", "kakao", "nick", true);
	}

	@Test
	@DisplayName("토큰이 없으면 NO_ACCESS_TOKEN(401)")
	void 토큰없음() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		assertThatThrownBy(() -> interceptor.preHandle(request, response, handler))
				.isInstanceOfSatisfying(CoreException.class,
						e -> assertThat(e.errorCode()).isEqualTo(AuthErrorCode.NO_ACCESS_TOKEN));
	}

	@Test
	@DisplayName("관리자 화이트리스트 유저는 통과")
	void 관리자_통과() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", "Bearer good");
		when(authFacade.requireAccess("good")).thenReturn(claims(1L));

		assertThat(interceptor.preHandle(request, response, handler)).isTrue();
	}

	@Test
	@DisplayName("유효 토큰이지만 관리자 아니면 FORBIDDEN(403)")
	void 비관리자_거부() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", "Bearer good");
		when(authFacade.requireAccess("good")).thenReturn(claims(999L));

		assertThatThrownBy(() -> interceptor.preHandle(request, response, handler))
				.isInstanceOfSatisfying(CoreException.class,
						e -> assertThat(e.errorCode()).isEqualTo(ErrorType.FORBIDDEN));
	}
}
