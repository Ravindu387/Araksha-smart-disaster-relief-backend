package org.example.arakshasmartdisasterreliefbackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * EmailService handles sending plain-text emails via JavaMailSender.
 * We use SimpleMailMessage for the OTP email (no HTML templates needed).
 *
 * Spring auto-configures JavaMailSender from spring.mail.* properties.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Sends the OTP email to the user's registered email address.
     *
     * @param toEmail   The recipient's email address
     * @param otp       The 6-digit OTP code
     */
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Reset Your Password – Araksha ADRMS");
            message.setText(buildEmailBody(otp));
            mailSender.send(message);
            log.info("OTP email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send OTP email. Please try again later.");
        }
    }

    /**
     * Builds the plain-text email body with the OTP.
     */
    private String buildEmailBody(String otp) {
        return """
                Hello,
                
                We received a request to reset your password for your Araksha ADRMS account.
                
                Your One-Time Password (OTP) is:
                
                %s
                
                This OTP is valid for 5 minutes.
                
                If you did not request this, please ignore this email. Your account remains secure.
                
                Thank you,
                Araksha ADRMS Team
                """.formatted(otp);
    }
}
