package com.intelliquest.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizResultDTO {
    private Long quizId;
    private String sub;
    private int totalMarks;
    private int marksAchieved;
    private int correctAnswers;
    private int wrongAnswers;
    private int totalQuestions;
    private int attemptedQuestions;
    
 // additional fields for UI
    private String quizTitle;
    private String category;
    private long submissionTime;
}
