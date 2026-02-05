package com.intelliquest.repository;

import com.intelliquest.model.Course;
import com.intelliquest.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {
	Topic findByNameAndCourse(String name, Course course);  
}
