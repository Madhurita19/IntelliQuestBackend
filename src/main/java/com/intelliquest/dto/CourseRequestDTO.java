package com.intelliquest.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CourseRequestDTO {
    // Step 1: Course Info
    private String title;
    private String description;
    private BigDecimal price;
    private String category;
    private String level;
    private String language;
    private MultipartFile thumbnail;

    // Step 2: Topics, Subtopics, Videos
    private List<TopicDTO> topics;

    // Step 3: Options
    private String benefits;
    private String prerequisites;

    // Step 4: Study Materials
    private List<StudyMaterialDTO> studyMaterials;

    // ---------- Nested DTO Classes ----------
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
    public static class StudyMaterialDTO {
    	private Long id;
        private String name;
        private MultipartFile file;
    }
}
