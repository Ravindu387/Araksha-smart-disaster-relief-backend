package org.example.arakshasmartdisasterreliefbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.arakshasmartdisasterreliefbackend.dto.request.TaskRequest;
import org.example.arakshasmartdisasterreliefbackend.dto.response.TaskResponse;
import org.example.arakshasmartdisasterreliefbackend.entity.Task;
import org.example.arakshasmartdisasterreliefbackend.repository.TaskRepository;
import org.example.arakshasmartdisasterreliefbackend.service.TaskService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;

    @Override
    public TaskResponse create(TaskRequest request){

        Task task = Task.builder()
                .taskCode(request.getTaskCode())
                .name(request.getName())
                .location(request.getLocation())
                .distance(request.getDistance())
                .eta(request.getEta())
                .priority(request.getPriority())
                .status(request.getStatus())
                .build();

        return map(repository.save(task));

    }

    @Override
    public List<TaskResponse> getVolunteerTasks(Long id){

        return repository.findByVolunteerId(id)
                .stream()
                .map(this::map)
                .toList();

    }

    @Override
    public TaskResponse complete(Long id){

        Task task =
                repository.findById(id).orElseThrow();

        task.setStatus("COMPLETED");

        return map(repository.save(task));

    }

    private TaskResponse map(Task t){

        return TaskResponse.builder()
                .id(t.getId())
                .taskCode(t.getTaskCode())
                .name(t.getName())
                .location(t.getLocation())
                .distance(t.getDistance())
                .eta(t.getEta())
                .priority(t.getPriority())
                .status(t.getStatus())
                .build();

    }

}
