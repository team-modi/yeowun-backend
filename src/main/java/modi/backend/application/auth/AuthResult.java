package modi.backend.application.auth;

import modi.backend.domain.auth.AuthTokens;
import modi.backend.domain.user.AgeGroup;
import modi.backend.domain.user.User;

/**
 * 인증 유스케이스 출력 모음. (Facade는 Result까지만)
 */
public final class AuthResult {

	private AuthResult() {
	}

	/**
	 * 로그인/재발급 출력. provider = 이번 로그인 provider, email = 해당 소셜계정 이메일(없으면 null).
	 * 소셜 동의항목에서 받은 이름(name)·연령대(ageGroup)·출생연도(birthYear)도 함께 내린다(미동의/미지원 시 null).
	 */
	public record Login(Long userId, String nickname, String name, boolean profileCompleted, String provider,
						String email, String ageGroup, Integer birthYear, String accessToken, String refreshToken) {

		public static Login of(User user, String provider, String email, AuthTokens tokens) {
			return new Login(user.getId(), user.getNickname(), user.getName(), user.isProfileCompleted(), provider,
					email, ageGroupCode(user.getAgeGroup()), user.getBirthYear(),
					tokens.accessToken(), tokens.refreshToken());
		}

		/** UNSPECIFIED(미입력/미동의)는 null로 내려 프론트가 "미설정"으로 다룰 수 있게 한다. */
		private static String ageGroupCode(AgeGroup ageGroup) {
			return ageGroup == null || ageGroup.isUnspecified() ? null : ageGroup.name();
		}
	}
}
