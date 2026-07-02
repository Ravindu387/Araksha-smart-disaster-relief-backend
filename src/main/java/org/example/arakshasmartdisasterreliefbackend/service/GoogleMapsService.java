package org.example.arakshasmartdisasterreliefbackend.service;

import org.example.arakshasmartdisasterreliefbackend.dto.RouteResponseDTO;

public interface GoogleMapsService {
    RouteResponseDTO getRoute(Double startLat, Double startLng, Double endLat, Double endLng);
}
