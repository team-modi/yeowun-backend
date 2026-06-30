package modi.backend.support.error;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

	HttpStatus getStatus();

	String code();

	String message();
}
