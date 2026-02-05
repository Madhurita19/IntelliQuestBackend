package com.intelliquest.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupportRequestDTO {
    private String name;
    private String email;
    private String subject;
    private String message;
}
