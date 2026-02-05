package com.intelliquest.controller;

import com.intelliquest.dto.CourseSalesDTO;
import com.intelliquest.dto.TransactionsDTO;
import com.intelliquest.model.User;
import com.intelliquest.service.UserService;
import com.intelliquest.service.InstructorDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class InstructorDashboardController {

    private final InstructorDashboardService instructorDashboardService;
    private final UserService userService;

    @GetMapping("/instructor/total-courses")
    public ResponseEntity<?> getTotalCoursesByInstructor(@AuthenticationPrincipal Object principal) {
        try {
            String email = null;

            if (principal instanceof OAuth2User oAuth2User) {
                email = oAuth2User.getAttribute("email");
            } else if (principal instanceof UserDetails userDetails) {
                email = userDetails.getUsername();
            } else if (principal instanceof String emailString) {
                email = emailString;
            }

            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Email not found.");
            }

            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: User not found.");
            }

            User instructor = userOpt.get();

            if (!instructor.getRole().equals(User.UserRole.INSTRUCTOR)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden: Only instructors can access this endpoint.");
            }

            long totalCourses = instructorDashboardService.getTotalCoursesByInstructor(instructor);
            return ResponseEntity.ok().body("{\"totalCourses\": " + totalCourses + "}");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Internal server error\"}");
        }
    }
    
    @GetMapping("/instructor/total-revenue")
    public ResponseEntity<?> getTotalRevenueByInstructor(@AuthenticationPrincipal Object principal) {
        try {
            String email = null;

            if (principal instanceof OAuth2User oAuth2User) {
                email = oAuth2User.getAttribute("email");
            } else if (principal instanceof UserDetails userDetails) {
                email = userDetails.getUsername();
            } else if (principal instanceof String emailString) {
                email = emailString;
            }

            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Email not found.");
            }

            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: User not found.");
            }

            User instructor = userOpt.get();

            if (!instructor.getRole().equals(User.UserRole.INSTRUCTOR)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden: Only instructors can access this endpoint.");
            }

            double totalRevenue = instructorDashboardService.getTotalRevenueByInstructor(instructor);
            return ResponseEntity.ok().body("{\"totalRevenue\": " + totalRevenue + "}");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Internal server error\"}");
        }
    }
    
    @GetMapping("/instructor/transactions")
    public ResponseEntity<?> getTransactionsForInstructor(@AuthenticationPrincipal Object principal) {
        try {
            String email = null;

            if (principal instanceof OAuth2User oAuth2User) {
                email = oAuth2User.getAttribute("email");
            } else if (principal instanceof UserDetails userDetails) {
                email = userDetails.getUsername();
            } else if (principal instanceof String emailString) {
                email = emailString;
            }

            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Email not found.");
            }

            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: User not found.");
            }

            User instructor = userOpt.get();

            if (!instructor.getRole().equals(User.UserRole.INSTRUCTOR)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden: Only instructors can access this endpoint.");
            }

            List<TransactionsDTO> transactions = instructorDashboardService.getTransactionsForInstructor(instructor);
            return ResponseEntity.ok().body(transactions);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Internal server error\"}");
        }
    }
    
    @GetMapping("/instructor/course-sales")
    public ResponseEntity<?> getCourseSalesByInstructor(@AuthenticationPrincipal Object principal) {
        try {
            String email = null;

            if (principal instanceof OAuth2User oAuth2User) {
                email = oAuth2User.getAttribute("email");
            } else if (principal instanceof UserDetails userDetails) {
                email = userDetails.getUsername();
            } else if (principal instanceof String emailString) {
                email = emailString;
            }

            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Email not found.");
            }

            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: User not found.");
            }

            User instructor = userOpt.get();

            if (!instructor.getRole().equals(User.UserRole.INSTRUCTOR)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden: Only instructors can access this endpoint.");
            }

            List<CourseSalesDTO> sales = instructorDashboardService.getCourseSalesByInstructor(instructor);
            return ResponseEntity.ok(sales);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Internal server error\"}");
        }
    }

}
