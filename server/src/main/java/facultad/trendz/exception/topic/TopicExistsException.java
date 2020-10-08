package facultad.trendz.exception.topic;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class TopicExistsException extends RuntimeException {
    public TopicExistsException(String msg) {
        super(msg);
    }
}