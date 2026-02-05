package com.intelliquest.controller;

import com.intelliquest.dto.SupportRequestDTO;
import com.intelliquest.service.SupportService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class SupportController {

    private final SupportService supportService;

    @PostMapping("/support")
    public ResponseEntity<?> sendSupportRequest(@RequestBody SupportRequestDTO request) {
        try {
            supportService.sendSupportRequest(request);
            return ResponseEntity.ok("Support request sent successfully.");
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Failed to send support request: " + e.getMessage());
        }
    }
}
