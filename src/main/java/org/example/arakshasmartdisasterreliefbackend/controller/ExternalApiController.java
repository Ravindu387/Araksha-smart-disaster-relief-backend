package org.example.arakshasmartdisasterreliefbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.arakshasmartdisasterreliefbackend.dto.*;
import org.example.arakshasmartdisasterreliefbackend.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class ExternalApiController {

    private final WeatherService weatherService;
    private final GeocodingService geocodingService;
    private final GoogleMapsService googleMapsService;
    private final DistanceService distanceService;
    private final SmsService smsService;
    private final EmailService emailService;

    // ── Weather API ──────────────────────────────────────────────────────────
    @GetMapping("/weather")
    public ResponseEntity<WeatherDTO> getWeather(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon) {
        
        log.info("REST request to fetch weather. City: {}, Lat: {}, Lon: {}", city, lat, lon);
        
        if (lat != null && lon != null) {
            return ResponseEntity.ok(weatherService.getWeather(lat, lon));
        }
        
        if (city == null || city.trim().isEmpty()) {
            city = "Colombo";
        }
        
        return ResponseEntity.ok(weatherService.getWeather(city));
    }

    // ── Geocoding API ────────────────────────────────────────────────────────
    @GetMapping("/maps/location")
    public ResponseEntity<?> handleLocationGeocode(
            @RequestParam(required = false) String address,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng) {
        
        log.info("REST request for location services. Address: {}, Lat: {}, Lng: {}", address, lat, lng);
        
        if (address != null && !address.trim().isEmpty()) {
            LatLngDTO coords = geocodingService.geocode(address);
            return ResponseEntity.ok(coords);
        }
        
        if (lat != null && lng != null) {
            String readableAddress = geocodingService.reverseGeocode(lat, lng);
            return ResponseEntity.ok(new StringResponse(readableAddress));
        }
        
        return ResponseEntity.badRequest().body(new StringResponse("Must supply either 'address' or 'lat' & 'lng' params"));
    }

    // ── Directions API ───────────────────────────────────────────────────────
    @GetMapping("/maps/route")
    public ResponseEntity<RouteResponseDTO> getRoute(
            @RequestParam Double startLat,
            @RequestParam Double startLng,
            @RequestParam Double endLat,
            @RequestParam Double endLng) {
        
        log.info("REST request to compute route directions");
        RouteResponseDTO route = googleMapsService.getRoute(startLat, startLng, endLat, endLng);
        return ResponseEntity.ok(route);
    }

    // ── Distance Matrix APIs ─────────────────────────────────────────────────
    @GetMapping("/maps/nearby-shelters")
    public ResponseEntity<List<NearbyShelterDTO>> getNearbyShelters(
            @RequestParam Double lat,
            @RequestParam Double lng) {
        
        log.info("REST request to query nearby shelters");
        List<NearbyShelterDTO> shelters = distanceService.getNearbyShelters(lat, lng);
        return ResponseEntity.ok(shelters);
    }

    @GetMapping("/maps/nearest-volunteer")
    public ResponseEntity<?> getNearestVolunteer(@RequestParam Long requestId) {
        log.info("REST request to find recommended volunteer for request: {}", requestId);
        NearestVolunteerDTO nearest = distanceService.getNearestVolunteer(requestId);
        if (nearest == null) {
            return ResponseEntity.ok(new StringResponse("No available volunteers online"));
        }
        return ResponseEntity.ok(nearest);
    }

    // ── Manual Notification Dispatchers ──────────────────────────────────────
    @PostMapping("/sms/send")
    public ResponseEntity<StringResponse> sendSms(@Valid @RequestBody SmsRequestDTO request) {
        log.info("REST request to dispatch manual SMS");
        smsService.sendSms(request.getPhoneNumber(), request.getMessage());
        return ResponseEntity.ok(new StringResponse("SMS successfully sent to queue"));
    }

    @PostMapping("/email/send")
    public ResponseEntity<StringResponse> sendEmail(@Valid @RequestBody EmailRequestDTO request) {
        log.info("REST request to dispatch manual Email");
        emailService.sendEmail(request.getTo(), request.getSubject(), request.getBody());
        return ResponseEntity.ok(new StringResponse("Email successfully sent to queue"));
    }

    // Helper static class to wrap responses nicely
    private static class StringResponse {
        public String message;
        public StringResponse(String message) { this.message = message; }
    }
}
