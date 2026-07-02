package org.example.arakshasmartdisasterreliefbackend.dto;

public class HazardZoneDTO {
    private String name;
    private double latitude;
    private double longitude;
    private double radiusKm;
    private String description;

    public HazardZoneDTO() {}

    public HazardZoneDTO(String name, double latitude, double longitude, double radiusKm, String description) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radiusKm = radiusKm;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getRadiusKm() {
        return radiusKm;
    }

    public void setRadiusKm(double radiusKm) {
        this.radiusKm = radiusKm;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
