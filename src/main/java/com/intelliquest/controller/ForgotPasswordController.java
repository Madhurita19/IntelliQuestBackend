package com.intelliquest.controller;

import com.intelliquest.model.User;
import com.intelliquest.service.OtpService;
import com.intelliquest.service.UserService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class ForgotPasswordController {

    @Autowired
    private OtpService otpService;

    @Autowired
    private UserService userService;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> sendOtpForPasswordReset(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required.");
        }

        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("No user found with the provided email.");
        }

        try {
            String otp = otpService.generateOtp();
            otpService.sendOtpEmail(email, otp);
            otpService.storeOtp(email, otp);
            return ResponseEntity.ok("OTP sent successfully to " + email);
        } catch (MessagingException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to send OTP email.");
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");

        if (email == null || email.isEmpty() || otp == null || otp.isEmpty()) {
            return ResponseEntity.badRequest().body("Email and OTP are required.");
        }

        boolean isValid = otpService.verifyOtp(email, otp);
        if (isValid) {
            otpService.clearOtp(email);
            return ResponseEntity.ok("OTP verified successfully.");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired OTP.");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");

        if (email == null || email.isEmpty() || newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity.badRequest().body("Email and new password are required.");
        }

        if (!otpService.isEmailVerified(email)) {
            return ResponseEntity.badRequest().body("OTP verification required before resetting password.");
        }

        boolean success = userService.resetPassword(email, newPassword);
        if (!success) {
            return ResponseEntity.badRequest().body("No user found with the provided email.");
        }

        otpService.clearVerification(email);
        return ResponseEntity.ok("Password has been reset successfully.");
    }
}
