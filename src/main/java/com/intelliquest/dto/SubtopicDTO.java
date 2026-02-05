package com.intelliquest.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubtopicDTO {
    private String name;
    private List<VideoDTO> videos;
}
