package com.intelliquest.repository;

import com.intelliquest.model.Quiz;
import com.intelliquest.model.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
	List<Quiz> findByInstructor(User instructor);
	@Query("SELECT COUNT(q) FROM Quiz q WHERE q.instructor.id = :instructorId")
	long countByInstructorId(@Param("instructorId") Long instructorId);
}
