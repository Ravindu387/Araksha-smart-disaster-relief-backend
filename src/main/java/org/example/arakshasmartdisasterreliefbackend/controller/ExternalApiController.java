package org.example.arakshasmartdisasterreliefbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.arakshasmartdisasterreliefbackend.dto.*;
import org.example.arakshasmartdisasterreliefbackend.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.arakshasmartdisasterreliefbackend.dto.response.EmergencyRequestResponse;

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
    private final org.example.arakshasmartdisasterreliefbackend.repository.MutualAidRepository mutualAidRepository;
    private final org.example.arakshasmartdisasterreliefbackend.repository.CitizenRepository citizenRepository;
    private final org.example.arakshasmartdisasterreliefbackend.repository.ShelterRepository shelterRepository;
    private final org.example.arakshasmartdisasterreliefbackend.repository.InventoryRepository inventoryRepository;

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
        
        log.info("REST request to query nearby shelters with predictive redirection check");
        List<NearbyShelterDTO> shelters = distanceService.getNearbyShelters(lat, lng);
        
        if (!shelters.isEmpty()) {
            NearbyShelterDTO closest = shelters.get(0);
            if (closest.getOccupied() != null && closest.getCapacity() != null &&
                    closest.getOccupied() >= (closest.getCapacity() * 0.9)) {
                
                NearbyShelterDTO alternative = null;
                for (int i = 1; i < shelters.size(); i++) {
                    NearbyShelterDTO candidate = shelters.get(i);
                    if (candidate.getOccupied() != null && candidate.getCapacity() != null &&
                            candidate.getOccupied() < (candidate.getCapacity() * 0.9)) {
                        alternative = candidate;
                        break;
                    }
                }
                
                if (alternative != null) {
                    closest.setRedirectionTarget(alternative.getName());
                    closest.setRedirectLat(alternative.getLatitude());
                    closest.setRedirectLng(alternative.getLongitude());
                    log.warn("🚨 Closest shelter '{}' is near capacity! Suggesting redirection to '{}'.",
                            closest.getName(), alternative.getName());
                }
            }
        }
        
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

        if (lowerBody.contains("flood") || lowerBody.contains("ගංවතුර") || lowerBody.contains("galayama") || lowerBody.contains("vellam") || lowerBody.contains("வெள்ளம்")) {
            emergencyType = "Flood";
        } else if (lowerBody.contains("fire") || lowerBody.contains("ගින්න") || lowerBody.contains("ginna") || lowerBody.contains("neruppu") || lowerBody.contains("நெருப்பு")) {
            emergencyType = "Fire";
        } else if (lowerBody.contains("landslide") || lowerBody.contains("නායයෑම") || lowerBody.contains("nayanama") || lowerBody.contains("sarivu") || lowerBody.contains("சரிவு")) {
            emergencyType = "Landslide";
        } else if (lowerBody.contains("medical") || lowerBody.contains("තදබල") || lowerBody.contains("மருத்துவ")) {
            emergencyType = "Medical Emergency";
        } else if (lowerBody.contains("earthquake") || lowerBody.contains("භූමිකම්පාව") || lowerBody.contains("நிலநடுக்கம்")) {
            emergencyType = "Earthquake";
        }

        int atIndex = lowerBody.indexOf(" at ");
        int prepLen = 4;
        if (atIndex == -1) {
            atIndex = lowerBody.indexOf(" ළඟ ");
            prepLen = 4;
        }
        if (atIndex == -1) {
            atIndex = lowerBody.indexOf(" இல் ");
            prepLen = 5;
        }

        if (atIndex != -1) {
            int endIndex = lowerBody.indexOf(" details ", atIndex);
            if (endIndex == -1) {
                endIndex = lowerBody.indexOf(" detail ", atIndex);
            }
            if (endIndex == -1) {
                locationStr = body.substring(atIndex + prepLen).trim();
            } else {
                locationStr = body.substring(atIndex + prepLen, endIndex).trim();
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

    public static class IotTelemetryDTO {
        private String riverName;
        private double latitude;
        private double longitude;
        private double waterLevelMeters;

        public String getRiverName() { return riverName; }
        public void setRiverName(String riverName) { this.riverName = riverName; }

        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }

        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }

        public double getWaterLevelMeters() { return waterLevelMeters; }
        public void setWaterLevelMeters(double waterLevelMeters) { this.waterLevelMeters = waterLevelMeters; }
    }

    @PostMapping("/iot/river-gauge")
    public ResponseEntity<StringResponse> processIotTelemetry(@RequestBody IotTelemetryDTO telemetry) {
        log.info("Received IoT river telemetry. River: {}, Level: {}m", telemetry.getRiverName(), telemetry.getWaterLevelMeters());
        
        if (telemetry.getWaterLevelMeters() > 5.0) {
            log.warn("🚨 CRITICAL WATER LEVEL DETECTED at {}! Level: {}m. Evacuating nearby areas...", 
                    telemetry.getRiverName(), telemetry.getWaterLevelMeters());

            List<org.example.arakshasmartdisasterreliefbackend.entity.Citizen> citizens = citizenRepository.findAll();
            int notifiedCount = 0;
            
            for (org.example.arakshasmartdisasterreliefbackend.entity.Citizen citizen : citizens) {
                if (citizen.getAddress() == null || citizen.getAddress().trim().isEmpty()) {
                    continue;
                }
                
                try {
                    LatLngDTO citizenCoords = geocodingService.geocode(citizen.getAddress());
                    double dist = calculateDistance(
                            telemetry.getLatitude(), telemetry.getLongitude(),
                            citizenCoords.getLatitude(), citizenCoords.getLongitude()
                    );
                    
                    if (dist <= 5.0) {
                        log.info("Dispatching evacuation SMS warning to: {} (Dist: {} km)", citizen.getFullName(), dist);
                        smsService.sendSms(
                                citizen.getPhoneNumber() != null ? citizen.getPhoneNumber() : "+94770000000",
                                String.format("🚨 Araksha Critical Early Evacuation Warning: %s water levels have exceeded danger limits at %.2fm. Please move to safety immediately.", 
                                        telemetry.getRiverName(), telemetry.getWaterLevelMeters())
                        );
                        notifiedCount++;
                    }
                } catch (Exception e) {
                    log.error("Failed to process warning for citizen {}: {}", citizen.getFullName(), e.getMessage());
                }
            }
            
            return ResponseEntity.ok(new StringResponse("Alerts dispatched. Notified citizens within 5km: " + notifiedCount));
        }
        
        return ResponseEntity.ok(new StringResponse("Water level within safe parameters: " + telemetry.getWaterLevelMeters() + "m"));
    }

    // ── P2P Resource Mutual Aid Registry ──────────────────────────────────────
    @PostMapping("/aid/register")
    public ResponseEntity<org.example.arakshasmartdisasterreliefbackend.entity.MutualAidItem> registerAidItem(
            @RequestBody org.example.arakshasmartdisasterreliefbackend.entity.MutualAidItem item) {
        log.info("Registering P2P Aid Item. Type: {}, Item: {}", item.getType(), item.getItemType());
        org.example.arakshasmartdisasterreliefbackend.entity.MutualAidItem saved = mutualAidRepository.save(item);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/aid/matches")
    public ResponseEntity<List<org.example.arakshasmartdisasterreliefbackend.entity.MutualAidItem>> getAidMatches(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam String type) {
        
        log.info("Request to fetch matched aid items for type: {} near lat: {}, lng: {}", type, lat, lng);
        
        String oppositeType = "OFFER".equalsIgnoreCase(type) ? "NEED" : "OFFER";
        List<org.example.arakshasmartdisasterreliefbackend.entity.MutualAidItem> oppositeItems = mutualAidRepository.findByType(oppositeType);
        
        oppositeItems.sort((a, b) -> {
            double distA = calculateDistance(lat, lng, a.getLatitude(), a.getLongitude());
            double distB = calculateDistance(lat, lng, b.getLatitude(), b.getLongitude());
            return Double.compare(distA, distB);
        });
        
        return ResponseEntity.ok(oppositeItems);
    }

    // ── Multi-Stop Route Optimizer (Greedy Nearest Neighbor TSP) ─────────────
    @GetMapping("/maps/multi-route")
    public ResponseEntity<RouteResponseDTO> getMultiRoute(
            @RequestParam double startLat,
            @RequestParam double startLng,
            @RequestParam List<Long> incidentIds) {
        
        log.info("REST request to calculate optimized multi-stop route. Incidents count: {}", incidentIds.size());
        
        if (incidentIds.isEmpty()) {
            RouteResponseDTO empty = new RouteResponseDTO();
            empty.setCoordinates(List.of());
            empty.setDistanceKm(0.0);
            empty.setDurationMinutes(0.0);
            return ResponseEntity.ok(empty);
        }

        List<LatLngDTO> stops = new java.util.ArrayList<>();
        for (Long id : incidentIds) {
            try {
                EmergencyRequestResponse req = emergencyRequestService.getEmergencyRequestById(id);
                if (req != null && req.getLatitude() != null && req.getLongitude() != null) {
                    LatLngDTO pt = new LatLngDTO();
                    pt.setLatitude(req.getLatitude());
                    pt.setLongitude(req.getLongitude());
                    stops.add(pt);
                }
            } catch (Exception e) {
                log.error("Could not fetch coordinate for emergency request ID: {}", id);
            }
        }

        List<LatLngDTO> orderedStops = new java.util.ArrayList<>();
        double currentLat = startLat;
        double currentLng = startLng;
        
        while (!stops.isEmpty()) {
            int nearestIdx = 0;
            double minDist = Double.MAX_VALUE;
            
            for (int i = 0; i < stops.size(); i++) {
                double dist = calculateDistance(currentLat, currentLng, stops.get(i).getLatitude(), stops.get(i).getLongitude());
                if (dist < minDist) {
                    minDist = dist;
                    nearestIdx = i;
                }
            }
            
            LatLngDTO nextStop = stops.remove(nearestIdx);
            orderedStops.add(nextStop);
            currentLat = nextStop.getLatitude();
            currentLng = nextStop.getLongitude();
        }

        List<LatLngDTO> finalPath = new java.util.ArrayList<>();
        double totalDistance = 0.0;
        double totalDuration = 0.0;
        
        double segmentStartLat = startLat;
        double segmentStartLng = startLng;
        
        for (LatLngDTO stop : orderedStops) {
            RouteResponseDTO segmentRoute = googleMapsService.getRoute(segmentStartLat, segmentStartLng, stop.getLatitude(), stop.getLongitude());
            if (segmentRoute != null) {
                if (segmentRoute.getCoordinates() != null) {
                    finalPath.addAll(segmentRoute.getCoordinates());
                }
                totalDistance += segmentRoute.getDistanceKm();
                totalDuration += segmentRoute.getDurationMinutes();
            }
            segmentStartLat = stop.getLatitude();
            segmentStartLng = stop.getLongitude();
        }

        RouteResponseDTO response = new RouteResponseDTO();
        response.setCoordinates(finalPath);
        response.setDistanceKm(totalDistance);
        response.setDurationMinutes(totalDuration);
        response.setStartAddress("Volunteer Starting Location");
        response.setEndAddress("Final Incident Delivery Spot");
        
        return ResponseEntity.ok(response);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @PostMapping("/shelters/{id}/audit")
    public ResponseEntity<StringResponse> auditShelterCapacity(
            @PathVariable Integer id,
            @RequestParam Integer occupied) {
        
        log.info("Auditing shelter capacity. ID: {}, Occupied: {}", id, occupied);
        
        org.example.arakshasmartdisasterreliefbackend.entity.Shelter shelter = 
                shelterRepository.findById(id).orElse(null);
        if (shelter == null) {
            return ResponseEntity.notFound().build();
        }
        
        shelter.setOccupied(occupied);
        if (occupied >= shelter.getCapacity()) {
            shelter.setStatus("Full");
        } else if (occupied >= (shelter.getCapacity() * 0.9)) {
            shelter.setStatus("Limited");
        } else {
            shelter.setStatus("Available");
        }
        shelterRepository.save(shelter);
        
        int waterNeeded = occupied * 3;
        int foodNeeded = occupied * 2;
        int medicalNeeded = (int) Math.ceil(occupied * 0.5);
        
        List<org.example.arakshasmartdisasterreliefbackend.entity.Inventory> items = inventoryRepository.findAll();
        for (org.example.arakshasmartdisasterreliefbackend.entity.Inventory item : items) {
            if (item.getName().toLowerCase().contains("water")) {
                item.setCount(Math.max(0, item.getCount() - waterNeeded));
                item.setAllocated(item.getAllocated() + waterNeeded);
                inventoryRepository.save(item);
            } else if (item.getName().toLowerCase().contains("food")) {
                item.setCount(Math.max(0, item.getCount() - foodNeeded));
                item.setAllocated(item.getAllocated() + foodNeeded);
                inventoryRepository.save(item);
            } else if (item.getName().toLowerCase().contains("medical")) {
                item.setCount(Math.max(0, item.getCount() - medicalNeeded));
                item.setAllocated(item.getAllocated() + medicalNeeded);
                inventoryRepository.save(item);
            }
        }
        
        String msg = String.format("Shelter %s audited. New Occupancy: %d/%d. Allocated: Water %d L, Food %d kits, Medical %d kits.",
                shelter.getName(), occupied, shelter.getCapacity(), waterNeeded, foodNeeded, medicalNeeded);
        log.info(msg);
        return ResponseEntity.ok(new StringResponse(msg));
    }

    // Helper static class to wrap responses nicely
    private static class StringResponse {
        public String message;
        public StringResponse(String message) { this.message = message; }
    }
}
