package facultad.trendz.exception.post;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Requested post not found")
public class PostNotFoundException extends RuntimeException {
}
