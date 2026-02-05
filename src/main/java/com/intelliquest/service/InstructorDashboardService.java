package com.intelliquest.service;

import com.intelliquest.dto.CourseSalesDTO;
import com.intelliquest.dto.TransactionsDTO;
import com.intelliquest.model.Payment;
import com.intelliquest.model.User;
import com.intelliquest.repository.CourseRepository;
import com.intelliquest.repository.EnrollmentRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InstructorDashboardService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public long getTotalCoursesByInstructor(User instructor) {
        return courseRepository.countByInstructor(instructor);
    }
    
    public double getTotalRevenueByInstructor(User instructor) {
        Double revenue = enrollmentRepository.sumRevenueByInstructor(instructor);
        return revenue != null ? revenue : 0.0;
    }
    
    public List<TransactionsDTO> getTransactionsForInstructor(User instructor) {

        List<Payment> payments = enrollmentRepository.findPaymentsByInstructor(instructor);

        return payments.stream()
                       .map(payment -> new TransactionsDTO(
                               payment.getPaymentGatewayId(),
                               payment.getPaymentStatus(),
                               payment.getPaymentMethod(),
                               payment.getAmount(),
                               payment.getPaidAt()
                       ))
                       .collect(Collectors.toList());
    }
    
    public List<CourseSalesDTO> getCourseSalesByInstructor(User instructor) {
        return enrollmentRepository.findPaymentsByInstructor(instructor).stream()
            .collect(Collectors.groupingBy(payment -> payment.getEnrollment().getCourse().getTitle(), Collectors.counting()))
            .entrySet().stream()
            .map(entry -> new CourseSalesDTO(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }
}
