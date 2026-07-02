package org.example.arakshasmartdisasterreliefbackend.service;

public interface SmsService {
    void sendSms(String to, String message);
    void sendVolunteerAssignmentSms(String volunteerPhone, String volunteerName, String requestId, String location);
    void sendEmergencyCreatedSms(String adminPhone, String requestId, String type, String location);
    void sendShelterNearlyFullSms(String managerPhone, String shelterName, int occupied, int capacity);
    void sendResourceShortageSms(String supervisorPhone, String itemName, int count, int minStock);
}
