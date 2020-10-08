package facultad.trendz.dto.post;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class PostEditDTO {

    @Size(max = 40, message = "Password must have as long as 40 characters")
    @NotNull(message = "Title cannot be empty")
    private String title;

    @Size(max = 40000, message = "Description must have as long as 40000 characters")
    private String description;

    private String link;

    public PostEditDTO(String title, String description, String link) {
        this.title = title;
        this.description = description;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
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
}
