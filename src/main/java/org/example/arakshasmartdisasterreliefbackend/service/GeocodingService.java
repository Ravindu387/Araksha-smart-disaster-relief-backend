package org.example.arakshasmartdisasterreliefbackend.service;

import org.example.arakshasmartdisasterreliefbackend.dto.LatLngDTO;

public interface GeocodingService {
    LatLngDTO geocode(String address);
    String reverseGeocode(Double latitude, Double longitude);
}
