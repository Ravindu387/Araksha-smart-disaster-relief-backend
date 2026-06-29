package org.example.arakshasmartdisasterreliefbackend.dto.request;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettingsDTO {

    private Long id;

    // Profile
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String jobTitle;
    private String location;
    private String bio;
    private String timezone;
    private String language;

    // Security
    private Boolean twoFactorEnabled;
    private String sessionTimeout;
    private String passwordExpiry;

    // System
    private Boolean autoAssignVolunteers;
    private Boolean aiRecommendations;
    private Boolean gpsTracking;
    private Boolean publicAlerts;
}