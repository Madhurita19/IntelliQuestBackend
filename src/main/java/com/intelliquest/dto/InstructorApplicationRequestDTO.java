package com.intelliquest.dto;

import lombok.Data;

@Data
public class InstructorApplicationRequestDTO {
    private String fullName;
    private String email;
    private String experience;
    private String expertise;
    private String motivation;
}
