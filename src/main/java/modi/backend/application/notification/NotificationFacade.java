package modi.backend.application.notification;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import modi.backend.domain.notification.Notification;
import modi.backend.domain.notification.NotificationErrorCode;
import modi.backend.domain.notification.NotificationRepository;
import modi.backend.support.error.CoreException;
import modi.backend.support.response.Cursor;

/**
 * 알림 유스케이스 조율. 상태 변경은 Entity 메서드, Facade는 load·조율·save만.
 * 목록은 (createdAt desc, id desc) 키셋 커서로 페이지네이션한다(정렬 판별자 "latest").
 */
@Service
@RequiredArgsConstructor
public class NotificationFacade {

	/** 커서 정렬 판별자 — 최신순 단일 정렬. */
	private static final String SORT_LATEST = "latest";
	private static final int DEFAULT_SIZE = 20;
	private static final int MAX_SIZE = 50;

	private final NotificationRepository notificationRepository;

	/** 내 알림 목록(최신순, 커서 페이지네이션). 손상된 커서는 Cursor.decode가 INVALID_CURSOR로 처리한다. */
	@Transactional(readOnly = true)
	public NotificationResult.List getNotifications(NotificationCriteria.List criteria) {
		int size = clampSize(criteria.size());
		Optional<Cursor> cursor = Cursor.decode(criteria.cursor(), SORT_LATEST);
		ZonedDateTime cursorCreatedAt = cursor.map(c -> ZonedDateTime.parse(c.key())).orElse(null);
		Long cursorId = cursor.map(Cursor::lastId).orElse(null);

		List<Notification> rows = notificationRepository.findPage(criteria.userId(), cursorCreatedAt, cursorId, size + 1);
		boolean hasNext = rows.size() > size;
		List<Notification> page = hasNext ? rows.subList(0, size) : rows;

		List<NotificationResult.Item> content = page.stream().map(NotificationResult.Item::from).toList();
		String nextCursor = hasNext ? nextCursorOf(page.get(page.size() - 1)) : null;
		long totalCount = notificationRepository.countByUserId(criteria.userId());

		return new NotificationResult.List(content, nextCursor, hasNext, totalCount);
	}

	/** 알림 읽음 처리(멱등). 없거나 타인 알림이면 NOTIFICATION_NOT_FOUND(404). */
	@Transactional
	public NotificationResult.Read markRead(NotificationCriteria.Read criteria) {
		Notification notification = notificationRepository.findById(criteria.notificationId())
				.filter(n -> n.isOwnedBy(criteria.userId()))
				.orElseThrow(() -> new CoreException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));
		notification.markRead();
		return NotificationResult.Read.from(notificationRepository.save(notification));
	}

	private int clampSize(Integer size) {
		if (size == null || size < 1) {
			return DEFAULT_SIZE;
		}
		return Math.min(size, MAX_SIZE);
	}

	private String nextCursorOf(Notification last) {
		return Cursor.of(SORT_LATEST, last.getCreatedAt().toString(), last.getId()).encode();
	}
}
