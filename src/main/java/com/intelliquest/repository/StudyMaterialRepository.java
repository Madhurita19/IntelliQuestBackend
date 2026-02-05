package com.intelliquest.repository;


import com.intelliquest.model.StudyMaterial;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyMaterialRepository extends JpaRepository<StudyMaterial, Long> {
	
	StudyMaterial findByCourse_CourseIdAndFileName(Long courseId, String fileName);
	Optional<StudyMaterial> findByIdAndCourse_CourseId(Long id, Long courseId);

}
