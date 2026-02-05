package com.intelliquest.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentGatewayId;
    private String paymentStatus;
    private String paymentMethod;
    private Double amount;

    @CreationTimestamp
    private LocalDateTime paidAt;

    @OneToOne(mappedBy = "payment")
    private Enrollment enrollment;
}
