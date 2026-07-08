package modi.backend.domain.remind;

import org.springframework.http.HttpStatus;

import modi.backend.support.error.ErrorCode;

public enum RemindErrorCode implements ErrorCode {

	REMIND_NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND", "리마인드를 찾을 수 없습니다."),
	FORBIDDEN_REMIND(HttpStatus.FORBIDDEN, "FORBIDDEN", "리마인드에 접근할 권한이 없습니다."),
	INVALID_REMIND_INPUT(HttpStatus.BAD_REQUEST, "INVALID_INPUT", "리마인드 입력값이 올바르지 않습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;

	RemindErrorCode(HttpStatus status, String code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}

	@Override
	public HttpStatus getStatus() {
		return status;
	}

	@Override
	public String code() {
		return code;
	}

	@Override
	public String message() {
		return message;
	}
}
