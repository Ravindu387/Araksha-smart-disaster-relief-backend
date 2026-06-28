package org.example.arakshasmartdisasterreliefbackend.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;

    private String severity;

    private String title;

    private String badge;

    @Column(length = 1000)
    private String description;

    private String time;

    @Column(name = "is_read")
    private boolean read;

    public Notification() {
    }

    public Notification(Long id, String category, String severity, String title,
                        String badge, String description, String time, boolean read) {
        this.id = id;
        this.category = category;
        this.severity = severity;
        this.title = title;
        this.badge = badge;
        this.description = description;
        this.time = time;
        this.read = read;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
