package facultad.trendz.dto.post;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class PostCreateDTO {

    @Size(max = 40, message = "Password must have as much as 40 characters")
    @NotNull(message = "Title cannot be empty")
    private String title;

    @Size(max = 40000, message = "Description can be up to 40000 characters long")
    private String description;

    private String link;

    private Long topicId;

    public PostCreateDTO(String title, String description, String link,Long topicId) {
        this.title = title;
        this.description = description;
        this.link=link;
        this.topicId=topicId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }
}