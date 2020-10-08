package facultad.trendz.dto.topic;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class TopicCreateDTO {

    @Size(max = 40, message = "Password must have as much as 40 characters")
    @NotNull(message = "Title cannot be empty")
    private String title;

    @Size(max = 40000, message = "Description can be up to 40000 characters long")
    private String description;

    public TopicCreateDTO(String title, String description) {
        this.title = title;
        this.description = description;
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


}
