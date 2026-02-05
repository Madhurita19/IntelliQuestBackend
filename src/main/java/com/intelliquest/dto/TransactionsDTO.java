package com.intelliquest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransactionsDTO {
    private String paymentGatewayId;
    private String paymentStatus;
    private String paymentMethod;
    private Double amount;
    private LocalDateTime paidAt;
}
