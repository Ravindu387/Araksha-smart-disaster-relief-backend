package org.example.arakshasmartdisasterreliefbackend.controller;

import org.example.arakshasmartdisasterreliefbackend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "http://localhost:4200")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/test")
    public String sendTestEmail(@RequestParam String email) {

        emailService.sendEmail(
                email,
                "Araksha Email Test",
                "Congratulations! Your Spring Boot email configuration is working successfully."
        );

        return "Email sent successfully.";

    }

}