package modi.backend.domain.venue;

import org.springframework.http.HttpStatus;

import modi.backend.support.error.ErrorCode;

/**
 * 전시관 도메인 에러. 메시지는 여기 한 곳에서 정의한다.
 */
public enum VenueErrorCode implements ErrorCode {

	VENUE_NOT_FOUND(HttpStatus.NOT_FOUND, "VENUE_NOT_FOUND", "요청한 전시관을 찾을 수 없습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;

	VenueErrorCode(HttpStatus status, String code, String message) {
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
