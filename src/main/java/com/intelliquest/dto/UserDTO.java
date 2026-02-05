package com.intelliquest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String role;
    private boolean enabled;
    private List<CourseResponseDTO> courses; // Add this field for courses
}
