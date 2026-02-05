package com.intelliquest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.intelliquest.model.Enrollment;
import com.intelliquest.model.Payment;
import com.intelliquest.model.User;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByUserId(Long userId);
    
    @Query("SELECT SUM(p.amount) FROM Enrollment e JOIN e.payment p JOIN e.course c WHERE c.instructor = :instructor")
    Double sumRevenueByInstructor(@Param("instructor") User instructor);
    
    @Query("SELECT p FROM Enrollment e JOIN e.payment p WHERE e.course.instructor = :instructor")
    List<Payment> findPaymentsByInstructor(@Param("instructor") User instructor);
}

