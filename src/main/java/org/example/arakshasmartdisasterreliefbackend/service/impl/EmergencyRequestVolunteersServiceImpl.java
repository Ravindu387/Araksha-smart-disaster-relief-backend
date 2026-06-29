package org.example.arakshasmartdisasterreliefbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.arakshasmartdisasterreliefbackend.dto.response.EmergencyResponseVolunteers;
import org.example.arakshasmartdisasterreliefbackend.entity.EmergencyRequestVolunteers;
import org.example.arakshasmartdisasterreliefbackend.repository.EmergencyRequestVolunteersRepository;
import org.example.arakshasmartdisasterreliefbackend.service.EmergencyRequestVolunteersService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmergencyRequestVolunteersServiceImpl implements EmergencyRequestVolunteersService{

        private final EmergencyRequestVolunteersRepository repository;

        @Override
        public EmergencyResponseVolunteers create(EmergencyRequestVolunteers request){


            EmergencyRequestVolunteers emergency =
                    EmergencyRequestVolunteers.builder()

                            .requestCode(request.getRequestCode())
                            .title(request.getTitle())
                            .description(request.getDescription())
                            .citizenName(request.getCitizenName())
                            .contact(request.getContact())
                            .address(request.getAddress())
                            .priority(request.getPriority())
                            .status(request.getStatus())
                            .latitude(request.getLatitude())
                            .longitude(request.getLongitude())

                            .build();

            return map(repository.save(emergency));

        }

        @Override
        public List<EmergencyResponseVolunteers> getOpenRequests(){


            return repository.findByStatus("OPEN")
                    .stream()
                    .map(this::map)
                    .toList();

        }

        @Override
        public EmergencyResponseVolunteers getByCode(String code){


            return repository.findByRequestCode(code)
                    .map(this::map)
                    .orElseThrow();


        }

        private EmergencyResponseVolunteers map(EmergencyRequestVolunteers e){


            return EmergencyResponseVolunteers.builder()

                    .requestCode(e.getRequestCode())
                    .title(e.getTitle())
                    .description(e.getDescription())
                    .citizenName(e.getCitizenName())
                    .contact(e.getContact())
                    .address(e.getAddress())
                    .priority(e.getPriority())

                    .build();

        }

}

