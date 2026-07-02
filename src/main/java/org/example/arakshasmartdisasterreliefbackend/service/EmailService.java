package org.example.arakshasmartdisasterreliefbackend.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
    void sendVolunteerAssignmentEmail(String toEmail, String volunteerName, String requestId, String location, String instructions);
    void sendEmergencyConfirmationEmail(String toEmail, String citizenName, String requestId, String emergencyType, String location);
    void sendShelterNotificationEmail(String toEmail, String shelterName, String status, int occupied, int capacity);
    void sendDailySummaryReportEmail(String toEmail, int totalEmergencies, int activeVolunteers, double shelterOccupancy, int lowStockResources, int pendingRequests);
}
