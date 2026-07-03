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
import org.example.arakshasmartdisasterreliefbackend.repository.CitizenRepository;
import org.example.arakshasmartdisasterreliefbackend.repository.VolunteerRepository;
import org.example.arakshasmartdisasterreliefbackend.entity.VolunteerHub;
import org.example.arakshasmartdisasterreliefbackend.repository.VolunteerHubRepository;
import org.example.arakshasmartdisasterreliefbackend.enums.Role;

import java.util.ArrayList;

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
            citizen.setPhoneNumber("");
            citizen.setAddress("");
            citizenRepository.save(citizen);
        } else if (request.getRole() == Role.VOLUNTEER) {
            Volunteer volunteer = new Volunteer();
            volunteer.setName(request.getFirstName() + " " + request.getLastName());
            volunteer.setLocation("Houston, TX");
            volunteer.setStatus("Available");
            volunteer.setRating(5.0);
            volunteer.setTasks(0);
            volunteer.setPhone("");
            volunteer.setSkills(new ArrayList<>());
            volunteerRepository.save(volunteer);

            VolunteerHub volunteerHub = new VolunteerHub();
            volunteerHub.setVolunteerCode("VOL-" + String.format("%04d", (int)(Math.random() * 10000)));
            volunteerHub.setName(request.getFirstName() + " " + request.getLastName());
            volunteerHub.setEmail(request.getEmail());
            volunteerHub.setPhone("+94 77 " + String.format("%07d", (int)(Math.random() * 10000000)));
            volunteerHub.setStatus("Available");
            volunteerHub.setAvailable(true);
            volunteerHub.setCurrentLatitude(6.9271);
            volunteerHub.setCurrentLongitude(79.8612);
            volunteerHubRepository.save(volunteerHub);
        }

        /* ---------------------------------------------- */

        return "Registration Successful";}

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
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
