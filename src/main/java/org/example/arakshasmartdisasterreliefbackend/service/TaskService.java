package org.example.arakshasmartdisasterreliefbackend.service;

import org.example.arakshasmartdisasterreliefbackend.dto.request.TaskRequest;
import org.example.arakshasmartdisasterreliefbackend.dto.response.TaskResponse;

import java.util.List;

public interface TaskService {
    TaskResponse create(TaskRequest request);


    List<TaskResponse> getVolunteerTasks(Long volunteerId);


    TaskResponse complete(Long id);
}
