package org.example.arakshasmartdisasterreliefbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.arakshasmartdisasterreliefbackend.entity.Allocation;
import org.example.arakshasmartdisasterreliefbackend.service.AllocationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/allocations")
@RequiredArgsConstructor
@CrossOrigin(origins = "${cors.allowed-origins}")
public class AllocationController {

    private final AllocationService service;

    @GetMapping
    public List<Allocation> getAllAllocations() {
        return service.getAllAllocations();
    }

    @PostMapping
    public Allocation createAllocation(@RequestBody Allocation allocation) {
        return service.createAllocation(allocation);
    }
}