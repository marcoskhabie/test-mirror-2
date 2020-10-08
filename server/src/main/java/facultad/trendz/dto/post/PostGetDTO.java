package facultad.trendz.dto.post;


import java.util.Date;

public class PostGetDTO {

    private Long id;
    private String title;
    private String description;
    private String link;
    private Date date;


    public PostGetDTO(Long id, String title, String description, String link, Date date){
        this.id = id;
        this.title = title;
        this.description = description;
        this.link=link;
        this.date=date;
    }

    public PostGetDTO() {}

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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
