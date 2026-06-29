package org.example.arakshasmartdisasterreliefbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.arakshasmartdisasterreliefbackend.dto.response.EmergencyResponseVolunteers;
import org.example.arakshasmartdisasterreliefbackend.entity.EmergencyRequestVolunteers;
import org.example.arakshasmartdisasterreliefbackend.service.EmergencyRequestVolunteersService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emergencyvolunteers")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class EmergencyRequestVolunteersController {
    private final EmergencyRequestVolunteersService service;

    @PostMapping
    public EmergencyResponseVolunteers create(
            @RequestBody EmergencyRequestVolunteers request){

        return service.create(request);

    }
    @GetMapping("/open")
    public List<EmergencyResponseVolunteers> getOpen(){


        return service.getOpenRequests();

    }
    @GetMapping("/{code}")
    public EmergencyResponseVolunteers getByCode(
            @PathVariable String code){


        return service.getByCode(code);

    }
}
