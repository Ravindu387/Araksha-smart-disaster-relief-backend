package org.example.arakshasmartdisasterreliefbackend.service;

import org.example.arakshasmartdisasterreliefbackend.dto.NearbyShelterDTO;
import org.example.arakshasmartdisasterreliefbackend.dto.NearestVolunteerDTO;
import java.util.List;

public interface DistanceService {
    List<NearbyShelterDTO> getNearbyShelters(Double lat, Double lng);
    NearestVolunteerDTO getNearestVolunteer(Long requestId);
}
