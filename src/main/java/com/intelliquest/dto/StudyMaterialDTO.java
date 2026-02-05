package com.intelliquest.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudyMaterialDTO {
	private Long id;
    private String name;
    private MultipartFile file;
}
