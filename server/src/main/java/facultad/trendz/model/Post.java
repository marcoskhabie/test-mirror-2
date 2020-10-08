package facultad.trendz.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue
    private Long id;

    private String title;

    @Column(length=40000)
    private String description;

    private String link;

    private Date date;


    @ManyToOne()
    @JoinColumn(name="topic_id", nullable=false)
    private Topic topic;


    public Post(String title, String description, String link, Date date, Topic topic) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.date = date;
        this.topic = topic;
    }

    public Post() {

    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
