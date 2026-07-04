package org.example.arakshasmartdisasterreliefbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.arakshasmartdisasterreliefbackend.repository.PasswordResetOtpRepository;
import org.example.arakshasmartdisasterreliefbackend.service.EmailService;
import org.example.arakshasmartdisasterreliefbackend.service.ForgotPasswordService;
import org.springframework.stereotype.Service;
import org.example.arakshasmartdisasterreliefbackend.repository.CitizenRepository;
import org.example.arakshasmartdisasterreliefbackend.repository.VolunteerRepository;
import org.example.arakshasmartdisasterreliefbackend.entity.PasswordResetOtp;
import org.example.arakshasmartdisasterreliefbackend.util.OtpGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.example.arakshasmartdisasterreliefbackend.entity.Citizen;
import org.example.arakshasmartdisasterreliefbackend.entity.Volunteer;

import org.example.arakshasmartdisasterreliefbackend.repository.UserRepository;
import org.example.arakshasmartdisasterreliefbackend.entity.User;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    private final PasswordResetOtpRepository passwordResetOtpRepository;
    private final EmailService emailService;
    private final CitizenRepository citizenRepository;

    private final VolunteerRepository volunteerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void sendOtp(String email) {

        boolean exists = citizenRepository.existsByEmail(email)
                || volunteerRepository.existsByEmail(email);

        if (!exists) {
            throw new RuntimeException("No account found with this email.");
        }

        String otp = OtpGenerator.generateOtp();

        PasswordResetOtp passwordResetOtp = passwordResetOtpRepository
                .findByEmail(email)
                .orElse(new PasswordResetOtp());

        passwordResetOtp.setEmail(email);
        passwordResetOtp.setOtp(otp);
        passwordResetOtp.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        passwordResetOtpRepository.save(passwordResetOtp);

        emailService.sendOtpEmail(email, otp);
    }
    @Override
    public void verifyOtp(String email, String otp) {

        PasswordResetOtp passwordResetOtp = passwordResetOtpRepository
                .findByEmail(email)
                .orElseThrow(() -> new RuntimeException("OTP not found."));

        if (!passwordResetOtp.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP.");
        }

        if (passwordResetOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired.");
        }
    }
    @Override
    public void resetPassword(String email, String otp, String newPassword) {

        PasswordResetOtp passwordResetOtp = passwordResetOtpRepository
                .findByEmail(email)
                .orElseThrow(() -> new RuntimeException("OTP not found."));
        if (!passwordResetOtp.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP.");
        }
        if (passwordResetOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired.");
        }
        Citizen citizen = citizenRepository.findByEmail(email).orElse(null);


        if (citizen != null) {

            String encodedPassword = passwordEncoder.encode(newPassword);

            citizen.setPassword(encodedPassword);
            citizenRepository.save(citizen);

// Update users table too
            User user = userRepository.findByEmail(email).orElse(null);

            if (user != null) {
                user.setPassword(encodedPassword);
                userRepository.save(user);
            }

            passwordResetOtpRepository.delete(passwordResetOtp);
            return;
        }
        Volunteer volunteer = volunteerRepository.findByEmail(email).orElse(null);

        if (volunteer != null) {

            String encodedPassword = passwordEncoder.encode(newPassword);

            volunteer.setPassword(encodedPassword);
            volunteerRepository.save(volunteer);

// Update users table too
            User user = userRepository.findByEmail(email).orElse(null);

            if (user != null) {
                user.setPassword(encodedPassword);
                userRepository.save(user);
            }

            passwordResetOtpRepository.delete(passwordResetOtp);
            return;
        }

        throw new RuntimeException("User not found.");

    }

}