package facultad.logistics.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Requested user not found")
public class UserNotFoundException extends RuntimeException {
}
