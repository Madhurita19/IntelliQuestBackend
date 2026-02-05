package com.intelliquest.repository;

import com.intelliquest.model.Subtopic;
import com.intelliquest.model.Topic;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubtopicRepository extends JpaRepository<Subtopic, Long> {
	Subtopic findByNameAndTopic(String name, Topic topic);
}
