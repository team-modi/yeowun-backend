package modi.backend.application.notification;

import java.time.LocalDateTime;

import modi.backend.domain.notification.Notification;

/**
 * 알림 유스케이스 출력 모음. (Facade는 Result까지만) 커서 페이지네이션 봉투 변환은 Interface(DTO)에서 한다.
 */
public final class NotificationResult {

	private NotificationResult() {
	}

	/** 알림 목록 결과 — 커서 페이지네이션 shape(content·nextCursor·hasNext·totalCount). */
	public record List(java.util.List<Item> content, String nextCursor, boolean hasNext, long totalCount) {
	}

	/** 알림 한 건(목록 항목). type은 문자열, createdAt은 LocalDateTime로 내린다. */
	public record Item(Long notificationId, String type, String title, String body,
			Long targetId, boolean read, LocalDateTime createdAt) {

		public static Item from(Notification notification) {
			return new Item(notification.getId(), notification.getType().name(), notification.getTitle(),
					notification.getBody(), notification.getTargetId(), notification.isRead(),
					notification.getCreatedAt().toLocalDateTime());
		}
	}

	/** 읽음 처리 결과. */
	public record Read(Long notificationId, boolean read) {

		public static Read from(Notification notification) {
			return new Read(notification.getId(), notification.isRead());
		}
	}
}
