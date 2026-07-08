package org.example.arakshasmartdisasterreliefbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.arakshasmartdisasterreliefbackend.config.JwtService;
import org.example.arakshasmartdisasterreliefbackend.dto.request.LoginRequest;
import org.example.arakshasmartdisasterreliefbackend.dto.request.RegisterRequest;
import org.example.arakshasmartdisasterreliefbackend.dto.response.LoginResponse;
import org.example.arakshasmartdisasterreliefbackend.entity.User;
import org.example.arakshasmartdisasterreliefbackend.repository.UserRepository;
import org.example.arakshasmartdisasterreliefbackend.service.AuthService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.example.arakshasmartdisasterreliefbackend.entity.Citizen;
import org.example.arakshasmartdisasterreliefbackend.entity.Volunteer;
import org.example.arakshasmartdisasterreliefbackend.entity.VolunteerHub;
import org.example.arakshasmartdisasterreliefbackend.repository.CitizenRepository;
import org.example.arakshasmartdisasterreliefbackend.repository.VolunteerRepository;
import org.example.arakshasmartdisasterreliefbackend.repository.VolunteerHubRepository;
import org.example.arakshasmartdisasterreliefbackend.enums.Role;

import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final CitizenRepository citizenRepository;
    private final VolunteerRepository volunteerRepository;
    private final VolunteerHubRepository volunteerHubRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public String register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return "Email already exists";
        }

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        userRepository.save(user);

        /* ---------------- Save Role Entity ---------------- */

        if (request.getRole() == Role.CITIZEN) {
            Citizen citizen = new Citizen();
            citizen.setFullName(request.getFirstName() + " " + request.getLastName());
            citizen.setEmail(request.getEmail());
            citizen.setPassword(user.getPassword());
            citizen.setPhoneNumber(request.getPhone() != null ? request.getPhone() : "");
            citizen.setAddress("");
            citizenRepository.save(citizen);
        } else if (request.getRole() == Role.VOLUNTEER) {
            String volunteerPhone = (request.getPhone() != null && !request.getPhone().isBlank()) 
                    ? request.getPhone() 
                    : "V-PHONE-" + UUID.randomUUID().toString().substring(0, 8);
            
            Volunteer volunteer = new Volunteer();
            volunteer.setName(request.getFirstName() + " " + request.getLastName());
            volunteer.setLocation(request.getLocation() != null && !request.getLocation().isBlank() ? request.getLocation() : "Colombo");
            volunteer.setStatus("Available");
            volunteer.setRating(5.0);
            volunteer.setTasks(0);
            volunteer.setPhone(volunteerPhone);
            volunteer.setEmail(request.getEmail());
            volunteer.setSkills(request.getSkills() != null ? request.getSkills() : new ArrayList<>());
            volunteer.setProfilePhotoUrl(request.getProfilePhotoUrl());
            volunteer.setIdVerificationDocUrl(request.getIdVerificationDocUrl());
            volunteerRepository.save(volunteer);

            VolunteerHub volunteerHub = VolunteerHub.builder()
                    .volunteerCode("V-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase())
                    .name(request.getFirstName() + " " + request.getLastName())
                    .email(request.getEmail())
                    .phone(volunteerPhone)
                    .status("Available")
                    .available(true)
                    .currentLatitude(6.9271) // Default to Colombo coordinates
                    .currentLongitude(79.8612)
                    .address(request.getLocation() != null && !request.getLocation().isBlank() ? request.getLocation() : "Colombo")
                    .district(request.getLocation() != null && !request.getLocation().isBlank() ? request.getLocation() : "Colombo")
                    .skills(request.getSkills() != null ? request.getSkills() : new ArrayList<>())
                    .build();
            volunteerHubRepository.save(volunteerHub);
        }

        /* ---------------------------------------------- */

        return "Registration Successful";}

    @Override
    public LoginResponse login(LoginRequest request) {

        System.out.println("========== LOGIN ==========");
        System.out.println("Email : " + request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        System.out.println("DB Password : " + user.getPassword());
        System.out.println("Entered Password : " + request.getPassword());

        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());

        System.out.println("Password Matches : " + matches);

        if (!matches) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().toString());

        return response;
    }
}
