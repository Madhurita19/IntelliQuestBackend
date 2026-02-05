package com.intelliquest.repository;

import com.intelliquest.model.Course;
import com.intelliquest.model.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, Long> {
	
	List<Course> findByInstructor(User instructor);
	
	@Query("SELECT c FROM Course c JOIN FETCH c.instructor WHERE c.courseId = :courseId")
	Optional<Course> findByIdWithInstructor(@Param("courseId") Long courseId);
	List<Course> findByPublishedTrue();
	
	long countByInstructor(User instructor);


}
