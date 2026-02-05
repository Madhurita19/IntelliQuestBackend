package com.intelliquest.dto;

import lombok.*;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizSubmissionDTO {
	private Long id;
    private String title;
    private int playingTime;
    private Long instructorId;
    private String instructorName;
    private String instructorEmail;
    private String level;
    private String category;
    
    @JsonIgnore
    private MultipartFile thumbnail;
    
    private String thumbnailUrl;
    private List<Question> questions;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Question {
        private String id;
        private String question;
        private String correctOptionId;
        private int marksForCorrect;
        private int marksForIncorrect;
        private List<Option> options;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Option {
        private String id; 
        private String text;
    }
}
