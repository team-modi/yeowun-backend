package modi.backend.support.error;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import modi.backend.support.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(CoreException.class)
	public ResponseEntity<ApiResponse<Object>> handleCore(CoreException e) {
		ErrorCode errorCode = e.errorCode();
		if (errorCode.getStatus().is5xxServerError()) {
			log.error("[{}] {}", errorCode.code(), e.getMessage(), e);
		} else {
			log.warn("[{}] {}", errorCode.code(), e.getMessage());
		}
		return ResponseEntity.status(errorCode.getStatus())
				.body(ApiResponse.fail(errorCode.code(), e.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<List<ApiResponse.FieldError>>> handleValidation(MethodArgumentNotValidException e) {
		List<ApiResponse.FieldError> fieldErrors = e.getBindingResult().getFieldErrors().stream()
				.map(GlobalExceptionHandler::toFieldError)
				.toList();
		return ResponseEntity.status(ErrorType.INVALID_INPUT.getStatus())
				.body(ApiResponse.failValidation(ErrorType.INVALID_INPUT.code(), ErrorType.INVALID_INPUT.message(),
						fieldErrors));
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ApiResponse<Object>> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
		return ResponseEntity.status(ErrorType.METHOD_NOT_ALLOWED.getStatus())
				.body(ApiResponse.fail(ErrorType.METHOD_NOT_ALLOWED.code(), ErrorType.METHOD_NOT_ALLOWED.message()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Object>> handleUnexpected(Exception e) {
		log.error("[{}] unhandled", ErrorType.INTERNAL_ERROR.code(), e);
		return ResponseEntity.status(ErrorType.INTERNAL_ERROR.getStatus())
				.body(ApiResponse.fail(ErrorType.INTERNAL_ERROR.code(), ErrorType.INTERNAL_ERROR.message()));
	}

	private static ApiResponse.FieldError toFieldError(FieldError fieldError) {
		return new ApiResponse.FieldError(fieldError.getField(), fieldError.getRejectedValue(),
				fieldError.getDefaultMessage());
	}
}
