package com.intelliquest.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserWithCoursesDTO {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String role;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CourseResponseDTO> courses;
}
