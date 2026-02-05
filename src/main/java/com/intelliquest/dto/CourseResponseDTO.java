package com.intelliquest.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CourseResponseDTO {
    private Long courseId;
    private String title;
    private String description;
    private BigDecimal price;
    private String category;
    private String level;
    private String language;
    private boolean published;

    private String instructorEmail;
    private String thumbnailUrl;

    private List<TopicDTO> topics;
    private CourseOptionDTO courseOptions;
    private List<StudyMaterialDTO> studyMaterials;

    @Data
    public static class TopicDTO {
        private String name;
        private List<SubtopicDTO> subtopics;
    }

    @Data
    public static class SubtopicDTO {
        private String name;
        private List<VideoDTO> videos;
    }

    @Data
    public static class VideoDTO {
        private String title;
        private String description;
        private String url;
    }

    @Data
    public static class CourseOptionDTO {
        private String benefits;
        private String prerequisites;
    }

    @Data
    public static class StudyMaterialDTO {
        private String name;
        private String fileName;
        private String fileUrl;
        private String downloadUrl;
    }
}
