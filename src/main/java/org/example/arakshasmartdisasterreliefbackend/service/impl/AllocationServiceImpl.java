package org.example.arakshasmartdisasterreliefbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.arakshasmartdisasterreliefbackend.entity.Allocation;
import org.example.arakshasmartdisasterreliefbackend.repository.AllocationRepository;
import org.example.arakshasmartdisasterreliefbackend.service.AllocationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AllocationServiceImpl implements AllocationService {

    private final AllocationRepository repository;

    @Override
    public List<Allocation> getAllAllocations() {
        return repository.findAll();
    }

    @Override
    public Allocation createAllocation(Allocation allocation) {
        return repository.save(allocation);
    }
}