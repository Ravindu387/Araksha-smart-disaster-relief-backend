package org.example.arakshasmartdisasterreliefbackend.controller;


import lombok.RequiredArgsConstructor;
import org.example.arakshasmartdisasterreliefbackend.dto.request.TaskRequest;
import org.example.arakshasmartdisasterreliefbackend.dto.response.TaskResponse;
import org.example.arakshasmartdisasterreliefbackend.service.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")

public class TaskController {

    private final TaskService service;

    @PostMapping
    public TaskResponse create(
            @RequestBody TaskRequest request){

        return service.create(request);

    }
    @GetMapping("/volunteer/{id}")
    public List<TaskResponse> getVolunteerTasks(
            @PathVariable Long id){

        return service.getVolunteerTasks(id);

    }
    @PutMapping("/{id}/complete")
    public TaskResponse complete(
            @PathVariable Long id){

        return service.complete(id);

    }

}
