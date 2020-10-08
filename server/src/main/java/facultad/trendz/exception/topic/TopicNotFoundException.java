package facultad.trendz.exception.topic;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Requested topic not found")
public class TopicNotFoundException extends RuntimeException {
}