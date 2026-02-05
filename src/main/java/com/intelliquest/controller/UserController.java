package com.intelliquest.controller;

import com.intelliquest.dto.UserUpdateRequestDTO;
import com.intelliquest.model.User;
import com.intelliquest.model.UserProfilePicture;
import com.intelliquest.service.RoleChangeRequestService;
import com.intelliquest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;


@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/auth/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoleChangeRequestService roleChangeRequestService;

    private String extractEmail(Object principal) {
        if (principal instanceof OAuth2User oAuth2User) {
            return oAuth2User.getAttribute("email");
        } else if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        } else if (principal instanceof org.springframework.security.core.userdetails.User user) {
            return user.getUsername();
        } else if (principal instanceof String emailString) {
            return emailString;
        }
        return null;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getAuthenticatedUser(@AuthenticationPrincipal Object principal) {
        String email = extractEmail(principal);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Email not found.");
        }

        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        return ResponseEntity.ok(userOpt.get());
    }

    @GetMapping("/role")
    public ResponseEntity<?> getUserRole(@AuthenticationPrincipal Object principal) {
        String email = extractEmail(principal);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Email not found.");
        }

        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isPresent()) {
            return ResponseEntity.ok(userOpt.get().getRole());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }
    
    @PostMapping("/update-profile")
    public ResponseEntity<?> updateUserProfile(
        @AuthenticationPrincipal Object principal,
        @ModelAttribute UserUpdateRequestDTO updateRequest
    ) {
        String email = extractEmail(principal);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized.");
        }

        try {
            boolean updated = userService.updateUserProfile(email, updateRequest);
            if (updated) {
                return ResponseEntity.ok("Profile updated successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Update failed.");
        }
    }
    
    @PostMapping("/request-role-change")
    public ResponseEntity<?> requestRoleChange(
            @AuthenticationPrincipal Object principal,
            @RequestParam("newRole") String newRoleStr
    ) {
        String email = extractEmail(principal);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized.");
        }

        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        User user = userOpt.get();
        User.UserRole requestedRole;

        try {
            requestedRole = User.UserRole.valueOf(newRoleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid role requested.");
        }

        try {
            roleChangeRequestService.requestRoleChange(user.getId(), requestedRole); // Use the autowired service
            return ResponseEntity.ok("Role change request submitted.");
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/profile-picture")
    public ResponseEntity<?> getProfilePicture(@AuthenticationPrincipal Object principal) throws IOException {
        String email = extractEmail(principal);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized.");
        }

        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        User user = userOpt.get();
        Optional<UserProfilePicture> pictureOpt = userService.getProfilePictureByUserId(user.getId());
        if (pictureOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile picture not found.");
        }

        Path filePath = Paths.get("uploads/profile-pictures/" + pictureOpt.get().getFileName());
        if (!Files.exists(filePath)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found on disk.");
        }

        byte[] imageBytes = Files.readAllBytes(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }



}
