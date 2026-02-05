package com.intelliquest.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "course_options")
public class CourseOptions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000, nullable = false)
    private String benefits;

    @Column(length = 2000, nullable = false)
    private String prerequisites;

    @OneToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
}
