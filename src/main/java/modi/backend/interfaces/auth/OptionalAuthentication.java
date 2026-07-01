package modi.backend.interfaces.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 선택적 인증 주입 마커. 공개 API지만 로그인 시 사용자 맥락이 필요한 경우 사용한다(예: 전시 목록의 CUSTOM 노출).
 * 토큰이 없거나 무효면 예외 대신 {@code Optional.empty()}로 주입한다(공개 엔드포인트 유지).
 * (해석은 {@link OptionalAuthenticationArgumentResolver})
 * <pre>public X handler(@OptionalAuthentication Optional&lt;LoginUser&gt; user) { ... }</pre>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface OptionalAuthentication {
}
