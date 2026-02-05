package com.intelliquest.controller;

import com.intelliquest.dto.CourseResponseDTO;
import com.intelliquest.dto.UserDTO;
import com.intelliquest.dto.UserWithCoursesDTO;
import com.intelliquest.model.User;
import com.intelliquest.service.AdminService;
import com.intelliquest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/auth/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {
        return adminService.getAllUsersWithCourses();  // Fetch users with courses
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<UserWithCoursesDTO> user = adminService.getUserWithCoursesById(id);

        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(404).body("User not found!");
        }
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<String> updateUserStatus(@PathVariable Long id, @RequestParam boolean enabled) {
        adminService.updateUserStatus(id, enabled);
        return ResponseEntity.ok("User status updated successfully");
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<String> updateUserRole(@PathVariable Long id, @RequestParam String role) {
        List<String> allowedRoles = List.of("INSTRUCTOR", "USER");

        if (!allowedRoles.contains(role.toUpperCase())) {
            return ResponseEntity.badRequest().body("Invalid role! Allowed roles: INSTRUCTOR, USER");
        }

        boolean updated = adminService.updateUserRole(id, role.toUpperCase());
        if (updated) {
            return ResponseEntity.ok("User role updated successfully");
        } else {
            return ResponseEntity.status(404).body("User not found!");
        }
    }


    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id, @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails adminUser) {
    	
    	if (adminUser == null) {
            return ResponseEntity.status(401).body("Unauthorized: admin user not authenticated");
        }
    	
        Optional<User> admin = userRepository.findByEmail(adminUser.getUsername());

        if (admin.isPresent() && id.equals(admin.get().getId())) {
            return ResponseEntity.badRequest().body("Admins cannot delete themselves!");
        }

        adminService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAdmin(@RequestBody User request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email already in use!");
        }

        User newAdmin = new User();
        newAdmin.setUsername(request.getUsername());
        newAdmin.setEmail(request.getEmail());
        newAdmin.setPassword(passwordEncoder.encode(request.getPassword()));
        newAdmin.setRole(User.UserRole.ADMIN);

        userRepository.save(newAdmin);
        return ResponseEntity.ok("Admin user created successfully!");
    }

    @PutMapping("/users/{id}/assign-instructor")
    public ResponseEntity<String> assignInstructor(@PathVariable Long id) {
        boolean updated = adminService.assignInstructorRole(id);
        if (updated) {
            return ResponseEntity.ok("User has been assigned as an Instructor.");
        }
        return ResponseEntity.status(404).body("User not found.");
    }

    @PutMapping("/users/{id}/revoke-instructor")
    public ResponseEntity<String> revokeInstructor(@PathVariable Long id) {
        boolean updated = adminService.revokeInstructorRole(id);
        if (updated) {
            return ResponseEntity.ok("Instructor role has been revoked.");
        }
        return ResponseEntity.status(404).body("User not found.");
    }
    

    @GetMapping("/dashboard-counts")
    public ResponseEntity<?> getDashboardCounts() {
        long totalUsers = userRepository.count();
        long totalInstructors = userRepository.countByRole(User.UserRole.INSTRUCTOR);
        long totalCourses = adminService.countCourses();
        long enabledUsers = userRepository.countByEnabledTrue();

        return ResponseEntity.ok(
                Map.of(
                        "users", totalUsers,
                        "instructors", totalInstructors,
                        "courses", totalCourses,
                        "enabledUsers", enabledUsers
                )
        );
    }
    
   

    
    @GetMapping("/courses")
    public List<CourseResponseDTO> getAllCourses() {
        return adminService.getAllCourses();
    }
    
 // Endpoint to delete a course
    @DeleteMapping("/courses/{courseId}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long courseId) {
        boolean deleted = adminService.deleteCourse(courseId);
        if (deleted) {
            return ResponseEntity.ok("Course deleted successfully");
        } else {
            return ResponseEntity.status(404).body("Course not found");
        }
    }

}
