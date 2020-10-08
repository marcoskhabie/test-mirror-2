package facultad.trendz.repository;

import facultad.trendz.model.Post;
import facultad.trendz.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    boolean existsByTitle(String title);
    Topic getTopicById(Long id);
    List<Topic> findAllByDeletedIsFalse();

}
