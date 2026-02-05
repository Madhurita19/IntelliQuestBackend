package com.intelliquest.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDTO {
    private String razorpayPaymentId;
    private String razorpayOrderId;
    private String userEmail;
    private Long courseId;
}
