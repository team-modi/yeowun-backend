package modi.backend.infra.notification;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import modi.backend.domain.notification.Notification;
import modi.backend.domain.notification.NotificationRepository;

/**
 * {@link NotificationRepository} 어댑터(DIP). Spring Data로 위임하며, 조회는 살아있는 행만 본다.
 */
@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {

	private final NotificationJpaRepository jpaRepository;

	@Override
	public Optional<Notification> findById(Long id) {
		return jpaRepository.findByIdAndDeletedAtIsNull(id);
	}

	@Override
	public Notification save(Notification notification) {
		return jpaRepository.save(notification);
	}

	@Override
	public List<Notification> findPage(Long userId, ZonedDateTime cursorCreatedAt, Long cursorId, int limitPlusOne) {
		PageRequest limit = PageRequest.of(0, limitPlusOne);
		if (cursorCreatedAt == null) {
			return jpaRepository.findFirstPage(userId, limit);
		}
		return jpaRepository.findPageAfterCursor(userId, cursorCreatedAt, cursorId, limit);
	}

	@Override
	public long countByUserId(Long userId) {
		return jpaRepository.countByUserIdAndDeletedAtIsNull(userId);
	}
}
