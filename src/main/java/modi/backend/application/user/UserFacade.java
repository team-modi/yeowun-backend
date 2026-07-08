package modi.backend.application.user;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import modi.backend.domain.auth.RefreshTokenStore;
import modi.backend.domain.bookmark.ExhibitionBookmarkRepository;
import modi.backend.domain.user.AgeGroup;
import modi.backend.domain.user.ResidenceRegion;
import modi.backend.domain.user.User;
import modi.backend.domain.user.UserErrorCode;
import modi.backend.domain.user.UserRepository;
import modi.backend.support.error.CoreException;

/**
 * 사용자 프로필 유스케이스 조율. 상태 변경은 Entity 메서드, Facade는 load·조율·save만.
 * 취향 키워드 집계는 Exhibition 도메인 도입 전까지 스텁([])으로 둔다.
 */
@Service
@RequiredArgsConstructor
public class UserFacade {

	private final UserRepository userRepository;
	private final ExhibitionBookmarkRepository exhibitionBookmarkRepository;
	private final RefreshTokenStore refreshTokenStore;

	/** 내 프로필 + 취향 키워드 + 활동 통계 조회. bookmarkCount는 실집계, recordCount·exhibitionCount는 record 도메인 도입 전까지 0 스텁. */
	@Transactional(readOnly = true)
	public UserResult.Me getProfile(UserCriteria.Me criteria) {
		User user = userRepository.findById(criteria.userId())
				.orElseThrow(() -> new CoreException(UserErrorCode.USER_NOT_FOUND));
		long bookmarkCount = exhibitionBookmarkRepository.countByUserId(user.getId());
		return UserResult.Me.of(user, criteria.provider(), List.of(),
				UserResult.Stats.ofBookmarkCount(bookmarkCount));
	}

	/** 알림 설정 조회. */
	@Transactional(readOnly = true)
	public UserResult.NotificationSettings getNotificationSettings(UserCriteria.Me criteria) {
		User user = userRepository.findById(criteria.userId())
				.orElseThrow(() -> new CoreException(UserErrorCode.USER_NOT_FOUND));
		return UserResult.NotificationSettings.from(user);
	}

	/** 알림 설정 수정(리마인드·공지 수신 여부 전체 갱신). */
	@Transactional
	public UserResult.NotificationSettings updateNotificationSettings(UserCriteria.NotificationUpdate criteria) {
		User user = userRepository.findById(criteria.userId())
				.orElseThrow(() -> new CoreException(UserErrorCode.USER_NOT_FOUND));
		user.updateNotificationSettings(criteria.remindEnabled(), criteria.noticeEnabled());
		return UserResult.NotificationSettings.from(userRepository.save(user));
	}

	/** 회원 탈퇴: soft-delete 후 refresh 토큰 무효화. 이미 탈퇴한 사용자는 조회되지 않아 USER_NOT_FOUND. */
	@Transactional
	public void withdraw(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new CoreException(UserErrorCode.USER_NOT_FOUND));
		user.delete();
		userRepository.save(user);
		refreshTokenStore.remove(userId);
	}

	/** 프로필 부분 갱신(전달된 필드만). enum 필드는 여기서 검증·변환한 뒤 Entity에 위임. */
	@Transactional
	public UserResult.Profile updateProfile(UserCriteria.ProfileUpdate criteria) {
		User user = userRepository.findById(criteria.userId())
				.orElseThrow(() -> new CoreException(UserErrorCode.USER_NOT_FOUND));
		AgeGroup ageGroup = criteria.ageGroup() == null ? null : AgeGroup.from(criteria.ageGroup());
		ResidenceRegion region = criteria.residenceRegion() == null ? null
				: ResidenceRegion.from(criteria.residenceRegion());
		user.updateProfile(criteria.nickname(), criteria.profileImageUrl(), ageGroup, region,
				criteria.residenceDistrict());
		return UserResult.Profile.from(userRepository.save(user), criteria.provider());
	}
}
