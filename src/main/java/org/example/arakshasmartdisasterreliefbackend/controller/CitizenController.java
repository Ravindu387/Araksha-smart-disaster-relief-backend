package org.example.arakshasmartdisasterreliefbackend.controller;

import org.example.arakshasmartdisasterreliefbackend.entity.Citizen;
import org.example.arakshasmartdisasterreliefbackend.service.CitizenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/citizens")
@CrossOrigin(origins = "http://localhost:4200")
public class CitizenController {

    private final CitizenService citizenService;

    public CitizenController(CitizenService citizenService) {
        this.citizenService = citizenService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<Citizen> createCitizen(@RequestBody Citizen citizen) {
        Citizen savedCitizen = citizenService.saveCitizen(citizen);
        return new ResponseEntity<>(savedCitizen, HttpStatus.CREATED);
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<Citizen>> getAllCitizens() {
        return ResponseEntity.ok(citizenService.getAllCitizens());
    }

    // READ ONE
    @GetMapping("/{id}")
    public ResponseEntity<Citizen> getCitizenById(@PathVariable Long id) {

        Optional<Citizen> citizen = citizenService.getCitizenById(id);

        return citizen.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // READ BY EMAIL
    @GetMapping("/email/{email}")
    public ResponseEntity<Citizen> getCitizenByEmail(@PathVariable String email) {

        Optional<Citizen> citizen = citizenService.getCitizenByEmail(email);

        return citizen.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Citizen> updateCitizen(
            @PathVariable Long id,
            @RequestBody Citizen citizen) {

        Citizen updatedCitizen = citizenService.updateCitizen(id, citizen);

        return ResponseEntity.ok(updatedCitizen);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCitizen(@PathVariable Long id) {

        citizenService.deleteCitizen(id);

        return ResponseEntity.noContent().build();
    }

}