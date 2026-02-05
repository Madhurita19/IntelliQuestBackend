package com.intelliquest.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "study_materials")
public class StudyMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGBLOB")
    private byte[] fileData;

    @Column(nullable = false)
    private String fileName;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
}
