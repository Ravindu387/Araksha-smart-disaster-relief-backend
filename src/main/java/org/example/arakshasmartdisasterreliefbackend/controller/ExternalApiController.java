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
    private final EmergencyRequestService emergencyRequestService;

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

    // ── Incoming Twilio SMS Webhook ──────────────────────────────────────────
    @PostMapping(value = "/sms/incoming", consumes = org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = org.springframework.http.MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> handleIncomingSms(
            @RequestParam("From") String from,
            @RequestParam("Body") String body) {
        
        log.info("Inbound Twilio SMS from: {}, Body: {}", from, body);

        String emergencyType = "General Emergency";
        String locationStr = "Colombo";
        String details = body;
        String lowerBody = body.toLowerCase();

        if (lowerBody.contains("flood")) {
            emergencyType = "Flood";
        } else if (lowerBody.contains("fire")) {
            emergencyType = "Fire";
        } else if (lowerBody.contains("landslide")) {
            emergencyType = "Landslide";
        } else if (lowerBody.contains("medical")) {
            emergencyType = "Medical Emergency";
        } else if (lowerBody.contains("earthquake")) {
            emergencyType = "Earthquake";
        }

        int atIndex = lowerBody.indexOf(" at ");
        if (atIndex != -1) {
            int endIndex = lowerBody.indexOf(" details ", atIndex);
            if (endIndex == -1) {
                endIndex = lowerBody.indexOf(" detail ", atIndex);
            }
            if (endIndex == -1) {
                locationStr = body.substring(atIndex + 4).trim();
            } else {
                locationStr = body.substring(atIndex + 4, endIndex).trim();
            }
        }

        int detailsIndex = lowerBody.indexOf(" details ");
        if (detailsIndex == -1) {
            detailsIndex = lowerBody.indexOf(" detail ");
        }
        if (detailsIndex != -1) {
            details = body.substring(detailsIndex + 9).trim();
        }

        // 1. Geocode
        double lat = 6.9271;
        double lng = 79.8612;
        try {
            LatLngDTO coords = geocodingService.geocode(locationStr);
            lat = coords.getLatitude();
            lng = coords.getLongitude();
        } catch (Exception e) {
            log.warn("Could not geocode inbound SMS location {}. Falling back to default Colombo.", locationStr);
        }

        // 2. Create emergency request
        org.example.arakshasmartdisasterreliefbackend.dto.request.EmergencyRequestRequest req =
                new org.example.arakshasmartdisasterreliefbackend.dto.request.EmergencyRequestRequest();
        req.setRequestId("REQ" + (int)(Math.random() * 900000 + 100000));
        req.setCitizenName("SMS User (" + from + ")");
        req.setEmergencyType(emergencyType);
        req.setPriority("Critical");
        req.setStatus("Pending");
        req.setLocation(locationStr);
        req.setLatitude(lat);
        req.setLongitude(lng);
        req.setDisasterImageUrl("");
        req.setDocumentUrl("");

        org.example.arakshasmartdisasterreliefbackend.dto.response.EmergencyRequestResponse saved =
                emergencyRequestService.createEmergencyRequest(req);

        // 3. Search closest volunteer
        String smsReply;
        try {
            NearestVolunteerDTO nearest = distanceService.getNearestVolunteer(saved.getId());
            if (nearest != null && nearest.getVolunteerId() != null) {
                // Auto-update assignment in DB
                req.setAssignedVolunteer(nearest.getName());
                req.setStatus("Assigned");
                emergencyRequestService.updateEmergencyRequest(saved.getId(), req);

                smsReply = String.format(
                        "Araksha: Emergency logged (ID: %s). Volunteer %s is dispatched. Distance: %.1f km, ETA: %.0f mins. Stay safe.",
                        saved.getRequestId(), nearest.getName(), nearest.getDistanceKm(), nearest.getDurationMinutes());
            } else {
                smsReply = String.format(
                        "Araksha: Emergency logged (ID: %s). We are searching for an available volunteer in your area. Stay safe.",
                        saved.getRequestId());
            }
        } catch (Exception e) {
            log.error("Failed to auto-dispatch volunteer: {}", e.getMessage());
            smsReply = String.format(
                    "Araksha: Emergency logged (ID: %s). We are processing response team availability. Stay safe.",
                    saved.getRequestId());
        }

        // Return Twilio SMS TwiML response
        String twiml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<Response>\n" +
                "    <Message>" + smsReply + "</Message>\n" +
                "</Response>";

        return ResponseEntity.ok(twiml);
    }

    // ── Severe Weather Hazard Alert overlays ─────────────────────────────────
    @GetMapping("/weather/hazards")
    public ResponseEntity<List<HazardZoneDTO>> getHazardZones() {
        log.info("REST request to fetch severe weather hazard zones");
        return ResponseEntity.ok(List.of(
            new HazardZoneDTO("Colombo High Flood Area", 6.9271, 79.8612, 4.5, "Severe drainage congestion flooding risk"),
            new HazardZoneDTO("Matara Coastal Surge", 5.9549, 80.5550, 7.0, "High tide storm surge risk warning"),
            new HazardZoneDTO("Ratnapura Landslide Warning", 6.6828, 80.3992, 5.5, "Heavy precipitation mountain instability warning")
        ));
    }

    // Helper static class to wrap responses nicely
    private static class StringResponse {
        public String message;
        public StringResponse(String message) { this.message = message; }
    }
}
