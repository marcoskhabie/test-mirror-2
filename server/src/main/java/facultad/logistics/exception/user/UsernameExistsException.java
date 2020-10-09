package facultad.logistics.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class UsernameExistsException extends RuntimeException {
    public UsernameExistsException(String msg) {
        super(msg);
    }
}
