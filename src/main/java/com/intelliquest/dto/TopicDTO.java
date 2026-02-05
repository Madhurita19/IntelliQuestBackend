package com.intelliquest.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopicDTO {
    private String name;
    private List<SubtopicDTO> subtopics;
}
