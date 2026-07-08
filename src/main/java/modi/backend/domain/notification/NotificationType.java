package modi.backend.domain.notification;

/**
 * 알림 종류. REMIND=리마인드(오늘의 여운, targetId=remindId) · NOTICE=공지(targetId=null).
 */
public enum NotificationType {
	REMIND,
	NOTICE
}
