package modi.backend.application.notification;

/**
 * 알림 유스케이스 입력 모음. (Request →[Controller] Criteria → Facade)
 */
public final class NotificationCriteria {

	private NotificationCriteria() {
	}

	/** 알림 목록 조회 입력. userId는 인증에서, cursor·size는 요청 쿼리에서 채운다(size 미지정 시 기본값 적용). */
	public record List(Long userId, String cursor, Integer size) {
	}

	/** 알림 읽음 처리 입력. userId는 인증에서, notificationId는 경로에서 채운다. */
	public record Read(Long userId, Long notificationId) {
	}
}
