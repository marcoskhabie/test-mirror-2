package facultad.trendz.controller;

import facultad.trendz.dto.post.PostCreateDTO;
import facultad.trendz.dto.post.PostEditDTO;
import facultad.trendz.dto.post.PostGetDTO;
import facultad.trendz.dto.post.PostResponseDTO;
import facultad.trendz.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@Controller
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/post")
    public ResponseEntity<Object> createPost(@Valid @RequestBody PostCreateDTO post, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            final HttpStatus status = HttpStatus.BAD_REQUEST;
            String error = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(", "));
            return new ResponseEntity<>(error, status);
        }
        postService.validatePostTitle(post.getTitle());
        final PostResponseDTO body = postService.savePost(post);
        final HttpStatus status = HttpStatus.CREATED;
        return new ResponseEntity<>(body, status);
    }

    @PutMapping("/post/{postId}")
    public ResponseEntity<PostResponseDTO> editPost(@Valid @RequestBody PostEditDTO postEdit, @PathVariable Long postId) {
        final PostResponseDTO body = postService.editPost(postEdit, postId);
        final HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(body,status);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<PostGetDTO> getPost(@PathVariable Long postId) {
        final PostGetDTO body = postService.getPost(postId);
        final HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(body,status);
    }
}

