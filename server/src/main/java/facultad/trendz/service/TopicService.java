package facultad.trendz.service;

import facultad.trendz.dto.post.PostGetDTO;
import facultad.trendz.dto.topic.TopicCreateDTO;
import facultad.trendz.dto.topic.TopicResponseDTO;
import facultad.trendz.exception.topic.TopicExistsException;
import facultad.trendz.exception.topic.TopicNotFoundException;
import facultad.trendz.model.Post;
import facultad.trendz.model.Topic;
import facultad.trendz.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TopicService {

    private final TopicRepository topicRepository;

    @Autowired
    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public TopicResponseDTO saveTopic(TopicCreateDTO topicCreateDTO){
        final Topic topic = new Topic( topicCreateDTO.getTitle(), topicCreateDTO.getDescription(),new Date());
        topicRepository.save(topic);
        return new TopicResponseDTO(topic.getId(), topic.getTitle(), topic.getDescription(), topic.getCreationDate());
    }

    public void validateTopicTitle(String title) {
        if (topicRepository.existsByTitle(title))
            throw new TopicExistsException("Title " + title + " already in use");
    }

    public List<TopicResponseDTO> getTopicsByPopularity() {
        List<Topic> topics = topicRepository.findAllByDeletedIsFalse();

        topics.sort(Comparator.comparingInt((Topic topic) -> topic.getPosts().size()).reversed());

        List<TopicResponseDTO> topicResponses = new ArrayList<>(topics.size());
        for (Topic topic : topics) {
            topicResponses.add(new TopicResponseDTO(topic.getId(), topic.getTitle(), topic.getDescription(), topic.getCreationDate()));
        }
        return topicResponses;
    }

    public void deleteTopic(long topicId) {
        Optional<Topic> topic = topicRepository.findById(topicId);
        if (!topic.isPresent()) throw new TopicNotFoundException();

        topic.get().setDeleted(true);
        topicRepository.save(topic.get());
    }

    public List<PostGetDTO> getTopicPosts(Long topicId) {
       List<Post> posts = topicRepository.getTopicById(topicId).getPosts();
        List<PostGetDTO> postsInfo = new ArrayList<>(posts.size());
        for (Post post : posts) {
            postsInfo.add(new PostGetDTO(post.getId(), post.getTitle(), post.getDescription(),post.getLink(), post.getDate()));
        }
        return postsInfo;


    }
}
