package modi.backend.interfaces.admin;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import modi.backend.application.auth.AuthFacade;
import modi.backend.config.AdminProperties;
import modi.backend.domain.auth.AuthErrorCode;
import modi.backend.interfaces.auth.AccessTokens;
import modi.backend.support.error.CoreException;
import modi.backend.support.error.ErrorType;

/**
 * `/api-admin/**` 접근 통제. role 개념이 없으므로 관리자 유저ID 화이트리스트로 게이트한다.
 * 인터셉터(필터 아님)라 여기서 던진 예외는 전역 예외 핸들러가 처리한다(401/403 정상 응답).
 * - 토큰 없음/무효 → 401, 유효하지만 관리자 아님 → 403.
 */
@Component
@RequiredArgsConstructor
public class AdminAuthInterceptor implements HandlerInterceptor {

	private final AuthFacade authFacade;
	private final AdminProperties adminProperties;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String token = AccessTokens.resolveOrNull(request);
		if (token == null) {
			throw new CoreException(AuthErrorCode.NO_ACCESS_TOKEN);
		}
		Long userId = authFacade.requireAccess(token).userId(); // 무효 시 INVALID_ACCESS_TOKEN(401)
		if (!adminProperties.isAdmin(userId)) {
			throw new CoreException(ErrorType.FORBIDDEN);
		}
		return true;
	}
}
