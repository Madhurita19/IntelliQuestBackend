package com.intelliquest.controller;

import com.intelliquest.dto.CourseResponseDTO;
import com.intelliquest.model.User;
import com.intelliquest.model.User.UserRole;
import com.intelliquest.service.StudentDashboardService;
import com.intelliquest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class StudentDashboardController {

    private final StudentDashboardService studentDashboardService;
    private final UserService userService;

    @GetMapping("/fetch-all-courses")
    public ResponseEntity<?> getAllCoursesForStudent(@AuthenticationPrincipal Object principal) {
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

            User user = userOpt.get();

            if (!user.getRole().equals(UserRole.USER)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden: Only students can access this endpoint.");
            }

            List<CourseResponseDTO> courses = studentDashboardService.getAllCoursesForStudents();
            if (courses.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("{\"error\": \"No courses available\"}");
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(courses);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Internal server error\"}");
        }
    }
    
    
    @GetMapping("/enrolled-courses")
    public ResponseEntity<?> getEnrolledCoursesForStudent(@AuthenticationPrincipal Object principal) {
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

            User user = userOpt.get();

            if (!user.getRole().equals(UserRole.USER)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden: Only students can access this endpoint.");
            }

            List<CourseResponseDTO> enrolledCourses = studentDashboardService.getEnrolledCoursesForStudent(user.getId());
            if (enrolledCourses.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList()); // Always return a JSON array
            }


            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(enrolledCourses);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Internal server error\"}");
        }
    }
    @GetMapping("/courses/{courseId}")
    public ResponseEntity<?> getCourseDetails(@PathVariable Long courseId, @AuthenticationPrincipal Object principal) {
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

            User user = userOpt.get();

            if (!user.getRole().equals(UserRole.USER)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden: Only students can access this endpoint.");
            }

            Optional<CourseResponseDTO> courseOpt = studentDashboardService.getCourseDetailsById(courseId);
            if (courseOpt.isPresent()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(courseOpt.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\": \"Course not found\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Internal server error\"}");
        }
    }

    @PutMapping("/courses/{courseId}/lessons/{lessonId}/complete")
    public ResponseEntity<Void> completeLesson(
            @PathVariable Long courseId,
            @PathVariable String lessonId,
            @AuthenticationPrincipal UserDetails userDetails) {
    	Optional<User> userOpt = userService.getUserByEmail(userDetails.getUsername());
    	if (userOpt.isEmpty()) {
    	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    	}
    	Long userId = userOpt.get().getId();
        studentDashboardService.markLessonAsComplete(userId, courseId, lessonId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/courses/{courseId}/progress")
    public ResponseEntity<List<String>> getCompletedLessons(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails userDetails) {
    	Optional<User> userOpt = userService.getUserByEmail(userDetails.getUsername());
    	if (userOpt.isEmpty()) {
    	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    	}
    	Long userId = userOpt.get().getId();
        return ResponseEntity.ok(studentDashboardService.getCompletedLessonIds(userId, courseId));
    }



}
