package org.example.arakshasmartdisasterreliefbackend.controller;

import org.example.arakshasmartdisasterreliefbackend.entity.Incident;
import org.example.arakshasmartdisasterreliefbackend.repository.IncidentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/incidents")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class IncidentController {

    private final IncidentRepository incidentRepository;

    public IncidentController(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    @PostMapping
    public ResponseEntity<Incident> createIncident(@RequestBody Incident incident) {
        return ResponseEntity.ok(incidentRepository.save(incident));
    }
}
