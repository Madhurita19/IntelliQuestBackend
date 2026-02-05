package com.intelliquest.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizStatsDTO {
    private long quizzesCompleted;
    private double averageScore;
    private int highestScore;
    private String bestCategory;
}
