package org.example.arakshasmartdisasterreliefbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.arakshasmartdisasterreliefbackend.dto.LatLngDTO;
import org.example.arakshasmartdisasterreliefbackend.dto.RouteResponseDTO;
import org.example.arakshasmartdisasterreliefbackend.service.GoogleMapsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleMapsServiceImpl implements GoogleMapsService {

    @Value("${google.maps.api.key:YOUR_GOOGLE_MAPS_API_KEY}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public RouteResponseDTO getRoute(Double startLat, Double startLng, Double endLat, Double endLng) {
        log.info("Requesting route from ({}, {}) to ({}, {})", startLat, startLng, endLat, endLng);

        if (startLat == null || startLng == null || endLat == null || endLng == null) {
            throw new IllegalArgumentException("Start and End coordinates must not be null");
        }

        // Fallback checks
        if (apiKey == null || apiKey.trim().isEmpty() || "YOUR_GOOGLE_MAPS_API_KEY".equals(apiKey)) {
            return generateFallbackRoute(startLat, startLng, endLat, endLng);
        }

        int retries = 2;
        while (retries >= 0) {
            try {
                String url = String.format("https://maps.googleapis.com/maps/api/directions/json?origin=%f,%f&destination=%f,%f&key=%s",
                        startLat, startLng, endLat, endLng, apiKey);
                Map<?, ?> response = restTemplate.getForObject(url, Map.class);
                if (response != null && "OK".equals(response.get("status"))) {
                    java.util.List<?> routes = (java.util.List<?>) response.get("routes");
                    if (routes != null && !routes.isEmpty()) {
                        Map<?, ?> route = (Map<?, ?>) routes.getFirst();
                        java.util.List<?> legs = (java.util.List<?>) route.get("legs");
                        Map<?, ?> firstLeg = (Map<?, ?>) legs.getFirst();
                        
                        Map<?, ?> distance = (Map<?, ?>) firstLeg.get("distance");
                        Map<?, ?> duration = (Map<?, ?>) firstLeg.get("duration");
                        Map<?, ?> polylineMap = (Map<?, ?>) route.get("overview_polyline");
                        
                        String polyline = (String) polylineMap.get("points");
                        Double distanceKm = ((Number) distance.get("value")).doubleValue() / 1000.0;
                        Double durationMin = ((Number) duration.get("value")).doubleValue() / 60.0;
                        String startAddr = (String) firstLeg.get("start_address");
                        String endAddr = (String) firstLeg.get("end_address");

                        // Extract points from polyline for visualizers
                        List<LatLngDTO> coordinates = decodePolyline(polyline);

                        return RouteResponseDTO.builder()
                                .startAddress(startAddr)
                                .endAddress(endAddr)
                                .polyline(polyline)
                                .distanceKm(distanceKm)
                                .durationMinutes(durationMin)
                                .coordinates(coordinates)
                                .build();
                    }
                }
                break;
            } catch (Exception e) {
                log.warn("Directions API failed, retrying (retries left: {})", retries, e);
                retries--;
            }
        }

        return generateFallbackRoute(startLat, startLng, endLat, endLng);
    }

    private RouteResponseDTO generateFallbackRoute(Double startLat, Double startLng, Double endLat, Double endLng) {
        log.info("Generating mock fallback route path between coordinates");
        double distanceKm = haversineDistance(startLat, startLng, endLat, endLng);
        double durationMin = (distanceKm / 45.0) * 60.0; // 45 km/h driving

        // Interpolate 10 coordinates to simulate a street-following route path
        List<LatLngDTO> coordinates = new ArrayList<>();
        int steps = 12;
        for (int i = 0; i <= steps; i++) {
            double ratio = (double) i / steps;
            double lat = startLat + ratio * (endLat - startLat);
            double lng = startLng + ratio * (endLng - startLng);
            
            // Add slight zig-zag to simulate a real road layout
            if (i > 0 && i < steps) {
                lat += (Math.random() - 0.5) * 0.005;
                lng += (Math.random() - 0.5) * 0.005;
            }
            coordinates.add(new LatLngDTO(lat, lng));
        }

        return RouteResponseDTO.builder()
                .startAddress(String.format("Position (%.4f, %.4f)", startLat, startLng))
                .endAddress(String.format("Position (%.4f, %.4f)", endLat, endLng))
                .polyline("")
                .distanceKm(distanceKm)
                .durationMinutes(durationMin)
                .coordinates(coordinates)
                .build();
    }

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * Decodes a Google Maps polyline.
     */
    private List<LatLngDTO> decodePolyline(String encoded) {
        List<LatLngDTO> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            poly.add(new LatLngDTO((double) lat / 1E5, (double) lng / 1E5));
        }

        return poly;
    }
}
