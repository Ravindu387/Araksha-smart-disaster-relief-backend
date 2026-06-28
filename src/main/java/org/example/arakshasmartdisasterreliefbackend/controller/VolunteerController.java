package org.example.arakshasmartdisasterreliefbackend.controller;

import org.example.arakshasmartdisasterreliefbackend.dto.request.VolunteerRequest;
import org.example.arakshasmartdisasterreliefbackend.dto.request.VolunteerResponse;
import org.example.arakshasmartdisasterreliefbackend.service.VolunteerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/volunteers")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class VolunteerController {

    private final VolunteerService volunteerService;

    // Create Volunteer
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VolunteerResponse createVolunteer(
            @Valid @RequestBody VolunteerRequest request) {

        return volunteerService.createVolunteer(request);
    }

    // Get All Volunteers
    @GetMapping
    public List<VolunteerResponse> getAllVolunteers() {

        return volunteerService.getAllVolunteers();
    }

    // Get Volunteer By ID
    @GetMapping("/{id}")
    public VolunteerResponse getVolunteerById(
            @PathVariable Long id) {

        return volunteerService.getVolunteerById(id);
    }

    // Update Volunteer
    @PutMapping("/{id}")
    public VolunteerResponse updateVolunteer(
            @PathVariable Long id,
            @Valid @RequestBody VolunteerRequest request) {

        return volunteerService.updateVolunteer(id, request);
    }

    // Delete Volunteer
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVolunteer(
            @PathVariable Long id) {

        volunteerService.deleteVolunteer(id);
    }

}