package org.example.arakshasmartdisasterreliefbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.arakshasmartdisasterreliefbackend.dto.request.EmergencyRequestRequest;
import org.example.arakshasmartdisasterreliefbackend.dto.response.EmergencyRequestResponse;
import org.example.arakshasmartdisasterreliefbackend.service.EmergencyRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emergency-requests")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class EmergencyRequestController {

    private final EmergencyRequestService emergencyRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmergencyRequestResponse createEmergencyRequest(
            @Valid @RequestBody EmergencyRequestRequest request) {

        return emergencyRequestService.createEmergencyRequest(request);
    }

    @GetMapping
    public List<EmergencyRequestResponse> getAllEmergencyRequests() {

        return emergencyRequestService.getAllEmergencyRequests();
    }

    @GetMapping("/{id}")
    public EmergencyRequestResponse getEmergencyRequestById(@PathVariable Long id) {

        return emergencyRequestService.getEmergencyRequestById(id);
    }

    @PutMapping("/{id}")
    public EmergencyRequestResponse updateEmergencyRequest(
            @PathVariable Long id,
            @Valid @RequestBody EmergencyRequestRequest request) {

        return emergencyRequestService.updateEmergencyRequest(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmergencyRequest(@PathVariable Long id) {

        emergencyRequestService.deleteEmergencyRequest(id);
    }
}