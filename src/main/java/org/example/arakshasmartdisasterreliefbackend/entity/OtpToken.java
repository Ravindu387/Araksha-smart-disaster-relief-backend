package org.example.arakshasmartdisasterreliefbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * OTP Token entity.
 * Stores one OTP record per password reset request.
 * Each OTP has a 5-minute expiry, a used flag,
 * and is tied to a specific email address.
 */
@Entity
@Table(name = "otp_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The email address the OTP was sent to */
    @Column(nullable = false)
    private String email;

    /** 6-digit OTP code */
    @Column(nullable = false, length = 6)
    private String otp;

    /** Timestamp when this OTP expires (createdAt + 5 minutes) */
    @Column(nullable = false)
    private LocalDateTime expiryTime;

    /** Whether this OTP has already been used */
    @Column(nullable = false)
    private boolean used = false;

    /** Timestamp when this record was created */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.expiryTime = this.createdAt.plusMinutes(5);
    }
}
