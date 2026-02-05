package com.intelliquest.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "enrollments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;
}
