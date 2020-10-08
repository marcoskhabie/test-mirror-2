package facultad.trendz.exception.post;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class PostExistsException extends RuntimeException {
    public PostExistsException(String msg) {
        super(msg);
    }
}
