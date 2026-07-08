package modi.backend.domain.notification;

import org.springframework.http.HttpStatus;

import modi.backend.support.error.ErrorCode;

/**
 * 알림 도메인 에러. 메시지는 여기 한 곳에서 정의한다. (09_알림.md 에러 표)
 * 공통 성격(INVALID_CURSOR·UNAUTHORIZED)은 {@link modi.backend.support.error.ErrorType} 재사용.
 */
public enum NotificationErrorCode implements ErrorCode {

	NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION_NOT_FOUND", "알림을 찾을 수 없습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;

	NotificationErrorCode(HttpStatus status, String code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}

	@Override
	public String code() {
		return code;
	}

	@Override
	public String message() {
		return message;
	}

	@Override
	public HttpStatus getStatus() {
		return status;
	}
}
