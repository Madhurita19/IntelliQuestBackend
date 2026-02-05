package com.intelliquest.repository;

import com.intelliquest.model.Subtopic;
import com.intelliquest.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {
	Video findByUrlAndSubtopic(String url, Subtopic subtopic);
}
