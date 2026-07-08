package org.example.arakshasmartdisasterreliefbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.arakshasmartdisasterreliefbackend.dto.request.PasswordChangeRequest;
import org.example.arakshasmartdisasterreliefbackend.dto.request.UpdateUserPayload;
import org.example.arakshasmartdisasterreliefbackend.dto.response.UserDTO;
import org.example.arakshasmartdisasterreliefbackend.entity.User;
import org.example.arakshasmartdisasterreliefbackend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "${cors.allowed-origins}")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Principal principal) {
        User user = getCurrentUserEntity(principal);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        return ResponseEntity.ok(mapToDTO(user));
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateCurrentUser(Principal principal, @RequestBody UpdateUserPayload payload) {
        User user = getCurrentUserEntity(principal);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        if (payload.getName() != null && !payload.getName().isBlank()) {
            String[] nameParts = payload.getName().split(" ", 2);
            user.setFirstName(nameParts[0]);
            user.setLastName(nameParts.length > 1 ? nameParts[1] : "");
        }
        if (payload.getEmail() != null && !payload.getEmail().isBlank()) {
            user.setEmail(payload.getEmail());
        }
        user.setDepartment(payload.getDepartment());

        User saved = userRepository.save(user);
        return ResponseEntity.ok(mapToDTO(saved));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(Principal principal, @RequestBody PasswordChangeRequest request) {
        User user = getCurrentUserEntity(principal);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Incorrect current password"));
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    private User getCurrentUserEntity(Principal principal) {
        if (principal == null) {
            return userRepository.findAll().stream().findFirst().orElse(null);
        }
        return userRepository.findByEmail(principal.getName()).orElse(null);
    }

    private UserDTO mapToDTO(User user) {
        String initials = "";
        if (user.getFirstName() != null && !user.getFirstName().isEmpty()) {
            initials += user.getFirstName().substring(0, 1);
        }
        if (user.getLastName() != null && !user.getLastName().isEmpty()) {
            initials += user.getLastName().substring(0, 1);
        }
        initials = initials.toUpperCase();

        return UserDTO.builder()
                .id(user.getId())
                .name(user.getFirstName() + " " + user.getLastName())
                .email(user.getEmail())
                .role(user.getRole() != null ? user.getRole().name() : "ADMIN")
                .department(user.getDepartment())
                .initials(initials)
                .build();
    }

    @lombok.Getter
    @lombok.AllArgsConstructor
    private static class ErrorResponse {
        private String message;
    }
}
