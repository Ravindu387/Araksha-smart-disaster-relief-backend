package org.example.arakshasmartdisasterreliefbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.arakshasmartdisasterreliefbackend.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:YOUR_GMAIL@gmail.com}")
    private String fromEmail;

    @Override
    public void sendEmail(String to, String subject, String body) {
        log.info("Preparing to send email to: {}", to);

        if (isEmailConfigMissing()) {
            log.info("[SIMULATED EMAIL] Send Success -> To: {}, Subject: \"{}\", Body:\n{}", to, subject, body);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // true means HTML message

            mailSender.send(message);
            log.info("Email successfully dispatched to: {}", to);
        } catch (Exception e) {
            log.error("SMTP Email transmission failed to: {}", to, e);
            // Fallback to text email
            try {
                SimpleMailMessage textMessage = new SimpleMailMessage();
                textMessage.setFrom(fromEmail);
                textMessage.setTo(to);
                textMessage.setSubject(subject);
                textMessage.setText(body.replaceAll("<[^>]*>", "")); // strip HTML tags
                mailSender.send(textMessage);
                log.info("Plaintext fallback email successfully dispatched to: {}", to);
            } catch (Exception ex) {
                log.error("Plaintext fallback email failed as well", ex);
            }
        }
    }

    @Override
    public void sendVolunteerAssignmentEmail(String toEmail, String volunteerName, String requestId, String location, String instructions) {
        String subject = "Araksha Relief - Emergency Assignment: " + requestId;
        String body = String.format(
                "<h2>Emergency Mission Assignment</h2>" +
                "<p>Dear %s,</p>" +
                "<p>You have been assigned to assist with a disaster relief operation.</p>" +
                "<table>" +
                "  <tr><td><b>Request ID:</b></td><td>%s</td></tr>" +
                "  <tr><td><b>Location:</b></td><td>%s</td></tr>" +
                "</table>" +
                "<p><b>Instructions:</b><br/>%s</p>" +
                "<p>Please log in to your dashboard to view directions and contact info.</p>" +
                "<br/>" +
                "<p>Thank you for your service,<br/>Araksha Disaster Management Operations Center</p>",
                volunteerName, requestId, location, instructions != null ? instructions : "Coordinate with sector leads on-site.");
        sendEmail(toEmail, subject, body);
    }

    @Override
    public void sendEmergencyConfirmationEmail(String toEmail, String citizenName, String requestId, String emergencyType, String location) {
        String subject = "Araksha Relief - Emergency Request Received: " + requestId;
        String body = String.format(
                "<h2>Emergency Request Confirmed</h2>" +
                "<p>Dear %s,</p>" +
                "<p>We have successfully received your report for a <b>%s</b> incident at <b>%s</b>.</p>" +
                "<p><b>Request ID:</b> %s</p>" +
                "<p>Our dispatch team is currently reviewing your request and allocating volunteers. Please remain safe.</p>" +
                "<br/>" +
                "<p>Warm regards,<br/>Araksha Disaster Relief System</p>",
                citizenName, emergencyType, location, requestId);
        sendEmail(toEmail, subject, body);
    }

    @Override
    public void sendShelterNotificationEmail(String toEmail, String shelterName, String status, int occupied, int capacity) {
        String subject = "Araksha Relief - Shelter Alert: " + shelterName;
        String body = String.format(
                "<h2>Shelter Capacity Advisory</h2>" +
                "<p>This is to inform that the shelter <b>%s</b> is now at <b>%s</b> status.</p>" +
                "<ul>" +
                "  <li><b>Total Occupied:</b> %d beds</li>" +
                "  <li><b>Total Capacity:</b> %d beds</li>" +
                "  <li><b>Occupancy Rate:</b> %.1f%%</li>" +
                "</ul>" +
                "<p>Please review current housing arrangements for affected citizens.</p>" +
                "<br/>" +
                "<p>Sincerely,<br/>Araksha Shelters Coordinator</p>",
                shelterName, status, occupied, capacity, ((double) occupied / capacity) * 100);
        sendEmail(toEmail, subject, body);
    }

    @Override
    public void sendDailySummaryReportEmail(String toEmail, int totalEmergencies, int activeVolunteers, double shelterOccupancy, int lowStockResources, int pendingRequests) {
        String subject = "Araksha Relief - System Status Daily Summary Report";
        String body = String.format(
                "<h2>Daily Disaster Relief System Summary</h2>" +
                "<p>Below is the system activity statistics for today:</p>" +
                "<table border='1' cellpadding='5' style='border-collapse: collapse;'>" +
                "  <tr style='background: #f2f2f2;'><th>Metric</th><th>Count/Status</th></tr>" +
                "  <tr><td><b>Total Reported Emergencies</b></td><td>%d</td></tr>" +
                "  <tr><td><b>Pending Requests</b></td><td>%d</td></tr>" +
                "  <tr><td><b>Active On-Duty Volunteers</b></td><td>%d</td></tr>" +
                "  <tr><td><b>Average Shelter Occupancy</b></td><td>%.1f%%</td></tr>" +
                "  <tr><td><b>Low Stock Inventory Items</b></td><td>%d</td></tr>" +
                "</table>" +
                "<p>Please log in to the administrator panel for detailed report logs.</p>" +
                "<br/>" +
                "<p>Automated Scheduler Center,<br/>Araksha Relief Operations</p>",
                totalEmergencies, pendingRequests, activeVolunteers, shelterOccupancy, lowStockResources);
        sendEmail(toEmail, subject, body);
    }
    @Override
    public void sendOtpEmail(String toEmail, String otp) {

        String subject = "Araksha Password Reset OTP";

        String body = String.format("""
            <html>
            <body style="font-family:Arial,sans-serif">

            <h2>Password Reset Verification</h2>

            <p>You requested to reset your password.</p>

            <p>Your OTP is:</p>

            <h1 style="color:#2563EB;">%s</h1>

            <p>This OTP is valid for <b>5 minutes</b>.</p>

            <p>If you did not request this request, please ignore this email.</p>

            <br>

            <p>Regards,<br>
            Araksha Smart Disaster Relief System</p>

            </body>
            </html>
            """, otp);

        sendEmail(toEmail, subject, body);
    }

    private boolean isEmailConfigMissing() {
        return fromEmail == null || fromEmail.trim().isEmpty() || "YOUR_GMAIL@gmail.com".equals(fromEmail);
    }
}
