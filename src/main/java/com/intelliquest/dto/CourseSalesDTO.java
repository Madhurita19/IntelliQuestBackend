package com.intelliquest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CourseSalesDTO {
    private String courseTitle;
    private long totalSales;
}
