package facultad.logistics.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "Invalid credentials")
public class IncorrectPasswordException extends RuntimeException {
}
