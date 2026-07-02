package org.example.arakshasmartdisasterreliefbackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mutual_aid_items")
public class MutualAidItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String citizenName;

    @Column(nullable = false)
    private String itemType;

    private String description;

    @Column(nullable = false)
    private String contactPhone;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private String type; // OFFER or NEED

    private LocalDateTime createdAt;

    public MutualAidItem() {
        this.createdAt = LocalDateTime.now();
    }

    public MutualAidItem(String citizenName, String itemType, String description, String contactPhone, double latitude, double longitude, String type) {
        this.citizenName = citizenName;
        this.itemType = itemType;
        this.description = description;
        this.contactPhone = contactPhone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCitizenName() { return citizenName; }
    public void setCitizenName(String citizenName) { this.citizenName = citizenName; }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
