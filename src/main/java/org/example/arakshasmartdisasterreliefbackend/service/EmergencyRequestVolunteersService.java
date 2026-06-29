package org.example.arakshasmartdisasterreliefbackend.service;

import org.example.arakshasmartdisasterreliefbackend.dto.response.EmergencyResponseVolunteers;
import org.example.arakshasmartdisasterreliefbackend.entity.EmergencyRequestVolunteers;

import java.util.List;

public interface EmergencyRequestVolunteersService {
    EmergencyResponseVolunteers create(EmergencyRequestVolunteers request);


    List<EmergencyResponseVolunteers> getOpenRequests();


    EmergencyResponseVolunteers getByCode(String code);

}
