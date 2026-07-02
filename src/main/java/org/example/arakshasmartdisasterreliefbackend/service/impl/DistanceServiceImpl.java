package org.example.arakshasmartdisasterreliefbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.arakshasmartdisasterreliefbackend.dto.LatLngDTO;
import org.example.arakshasmartdisasterreliefbackend.dto.NearbyShelterDTO;
import org.example.arakshasmartdisasterreliefbackend.dto.NearestVolunteerDTO;
import org.example.arakshasmartdisasterreliefbackend.entity.EmergencyRequest;
import org.example.arakshasmartdisasterreliefbackend.entity.Shelter;
import org.example.arakshasmartdisasterreliefbackend.entity.Volunteer;
import org.example.arakshasmartdisasterreliefbackend.repository.EmergencyRequestRepository;
import org.example.arakshasmartdisasterreliefbackend.repository.ShelterRepository;
import org.example.arakshasmartdisasterreliefbackend.repository.VolunteerRepository;
import org.example.arakshasmartdisasterreliefbackend.service.DistanceService;
import org.example.arakshasmartdisasterreliefbackend.service.GeocodingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DistanceServiceImpl implements DistanceService {

    private final ShelterRepository shelterRepository;
    private final VolunteerRepository volunteerRepository;
    private final EmergencyRequestRepository requestRepository;
    private final GeocodingService geocodingService;

    @Value("${google.maps.api.key:YOUR_GOOGLE_MAPS_API_KEY}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public List<NearbyShelterDTO> getNearbyShelters(Double lat, Double lng) {
        log.info("Calculating nearby shelters from: {}, {}", lat, lng);
        List<Shelter> shelters = shelterRepository.findAll();
        List<NearbyShelterDTO> nearbyShelters = new ArrayList<>();

        for (Shelter s : shelters) {
            Double sLat = s.getLatitude() != null ? s.getLatitude().doubleValue() : null;
            Double sLng = s.getLongitude() != null ? s.getLongitude().doubleValue() : null;

            // If coordinates are missing, mock them or geocode
            if (sLat == null || sLng == null) {
                LatLngDTO mockCoords = geocodingService.geocode(s.getName() + ", " + s.getAddress());
                sLat = mockCoords.getLatitude();
                sLng = mockCoords.getLongitude();
            }

            Double distanceKm;
            Double durationMin;

            if (isApiKeyMissing()) {
                // local calculation
                distanceKm = haversineDistance(lat, lng, sLat, sLng);
                durationMin = calculateDrivingDuration(distanceKm);
            } else {
                // Call Distance Matrix API
                DistanceDuration dd = getDistanceMatrix(lat, lng, sLat, sLng);
                distanceKm = dd.distanceKm;
                durationMin = dd.durationMinutes;
            }

            nearbyShelters.add(NearbyShelterDTO.builder()
                    .shelterId(s.getId())
                    .name(s.getName())
                    .address(s.getAddress())
                    .distanceKm(distanceKm)
                    .durationMinutes(durationMin)
                    .capacity(s.getCapacity())
                    .occupied(s.getOccupied())
                    .status(s.getStatus())
                    .latitude(sLat)
                    .longitude(sLng)
                    .build());
        }

        // Sort by distance ascending
        nearbyShelters.sort(Comparator.comparing(NearbyShelterDTO::getDistanceKm));
        return nearbyShelters;
    }

    @Override
    public NearestVolunteerDTO getNearestVolunteer(Long requestId) {
        log.info("Finding nearest volunteer for request ID: {}", requestId);
        EmergencyRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found: " + requestId));

        Double rLat = request.getLatitude();
        Double rLng = request.getLongitude();

        if (rLat == null || rLng == null) {
            LatLngDTO coords = geocodingService.geocode(request.getLocation());
            rLat = coords.getLatitude();
            rLng = coords.getLongitude();
            // Save coordinates back to request
            request.setLatitude(rLat);
            request.setLongitude(rLng);
            requestRepository.save(request);
        }

        List<Volunteer> volunteers = volunteerRepository.findAll().stream()
                .filter(v -> "Available".equalsIgnoreCase(v.getStatus()))
                .toList();

        if (volunteers.isEmpty()) {
            return null;
        }

        NearestVolunteerDTO nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Volunteer v : volunteers) {
            Double vLat = v.getLatitude();
            Double vLng = v.getLongitude();

            if (vLat == null || vLng == null) {
                LatLngDTO mockCoords = geocodingService.geocode(v.getName() + ", " + v.getLocation());
                vLat = mockCoords.getLatitude();
                vLng = mockCoords.getLongitude();
                v.setLatitude(vLat);
                v.setLongitude(vLng);
                volunteerRepository.save(v);
            }

            Double distanceKm;
            Double durationMin;

            if (isApiKeyMissing()) {
                distanceKm = haversineDistance(rLat, rLng, vLat, vLng);
                durationMin = calculateDrivingDuration(distanceKm);
            } else {
                DistanceDuration dd = getDistanceMatrix(vLat, vLng, rLat, rLng);
                distanceKm = dd.distanceKm;
                durationMin = dd.durationMinutes;
            }

            if (distanceKm < minDistance) {
                minDistance = distanceKm;
                nearest = NearestVolunteerDTO.builder()
                        .volunteerId(v.getId())
                        .name(v.getName())
                        .phone(v.getPhone())
                        .rating(v.getRating())
                        .skills(v.getSkills())
                        .status(v.getStatus())
                        .distanceKm(distanceKm)
                        .durationMinutes(durationMin)
                        .latitude(vLat)
                        .longitude(vLng)
                        .build();
            }
        }

        return nearest;
    }

    private boolean isApiKeyMissing() {
        return apiKey == null || apiKey.trim().isEmpty() || "YOUR_GOOGLE_MAPS_API_KEY".equals(apiKey);
    }

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radious of the earth
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private double calculateDrivingDuration(double distanceKm) {
        // Average speed: 45 km/h
        return (distanceKm / 45.0) * 60.0;
    }

    private DistanceDuration getDistanceMatrix(double originLat, double originLng, double destLat, double destLng) {
        int retries = 2;
        while (retries >= 0) {
            try {
                String url = String.format("https://maps.googleapis.com/maps/api/distancematrix/json?origins=%f,%f&destinations=%f,%f&key=%s",
                        originLat, originLng, destLat, destLng, apiKey);
                Map<?, ?> response = restTemplate.getForObject(url, Map.class);
                if (response != null && "OK".equals(response.get("status"))) {
                    java.util.List<?> rows = (java.util.List<?>) response.get("rows");
                    if (rows != null && !rows.isEmpty()) {
                        Map<?, ?> firstRow = (Map<?, ?>) rows.getFirst();
                        java.util.List<?> elements = (java.util.List<?>) firstRow.get("elements");
                        if (elements != null && !elements.isEmpty()) {
                            Map<?, ?> firstElement = (Map<?, ?>) elements.getFirst();
                            if ("OK".equals(firstElement.get("status"))) {
                                Map<?, ?> distance = (Map<?, ?>) firstElement.get("distance");
                                Map<?, ?> duration = (Map<?, ?>) firstElement.get("duration");
                                
                                double distanceValue = ((Number) distance.get("value")).doubleValue() / 1000.0; // meters to km
                                double durationValue = ((Number) duration.get("value")).doubleValue() / 60.0; // seconds to minutes
                                
                                return new DistanceDuration(distanceValue, durationValue);
                            }
                        }
                    }
                }
                break;
            } catch (Exception e) {
                log.warn("Distance Matrix call failed, retrying (retries left: {})", retries, e);
                retries--;
            }
        }
        
        // Fallback
        double dist = haversineDistance(originLat, originLng, destLat, destLng);
        return new DistanceDuration(dist, calculateDrivingDuration(dist));
    }

    private static class DistanceDuration {
        double distanceKm;
        double durationMinutes;

        DistanceDuration(double distanceKm, double durationMinutes) {
            this.distanceKm = distanceKm;
            this.durationMinutes = durationMinutes;
        }
    }
}
