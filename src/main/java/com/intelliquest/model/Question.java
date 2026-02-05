package com.intelliquest.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @Column(name = "question_id")
    private String id; 

    private String question;
    
    private String correctOptionId; 
    
    @Column(nullable = false)
    private int marksForCorrect;

    @Column(nullable = false)
    private int marksForIncorrect;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options;
}
