package com.intelliquest.model;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long courseId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String level;

    @Column(nullable = false)
    private String language;
    
    @Column(nullable = false)
    private boolean published = false;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGBLOB")
    private byte[] thumbnail;
    
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Topic> topics;
    
    @OneToOne(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private CourseOptions courseOptions;
    
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyMaterial> studyMaterials;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User instructor;
}
