package com.intelliquest.controller;

import com.intelliquest.dto.PaymentRequestDTO;
import com.intelliquest.model.Course;
import com.intelliquest.repository.CourseRepository;
import com.intelliquest.service.PaymentConfirmationMailService;
import com.intelliquest.service.RazorpayService;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/payment")
public class RazorpayController {

    private final RazorpayService razorpayService;
    private final PaymentConfirmationMailService paymentConfirmationMailService;
    private final CourseRepository courseRepository;

    public RazorpayController(RazorpayService razorpayService,
            PaymentConfirmationMailService paymentConfirmationMailService,
            CourseRepository courseRepository) {
    		this.razorpayService = razorpayService;
    		this.paymentConfirmationMailService = paymentConfirmationMailService;
    		this.courseRepository = courseRepository;
    }	

    @PostMapping("/create-order")
    public String createOrder(@RequestParam Double amount) {
        try {
            return razorpayService.createOrder(amount);
        } catch (Exception e) {
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }
    
    @PostMapping("/complete-payment")
    public ResponseEntity<?> completePayment(@RequestBody PaymentRequestDTO request) {
        try {
            razorpayService.recordPaymentAndEnrollUser(request);
            
            String courseTitle = courseRepository.findById(request.getCourseId())
                    .map(Course::getTitle)
                    .orElse("your course");

            BigDecimal courseAmount = courseRepository.findById(request.getCourseId())
                    .map(Course::getPrice) 
                    .orElse(BigDecimal.ZERO); 
            
            paymentConfirmationMailService.sendConfirmation(
            	    request.getUserEmail(),
            	    courseTitle,
            	    request.getRazorpayOrderId(),
            	    request.getRazorpayPaymentId(),
            	    courseAmount
            	);

            
            return ResponseEntity.ok("Course access granted.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Payment processing failed: " + e.getMessage());
        }
    }
}
