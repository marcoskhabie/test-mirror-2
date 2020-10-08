package facultad.trendz.controller;

import facultad.trendz.dto.MessageResponseDTO;
import facultad.trendz.dto.post.PostGetDTO;
import facultad.trendz.dto.topic.TopicCreateDTO;
import facultad.trendz.dto.topic.TopicResponseDTO;
import facultad.trendz.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@Controller
public class TopicController {

    private final TopicService topicService;

    @Autowired
    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/topic")
    public ResponseEntity<Object> createTopic(@Valid @RequestBody TopicCreateDTO topic, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            final HttpStatus status = HttpStatus.BAD_REQUEST;
            String error = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(", "));
            return new ResponseEntity<>(error, status);
        }
        topicService.validateTopicTitle(topic.getTitle());
        final TopicResponseDTO body = topicService.saveTopic(topic);
        final HttpStatus status = HttpStatus.CREATED;
        return new ResponseEntity<>(body, status);
    }

    @GetMapping("/topic")
    public ResponseEntity<List<TopicResponseDTO>> getTopics(){
        final List<TopicResponseDTO> body = topicService.getTopicsByPopularity();
        final HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(body,status);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/topic/{topicId}")
    public ResponseEntity<MessageResponseDTO> deleteTopic(@PathVariable Long topicId){
        topicService.deleteTopic(topicId);
        final MessageResponseDTO body = new MessageResponseDTO("Topic deleted");
        final HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(body,status);
    }



    @GetMapping("/topicposts/{topicId}")
    public ResponseEntity<List<PostGetDTO>> getTopicPosts(@PathVariable Long topicId){
        List<PostGetDTO> body=topicService.getTopicPosts(topicId);
        final HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(body,status);
    }
}
