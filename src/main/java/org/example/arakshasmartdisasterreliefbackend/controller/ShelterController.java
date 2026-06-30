package org.example.arakshasmartdisasterreliefbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.arakshasmartdisasterreliefbackend.dto.request.ShelterRequestDTO;
import org.example.arakshasmartdisasterreliefbackend.service.ShelterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shelters")
@RequiredArgsConstructor
@CrossOrigin
public class ShelterController {

    private final ShelterService shelterService;

    @PostMapping
    public ResponseEntity<ShelterRequestDTO> save(@RequestBody ShelterRequestDTO dto) {
        return ResponseEntity.ok(shelterService.save(dto));
    }

    @GetMapping
    public ResponseEntity<List<ShelterRequestDTO>> getAll() {
        return ResponseEntity.ok(shelterService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShelterRequestDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(shelterService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShelterRequestDTO> update(
            @PathVariable Integer id,
            @RequestBody ShelterRequestDTO dto) {

        return ResponseEntity.ok(shelterService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {

        shelterService.delete(id);

        return ResponseEntity.noContent().build();
    }
}