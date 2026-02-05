package com.intelliquest.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRequestDTO {
    private String name;
    private String email;
    private String feedback;
    private int rating;
}