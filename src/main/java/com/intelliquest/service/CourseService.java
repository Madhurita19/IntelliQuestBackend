package com.intelliquest.service;

import com.intelliquest.dto.CourseRequestDTO;
import com.intelliquest.dto.CourseResponseDTO;
import com.intelliquest.model.User;

import java.io.IOException;
import java.util.List;

public interface CourseService {
	
    void createCourse(CourseRequestDTO courseRequestDto, User user) throws IOException; 
    
    List<CourseResponseDTO> getCoursesForInstructor(User instructor);
    
    void updateCourse(Long courseId, CourseRequestDTO dto) throws IOException;
    
    void deleteCourse(Long courseId, User instructor);
    
    void setCoursePublishedStatus(Long courseId, boolean published, User instructor);

}
