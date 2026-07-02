package org.example.arakshasmartdisasterreliefbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.arakshasmartdisasterreliefbackend.dto.LatLngDTO;
import org.example.arakshasmartdisasterreliefbackend.service.GeocodingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeocodingServiceImpl implements GeocodingService {

    @Value("${google.maps.api.key:YOUR_GOOGLE_MAPS_API_KEY}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // Local dictionary of Sri Lankan cities for mock geocoding/reverse geocoding
    private static final Map<String, LatLngDTO> SRI_LANKA_CITIES = new HashMap<>();

    static {
        SRI_LANKA_CITIES.put("colombo", new LatLngDTO(6.9271, 79.8612));
        SRI_LANKA_CITIES.put("kandy", new LatLngDTO(7.2906, 80.6337));
        SRI_LANKA_CITIES.put("galle", new LatLngDTO(6.0367, 80.2170));
        SRI_LANKA_CITIES.put("jaffna", new LatLngDTO(9.6615, 80.0144));
        SRI_LANKA_CITIES.put("gampaha", new LatLngDTO(7.0873, 80.0144));
        SRI_LANKA_CITIES.put("kalutara", new LatLngDTO(6.5854, 79.9607));
        SRI_LANKA_CITIES.put("matara", new LatLngDTO(5.9549, 80.5550));
        SRI_LANKA_CITIES.put("hambantota", new LatLngDTO(6.1249, 81.1185));
        SRI_LANKA_CITIES.put("negombo", new LatLngDTO(7.2089, 79.8373));
        SRI_LANKA_CITIES.put("batticaloa", new LatLngDTO(7.7170, 81.7000));
        SRI_LANKA_CITIES.put("trincomalee", new LatLngDTO(8.5873, 81.2152));
        SRI_LANKA_CITIES.put("anuradhapura", new LatLngDTO(8.3114, 80.4037));
        SRI_LANKA_CITIES.put("polonnaruwa", new LatLngDTO(7.9403, 81.0188));
        SRI_LANKA_CITIES.put("kurunegala", new LatLngDTO(7.4863, 80.3623));
        SRI_LANKA_CITIES.put("puttalam", new LatLngDTO(8.0333, 79.8333));
        SRI_LANKA_CITIES.put("ratnapura", new LatLngDTO(6.6828, 80.3992));
        SRI_LANKA_CITIES.put("kegalle", new LatLngDTO(7.2513, 80.3464));
        SRI_LANKA_CITIES.put("badulla", new LatLngDTO(6.9934, 81.0550));
        SRI_LANKA_CITIES.put("moneragala", new LatLngDTO(6.8724, 81.3507));
        SRI_LANKA_CITIES.put("nuwara eliya", new LatLngDTO(6.9497, 80.7891));
        SRI_LANKA_CITIES.put("matale", new LatLngDTO(7.4675, 80.6234));
        SRI_LANKA_CITIES.put("vavuniya", new LatLngDTO(8.7542, 80.4982));
        SRI_LANKA_CITIES.put("mannar", new LatLngDTO(8.9810, 79.9044));
        SRI_LANKA_CITIES.put("mullaitivu", new LatLngDTO(9.2671, 80.8142));
        SRI_LANKA_CITIES.put("kilinochchi", new LatLngDTO(9.3803, 80.3992));
        SRI_LANKA_CITIES.put("ampara", new LatLngDTO(7.2833, 81.6667));
    }

    @Override
    public LatLngDTO geocode(String address) {
        if (address == null || address.trim().isEmpty()) {
            return new LatLngDTO(6.9271, 79.8612); // Colombo default
        }

        log.info("Geocoding address: {}", address);

        // Fallback check
        if (apiKey == null || apiKey.trim().isEmpty() || "YOUR_GOOGLE_MAPS_API_KEY".equals(apiKey)) {
            log.info("Using local fallback mock geocoder for: {}", address);
            String lowerAddr = address.toLowerCase();
            for (Map.Entry<String, LatLngDTO> entry : SRI_LANKA_CITIES.entrySet()) {
                if (lowerAddr.contains(entry.getKey())) {
                    LatLngDTO coords = entry.getValue();
                    // Add slight random offset to simulate precise geocoding
                    double offsetLat = (Math.random() - 0.5) * 0.01;
                    double offsetLng = (Math.random() - 0.5) * 0.01;
                    return new LatLngDTO(coords.getLatitude() + offsetLat, coords.getLongitude() + offsetLng);
                }
            }
            // Generate coordinates within Sri Lankan grid
            double lat = 6.0 + Math.random() * 3.5;
            double lng = 79.8 + Math.random() * 1.8;
            return new LatLngDTO(lat, lng);
        }

        // Live Google Maps API Integration with simple retry
        int retries = 2;
        while (retries >= 0) {
            try {
                String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s", 
                        address.replace(" ", "+"), apiKey);
                Map<?, ?> response = restTemplate.getForObject(url, Map.class);
                if (response != null && "OK".equals(response.get("status"))) {
                    java.util.List<?> results = (java.util.List<?>) response.get("results");
                    if (results != null && !results.isEmpty()) {
                        Map<?, ?> firstResult = (Map<?, ?>) results.getFirst();
                        Map<?, ?> geometry = (Map<?, ?>) firstResult.get("geometry");
                        Map<?, ?> location = (Map<?, ?>) geometry.get("location");
                        Double lat = ((Number) location.get("lat")).doubleValue();
                        Double lng = ((Number) location.get("lng")).doubleValue();
                        return new LatLngDTO(lat, lng);
                    }
                }
                break;
            } catch (Exception e) {
                log.warn("Geocoding failed for: {}, retrying (retries left: {})", address, retries, e);
                retries--;
            }
        }
        
        // Final fallback on API failure
        return new LatLngDTO(6.9271, 79.8612);
    }

    @Override
    public String reverseGeocode(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return "Unknown, Sri Lanka";
        }

        log.info("Reverse geocoding coordinates: {}, {}", latitude, longitude);

        // Fallback check
        if (apiKey == null || apiKey.trim().isEmpty() || "YOUR_GOOGLE_MAPS_API_KEY".equals(apiKey)) {
            log.info("Using local fallback mock reverse geocoder");
            double minDist = Double.MAX_VALUE;
            String nearestCity = "Colombo";
            for (Map.Entry<String, LatLngDTO> entry : SRI_LANKA_CITIES.entrySet()) {
                LatLngDTO coords = entry.getValue();
                double dist = Math.pow(coords.getLatitude() - latitude, 2) + Math.pow(coords.getLongitude() - longitude, 2);
                if (dist < minDist) {
                    minDist = dist;
                    nearestCity = entry.getKey();
                }
            }
            String capitalized = nearestCity.substring(0, 1).toUpperCase() + nearestCity.substring(1);
            return String.format("%s, Sri Lanka", capitalized);
        }

        // Live Google Maps API Integration with simple retry
        int retries = 2;
        while (retries >= 0) {
            try {
                String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&key=%s", 
                        latitude, longitude, apiKey);
                Map<?, ?> response = restTemplate.getForObject(url, Map.class);
                if (response != null && "OK".equals(response.get("status"))) {
                    java.util.List<?> results = (java.util.List<?>) response.get("results");
                    if (results != null && !results.isEmpty()) {
                        Map<?, ?> firstResult = (Map<?, ?>) results.getFirst();
                        return (String) firstResult.get("formatted_address");
                    }
                }
                break;
            } catch (Exception e) {
                log.warn("Reverse geocoding failed, retrying (retries left: {})", retries, e);
                retries--;
            }
        }

        return "Colombo, Sri Lanka";
    }
}
