package com.intelliquest.repository;

import com.intelliquest.model.Course;
import com.intelliquest.model.CourseOptions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseOptionRepository extends JpaRepository<CourseOptions, Long> {
	CourseOptions findByCourse(Course course);

}
