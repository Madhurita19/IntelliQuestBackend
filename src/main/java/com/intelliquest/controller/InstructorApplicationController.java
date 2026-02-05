package com.intelliquest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.intelliquest.dto.InstructorApplicationRequestDTO;
import com.intelliquest.service.ApplyInstructorMailService;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/auth")
public class InstructorApplicationController {

    @Autowired
    private ApplyInstructorMailService mailService;

    @PostMapping("/apply-for-instructor")
    public ResponseEntity<String> applyInstructor(@RequestBody InstructorApplicationRequestDTO request) {
        try {
            mailService.sendInstructorApplicationEmail(
                request.getFullName(),
                request.getEmail(),
                request.getExperience(),
                request.getExpertise(),
                request.getMotivation()
            );
            return ResponseEntity.ok("Application sent via email successfully");
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send email. Please try again later.");
        }
    }
}
