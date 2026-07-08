package org.example.arakshasmartdisasterreliefbackend.service;

import org.example.arakshasmartdisasterreliefbackend.dto.response.ReportsPageResponse;
import org.example.arakshasmartdisasterreliefbackend.dto.response.VolunteerPerformanceDto;

import java.util.List;


public interface ReportsService {


    ReportsPageResponse getReportsByPeriod(String period);


    List<VolunteerPerformanceDto> getTopVolunteers();
}
