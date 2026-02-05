package com.intelliquest.controller;

import com.intelliquest.dto.FeedbackRequestDTO;
import com.intelliquest.service.FeedbackService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping("/feedback")
    public ResponseEntity<?> submitFeedback(@RequestBody FeedbackRequestDTO request) {
        try {
            feedbackService.sendFeedbackEmail(request);
            return ResponseEntity.ok("Feedback submitted successfully.");
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Failed to send feedback: " + e.getMessage());
        }
    }
}