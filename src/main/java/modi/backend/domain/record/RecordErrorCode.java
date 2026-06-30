package modi.backend.domain.record;

import org.springframework.http.HttpStatus;

import modi.backend.support.error.ErrorCode;

public enum RecordErrorCode implements ErrorCode {

	RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND", "기록을 찾을 수 없습니다."),
	FORBIDDEN_RECORD(HttpStatus.FORBIDDEN, "FORBIDDEN", "기록에 접근할 권한이 없습니다."),
	INVALID_RECORD_INPUT(HttpStatus.BAD_REQUEST, "INVALID_INPUT", "기록 입력값이 올바르지 않습니다."),
	INVALID_RECORD_MEDIA(HttpStatus.BAD_REQUEST, "INVALID_MEDIA", "첨부할 수 없는 미디어입니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;

	RecordErrorCode(HttpStatus status, String code, String message) {
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
