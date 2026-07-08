package modi.backend.interfaces.user.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import modi.backend.application.user.UserResult;

/**
 * 사용자 API의 요청/응답 DTO 모음. (파일 수 절감을 위해 중첩 record로 묶음)
 * 형식 검증은 Bean Validation, 도메인 규칙(닉네임·거주지역)은 Entity/enum에서 판단한다.
 */
public final class UserDto {

	private UserDto() {
	}

	/**
	 * 프로필 수정 요청(부분 갱신) — 포함된 필드만 갱신, 미포함(null)은 기존 값 유지.
	 * 닉네임 RULE·연령대/거주지역 코드 검증은 도메인 계층에서 수행한다.
	 */
	public record ProfileRequest(String nickname, String profileImageUrl, String ageGroup,
			String residenceRegion, String residenceDistrict) {
	}

	/** 프로필 수정 응답. profileCompleted=true가 되면 FE는 온보딩을 종료한다. */
	public record ProfileResponse(Long userId, String provider, String nickname, boolean profileCompleted,
			String profileImageUrl, String ageGroup, Integer birthYear,
			String residenceRegion, String residenceDistrict) {

		public static ProfileResponse from(UserResult.Profile result) {
			return new ProfileResponse(result.userId(), result.provider(), result.nickname(),
					result.profileCompleted(), result.profileImageUrl(), result.ageGroup(), result.birthYear(),
					result.residenceRegion(), result.residenceDistrict());
		}
	}

	/** 프로필 조회 응답 — 프로필 + 취향 키워드 + 활동 통계(2.3.1). */
	public record MeResponse(Long userId, String provider, String nickname, String profileImageUrl,
			String ageGroup, Integer birthYear, String residenceRegion, String residenceDistrict,
			List<String> tasteKeywords, Stats stats) {

		public record Stats(long recordCount, long exhibitionCount, long bookmarkCount) {
		}

		public static MeResponse from(UserResult.Me result) {
			UserResult.Stats s = result.stats();
			return new MeResponse(result.userId(), result.provider(), result.nickname(),
					result.profileImageUrl(), result.ageGroup(), result.birthYear(), result.residenceRegion(),
					result.residenceDistrict(), result.tasteKeywords(),
					new Stats(s.recordCount(), s.exhibitionCount(), s.bookmarkCount()));
		}
	}

	/**
	 * 알림 설정 수정 요청(4.4) — 두 필드 모두 필수. 누락 시 Bean Validation이 INVALID_INPUT(400)으로 매핑한다.
	 * (원시 boolean은 미포함을 false로 흡수하므로 필수 검증을 위해 Boolean 사용.)
	 */
	public record NotificationSettingsRequest(@NotNull Boolean remindEnabled, @NotNull Boolean noticeEnabled) {
	}

	/** 알림 설정 조회·수정 응답(4.3·4.4). */
	public record NotificationSettingsResponse(boolean remindEnabled, boolean noticeEnabled) {

		public static NotificationSettingsResponse from(UserResult.NotificationSettings result) {
			return new NotificationSettingsResponse(result.remindEnabled(), result.noticeEnabled());
		}
	}
}
