package org.example.arakshasmartdisasterreliefbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.arakshasmartdisasterreliefbackend.dto.request.VolunteerHubRequest;
import org.example.arakshasmartdisasterreliefbackend.dto.response.VolunteerHubResponse;
import org.example.arakshasmartdisasterreliefbackend.service.VolunteerHubService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/volunteerhub")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")

public class VolunteerHubController {

    private final VolunteerHubService service;

    @PostMapping
    public ResponseEntity<VolunteerHubResponse> save(
            @RequestBody VolunteerHubRequest request){

        return ResponseEntity.ok(
                service.save(request)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<VolunteerHubResponse> getById(
            @PathVariable Long id){

        return ResponseEntity.ok(
                service.getById(id)
        );
    }
    @GetMapping
    public ResponseEntity<List<VolunteerHubResponse>> getAll(){

        return ResponseEntity.ok(
                service.getAll()
        );

    }
    @GetMapping("/by-email/{email}")
    public ResponseEntity<VolunteerHubResponse> getByEmail(
            @PathVariable String email){
        return ResponseEntity.ok(
                service.getByEmail(email)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @PathVariable Long id){

        service.delete(id);

        return ResponseEntity.ok(
                "Volunteer deleted"
        );
    }
}
