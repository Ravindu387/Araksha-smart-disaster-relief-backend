package org.example.arakshasmartdisasterreliefbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.arakshasmartdisasterreliefbackend.dto.response.*;
import org.example.arakshasmartdisasterreliefbackend.entity.Performance;
import org.example.arakshasmartdisasterreliefbackend.repository.CompletedTaskRepository;
import org.example.arakshasmartdisasterreliefbackend.repository.EmergencyRequestVolunteersRepository;
import org.example.arakshasmartdisasterreliefbackend.repository.PerformanceRepository;
import org.example.arakshasmartdisasterreliefbackend.repository.TaskRepository;
import org.example.arakshasmartdisasterreliefbackend.service.DashboardService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final TaskRepository taskRepository;
    private final EmergencyRequestVolunteersRepository emergencyRepository;
    private final PerformanceRepository performanceRepository;
    private final CompletedTaskRepository completedTaskRepository;


    @Override
    public DashboardStatsResponse getStats(Long volunteerId){


        int completed =
                completedTaskRepository
                        .findByVolunteerId(volunteerId)
                        .size();

        int active =
                taskRepository
                        .findByVolunteerId(volunteerId)
                        .size();

        int requests =
                emergencyRepository
                        .findByStatus("OPEN")
                        .size();

        return DashboardStatsResponse.builder()

                .completedTasks(completed)

                .activeTasks(active)

                .openRequests(requests)

                .rating(4.8)

                .build();


    }
    @Override
    public List<TaskResponse> getTasks(Long volunteerId){


        return taskRepository
                .findByVolunteerId(volunteerId)

                .stream()

                .map(t ->
                        TaskResponse.builder()

                                .id(t.getId())
                                .taskCode(t.getTaskCode())
                                .name(t.getName())
                                .location(t.getLocation())
                                .distance(t.getDistance())
                                .eta(t.getEta())
                                .priority(t.getPriority())
                                .status(t.getStatus())

                                .build()

                )

                .toList();

    }
    @Override
    public List<EmergencyResponseVolunteers> getRequests(){


        return emergencyRepository
                .findByStatus("OPEN")

                .stream()

                .map(e ->
                        EmergencyResponseVolunteers.builder()

                                .requestCode(e.getRequestCode())
                                .title(e.getTitle())
                                .description(e.getDescription())
                                .citizenName(e.getCitizenName())
                                .contact(e.getContact())
                                .address(e.getAddress())
                                .priority(e.getPriority())

                                .build()

                )

                .toList();

    }
    @Override
    public PerformanceResponse getPerformance(Long id){


        Performance p =
                performanceRepository
                        .findByVolunteerId(id)
                        .orElse(null);

        if(p==null){

            return PerformanceResponse.builder()
                    .response(0)
                    .feedback(0)
                    .completion(0)
                    .communication(0)
                    .safety(0)
                    .build();

        }
        return PerformanceResponse.builder()

                .response(p.getResponse())
                .feedback(p.getFeedback())
                .completion(p.getCompletion())
                .communication(p.getCommunication())
                .safety(p.getSafety())

                .build();

    }
    @Override
    public List<CompletedTaskResponse> getCompletedTasks(Long id){


        return completedTaskRepository
                .findByVolunteerId(id)

                .stream()

                .map(t ->
                        CompletedTaskResponse.builder()

                                .taskCode(t.getTaskCode())
                                .type(t.getType())
                                .rating(t.getRating())

                                .build()

                )

                .toList();

    }


}
