package com.intelliquest.controller;

import com.intelliquest.dto.CourseRequestDTO;
import com.intelliquest.dto.CourseResponseDTO;
import com.intelliquest.dto.PublishCourseRequestDTO;
import com.intelliquest.repository.CourseRepository;
import com.intelliquest.repository.StudyMaterialRepository;
import com.intelliquest.model.Course;
import com.intelliquest.model.StudyMaterial;
import com.intelliquest.model.User;
import com.intelliquest.model.User.UserRole;
import com.intelliquest.service.CourseService;
import com.intelliquest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;
    private final CourseRepository courseRepository;

    @PostMapping("/create-courses")
    public ResponseEntity<?> createCourse(@ModelAttribute CourseRequestDTO dto,
                                          @AuthenticationPrincipal Object principal) {
        try {

            String email = null;

            if (principal instanceof OAuth2User) {
                OAuth2User oAuth2User = (OAuth2User) principal;
                email = oAuth2User.getAttribute("email");
            } else if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                email = userDetails.getUsername();
            } else if (principal instanceof String) {
                email = (String) principal;
            }

            if (email == null) {
                return ResponseEntity.status(401).body("Email is null after principal inspection.");
            }

            Optional<User> userOpt = userService.findByEmail(email);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body("No user found with email = " + email);
            }

            User user = userOpt.get();
            System.out.println("DEBUG: Found user = " + user.getEmail() + ", Role = " + user.getRole());

            if (!user.getRole().equals(UserRole.INSTRUCTOR)) {
                return ResponseEntity.status(403).body("Forbidden: Only instructors can create courses.");
            }

            courseService.createCourse(dto, user);
            System.out.println("DEBUG: Course created successfully by user = " + email);
            return ResponseEntity.ok("Course created successfully.");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to process files: " + e.getMessage());
        } catch (MultipartException e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body("Invalid multipart data: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Something went wrong: " + e.getMessage());
        }
    }
    
    @GetMapping("/instructor-courses")
    public ResponseEntity<?> getInstructorCourses(@AuthenticationPrincipal Object principal) {
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

            if (!instructor.getRole().equals(UserRole.INSTRUCTOR)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden: Only instructors can access their courses.");
            }

            List<CourseResponseDTO> courses = courseService.getCoursesForInstructor(instructor);
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

    
    @PutMapping("/courses/{courseId}/edit")
    public ResponseEntity<String> editCourse(
            @PathVariable Long courseId,
            @ModelAttribute CourseRequestDTO courseRequestDTO
    ) {
        try {
            courseService.updateCourse(courseId, courseRequestDTO);
            return ResponseEntity.ok("Course updated successfully!");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process files: " + e.getMessage());
        } catch (MultipartException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid multipart data: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update course: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/courses/{courseId}/delete")
    public ResponseEntity<String> deleteCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal Object principal
    ) {
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

            if (!user.getRole().equals(UserRole.INSTRUCTOR)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden: Only instructors can delete courses.");
            }

            courseService.deleteCourse(courseId, user);

            return ResponseEntity.ok("Course deleted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete course: " + e.getMessage());
        }
    }
    
    @PatchMapping("/courses/{courseId}/publish")
    public ResponseEntity<?> updatePublishStatus(@PathVariable Long courseId,
                                                 @RequestBody PublishCourseRequestDTO request,
                                                 @AuthenticationPrincipal Object principal) {
        try {
            String email = null;

            if (principal instanceof OAuth2User oAuth2User) {
                email = oAuth2User.getAttribute("email");
            } else if (principal instanceof UserDetails userDetails) {
                email = userDetails.getUsername();
            } else if (principal instanceof String emailStr) {
                email = emailStr;
            }

            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Email not found.");
            }

            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: User not found.");
            }

            User instructor = userOpt.get();

            if (!instructor.getRole().equals(UserRole.INSTRUCTOR)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden: Only instructors can publish courses.");
            }

            courseService.setCoursePublishedStatus(courseId, request.isPublished(), instructor);

            return ResponseEntity.ok("Course publish status updated successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
    
    @GetMapping("/courses/{id}/thumbnail")
    public ResponseEntity<byte[]> getCourseThumbnail(@PathVariable Long id) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        byte[] thumbnail = course.getThumbnail();

        String mimeType = "image/jpeg";
        try {
            mimeType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(thumbnail));

            if (mimeType == null) {
                mimeType = "image/jpeg";
            }
        } catch (IOException e) {
            e.printStackTrace();
            mimeType = "image/jpeg";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(mimeType));

        return new ResponseEntity<>(thumbnail, headers, HttpStatus.OK);
    }
    
    @Autowired
    private StudyMaterialRepository studyMaterialRepository;
    
    @GetMapping("/courses/{courseId}/materials/{fileName}")
    public ResponseEntity<byte[]> getStudyMaterial(
            @PathVariable Long courseId,
            @PathVariable String fileName,
            @RequestParam(defaultValue = "false") boolean download) {

        StudyMaterial material = studyMaterialRepository.findByCourse_CourseIdAndFileName(courseId, fileName);
        if (material == null) {
            return ResponseEntity.notFound().build();
        }

        String mimeType = URLConnection.guessContentTypeFromName(fileName);
        MediaType mediaType = (mimeType != null) ? MediaType.parseMediaType(mimeType) : MediaType.APPLICATION_OCTET_STREAM;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setContentDisposition(
        download ? ContentDisposition.attachment().filename(fileName).build()
        		: ContentDisposition.inline().filename(fileName).build());

        return new ResponseEntity<>(material.getFileData(), headers, HttpStatus.OK);
    }

}
