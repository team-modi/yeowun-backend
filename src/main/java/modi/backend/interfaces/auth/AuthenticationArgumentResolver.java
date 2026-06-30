package modi.backend.interfaces.auth;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import modi.backend.support.error.CoreException;
import modi.backend.support.error.ErrorType;

@Component
public class AuthenticationArgumentResolver implements HandlerMethodArgumentResolver {

	private static final String DEV_USER_ID_HEADER = "X-User-Id";

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(Authentication.class)
				&& LoginUser.class.equals(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
		String userId = webRequest.getHeader(DEV_USER_ID_HEADER);
		if (userId == null || userId.isBlank()) {
			throw new CoreException(ErrorType.UNAUTHORIZED, "X-User-Id 헤더가 필요합니다.");
		}
		try {
			return new LoginUser(Long.parseLong(userId), "DEV", "dev-user", true);
		} catch (NumberFormatException e) {
			throw new CoreException(ErrorType.UNAUTHORIZED, "X-User-Id 헤더가 올바르지 않습니다.");
		}
	}
}
