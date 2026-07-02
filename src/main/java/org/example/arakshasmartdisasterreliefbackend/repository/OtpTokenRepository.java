package org.example.arakshasmartdisasterreliefbackend.repository;

import org.example.arakshasmartdisasterreliefbackend.entity.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository for OtpToken records.
 * Provides custom queries to find, validate, and clean up OTP records.
 */
@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {

    /**
     * Find the most recent unused, non-expired OTP for an email.
     * We match both email and otp so brute-force guessing is mitigated.
     */
    Optional<OtpToken> findTopByEmailAndOtpAndUsedFalseAndExpiryTimeAfterOrderByCreatedAtDesc(
            String email,
            String otp,
            LocalDateTime now
    );

    /**
     * Delete all OTPs for a given email (called after successful password reset
     * to clean up all previous OTPs for that email).
     */
    @Modifying
    @Query("DELETE FROM OtpToken o WHERE o.email = :email")
    void deleteAllByEmail(@Param("email") String email);

    /**
     * Delete all expired OTPs – call this periodically or before inserting new ones
     * to keep the table clean.
     */
    @Modifying
    @Query("DELETE FROM OtpToken o WHERE o.expiryTime < :now")
    void deleteAllExpired(@Param("now") LocalDateTime now);
}
