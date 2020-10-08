package facultad.trendz.dto.topic;

import java.util.Date;

public class TopicResponseDTO {

    private Long id;
    private String title;
    private String description;
    private Date date;


    public TopicResponseDTO(Long id, String title, String description, Date date){
        this.id = id;
        this.title = title;
        this.description = description;
        this.date=date;
    }

    public TopicResponseDTO() {}

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
