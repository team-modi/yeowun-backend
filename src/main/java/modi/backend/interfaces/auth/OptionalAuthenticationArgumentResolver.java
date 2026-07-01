package modi.backend.interfaces.auth;

import java.util.Optional;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import modi.backend.application.auth.AuthFacade;
import modi.backend.support.error.CoreException;

/**
 * {@link OptionalAuthentication} 파라미터를 {@code Optional<LoginUser>}로 해석한다.
 * access 토큰을 {@code access_token} 쿠키 → Authorization Bearer 헤더 순으로 읽되, 없거나 무효면
 * {@link CoreException}을 삼키고 {@code Optional.empty()}를 반환한다(공개 엔드포인트라 401로 막지 않는다).
 */
@Component
@RequiredArgsConstructor
public class OptionalAuthenticationArgumentResolver implements HandlerMethodArgumentResolver {

	private static final String BEARER_PREFIX = "Bearer ";

	private final AuthFacade authFacade;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(OptionalAuthentication.class)
				&& Optional.class.equals(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		String token = resolveAccessToken(request);
		if (token == null) {
			return Optional.empty();
		}
		try {
			return Optional.of(LoginUser.from(authFacade.requireAccess(token)));
		} catch (CoreException e) {
			return Optional.empty(); // 무효 토큰은 익명으로 처리(공개 API)
		}
	}

	/** access_token 쿠키 우선, 없으면 Authorization Bearer 헤더. 둘 다 없으면 null(예외 던지지 않음). */
	private String resolveAccessToken(HttpServletRequest request) {
		if (request == null) {
			return null;
		}
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if (AuthCookie.ACCESS.equals(cookie.getName())
						&& cookie.getValue() != null && !cookie.getValue().isBlank()) {
					return cookie.getValue();
				}
			}
		}
		String header = request.getHeader("Authorization");
		if (header != null && header.startsWith(BEARER_PREFIX)) {
			String value = header.substring(BEARER_PREFIX.length()).trim();
			return value.isEmpty() ? null : value;
		}
		return null;
	}
}
