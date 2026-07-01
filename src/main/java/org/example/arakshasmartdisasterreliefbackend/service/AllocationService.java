package org.example.arakshasmartdisasterreliefbackend.service;

import org.example.arakshasmartdisasterreliefbackend.entity.Allocation;

import java.util.List;

public interface AllocationService {
    List<Allocation> getAllAllocations();
    Allocation createAllocation(Allocation allocation);
}