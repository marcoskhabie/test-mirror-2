package facultad.trendz.service;

import facultad.trendz.dto.post.PostCreateDTO;
import facultad.trendz.dto.post.PostEditDTO;
import facultad.trendz.dto.post.PostGetDTO;
import facultad.trendz.dto.post.PostResponseDTO;
import facultad.trendz.exception.post.PostExistsException;
import facultad.trendz.exception.post.PostNotFoundException;
import facultad.trendz.model.Post;
import facultad.trendz.repository.PostRepository;
import facultad.trendz.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final TopicRepository topicRepository;

    @Autowired
    public PostService(PostRepository postRepository, TopicRepository topicRepository) {
        this.postRepository = postRepository;
        this.topicRepository = topicRepository;
    }

    public PostResponseDTO savePost(PostCreateDTO postCreateDTO) {
        final Post post = new Post(postCreateDTO.getTitle(), postCreateDTO.getDescription(),
                postCreateDTO.getLink(), new Date(), topicRepository.getTopicById(postCreateDTO.getTopicId()));
        postRepository.save(post);
        return new PostResponseDTO(post.getId(), post.getTitle(), post.getDescription(), post.getLink(), post.getDate(), post.getTopic().getId());
    }

    public void validatePostTitle(String title) {
        if (postRepository.existsByTitle(title))
            throw new PostExistsException("Title " + title + " already in use");
    }

    public PostResponseDTO editPost(PostEditDTO postEdit, Long postId) {
        final Optional<Post> post = postRepository.findById(postId);
        if (!post.isPresent()) throw new PostNotFoundException();

        String newTitle = postEdit.getTitle();
        String newDescription = postEdit.getDescription();
        String newLink = postEdit.getLink();

        if(!newTitle.equals(post.get().getTitle())) {
            if (postRepository.existsByTitle(newTitle)) {
                throw new PostExistsException(String.format("Title %s already in use", newTitle));
            } else post.get().setTitle(newTitle);
        }

        if (newDescription != null) post.get().setDescription(newDescription);

        if (newLink != null) post.get().setLink(newLink);

        Post editedPost = postRepository.save(post.get());

        return new PostResponseDTO(editedPost.getId(),
                editedPost.getTitle(),
                editedPost.getDescription(),
                editedPost.getLink(),
                editedPost.getDate() ,
                editedPost.getTopic().getId());
    }

    public PostGetDTO getPost(Long postId){
        final Optional<Post> post= postRepository.findById(postId);
        if (!post.isPresent()) throw new PostNotFoundException();

        return new PostGetDTO(post.get().getTopic().getId(),
                post.get().getTitle(),
                post.get().getDescription(),
                post.get().getLink(),
                post.get().getDate() );



    }


}