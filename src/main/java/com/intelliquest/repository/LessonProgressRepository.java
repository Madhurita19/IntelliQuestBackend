package com.intelliquest.repository;

import com.intelliquest.model.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {

    List<LessonProgress> findByUserIdAndCourseCourseId(Long userId, Long courseId);

    Optional<LessonProgress> findByUserIdAndCourseCourseIdAndLessonId(Long userId, Long courseId, String lessonId);
}
