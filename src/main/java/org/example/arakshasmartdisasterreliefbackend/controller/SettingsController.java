package org.example.arakshasmartdisasterreliefbackend.controller;

import org.example.arakshasmartdisasterreliefbackend.service.SettingsService;
import org.example.arakshasmartdisasterreliefbackend.dto.request.SettingsDTO;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/settings")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class SettingsController {

    private final SettingsService service;

    public SettingsController(SettingsService service) {
        this.service = service;
    }

    @GetMapping
    public SettingsDTO getSettings() {
        return service.getSettings();
    }

    @PutMapping
    public SettingsDTO updateSettings(@RequestBody SettingsDTO dto) {
        return service.updateSettings(dto);
    }
}
