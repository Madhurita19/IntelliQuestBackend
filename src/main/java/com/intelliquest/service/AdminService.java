package com.intelliquest.service;

import com.intelliquest.dto.CourseResponseDTO;
import com.intelliquest.dto.UserDTO;
import com.intelliquest.dto.UserWithCoursesDTO;
import com.intelliquest.model.Course;
import com.intelliquest.model.CourseOptions;
import com.intelliquest.model.User;
import com.intelliquest.repository.CourseOptionRepository;
import com.intelliquest.repository.CourseRepository;
import com.intelliquest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseOptionRepository courseOptionRepository;
    
    public List<UserDTO> getAllUsersWithCourses() {
        return userRepository.findAll().stream().map(user -> {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setUsername(user.getUsername());
            userDTO.setEmail(user.getEmail());
            userDTO.setRole(user.getRole().name());
            userDTO.setEnabled(user.isEnabled());

            // Add the courses for each user
            List<CourseResponseDTO> courseDTOs = user.getCourses().stream()
                    .map(this::mapToCourseResponseDTO)
                    .collect(Collectors.toList());
            userDTO.setCourses(courseDTOs);

            return userDTO;
        }).collect(Collectors.toList());
    }
    public Optional<UserWithCoursesDTO> getUserWithCoursesById(Long id) {
        return userRepository.findById(id).map(user -> {
            UserWithCoursesDTO dto = new UserWithCoursesDTO();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setPassword(user.getPassword());
            dto.setRole(user.getRole().name());
            dto.setEnabled(user.isEnabled());
            dto.setCreatedAt(user.getCreatedAt());
            dto.setUpdatedAt(user.getUpdatedAt());

            List<CourseResponseDTO> courseDTOs = user.getCourses().stream()
                    .map(this::mapToCourseResponseDTO)
                    .collect(Collectors.toList());

            dto.setCourses(courseDTOs);
            return dto;
        });
    }

    private CourseResponseDTO mapToCourseResponseDTO(Course course) {
        CourseResponseDTO dto = new CourseResponseDTO();
        dto.setCourseId(course.getCourseId());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setPrice(course.getPrice());
        dto.setCategory(course.getCategory());
        dto.setLevel(course.getLevel());
        dto.setLanguage(course.getLanguage());
        dto.setPublished(course.isPublished());

        if (course.getInstructor() != null) {
            dto.setInstructorEmail(course.getInstructor().getEmail());
        }

        dto.setThumbnailUrl("http://localhost:9092/auth/courses/" + course.getCourseId() + "/thumbnail");

        dto.setTopics(course.getTopics().stream()
                .map(topic -> {
                    CourseResponseDTO.TopicDTO topicDTO = new CourseResponseDTO.TopicDTO();
                    topicDTO.setName(topic.getName());
                    topicDTO.setSubtopics(topic.getSubtopics().stream()
                            .map(subtopic -> {
                                CourseResponseDTO.SubtopicDTO subtopicDTO = new CourseResponseDTO.SubtopicDTO();
                                subtopicDTO.setName(subtopic.getName());
                                subtopicDTO.setVideos(subtopic.getVideos().stream()
                                        .map(video -> {
                                            CourseResponseDTO.VideoDTO videoDTO = new CourseResponseDTO.VideoDTO();
                                            videoDTO.setTitle(video.getTitle());
                                            videoDTO.setDescription(video.getDescription());
                                            videoDTO.setUrl(video.getUrl());
                                            return videoDTO;
                                        }).collect(Collectors.toList()));
                                return subtopicDTO;
                            }).collect(Collectors.toList()));
                    return topicDTO;
                }).collect(Collectors.toList()));

        var options = courseOptionRepository.findByCourse(course);
        if (options != null) {
            var optionDTO = new CourseResponseDTO.CourseOptionDTO();
            optionDTO.setBenefits(options.getBenefits());
            optionDTO.setPrerequisites(options.getPrerequisites());
            dto.setCourseOptions(optionDTO);
        }

        dto.setStudyMaterials(course.getStudyMaterials().stream()
                .map(material -> {
                    CourseResponseDTO.StudyMaterialDTO materialDTO = new CourseResponseDTO.StudyMaterialDTO();
                    materialDTO.setName(material.getName());
                    materialDTO.setFileName(material.getFileName());
                    materialDTO.setFileUrl("http://localhost:9092/auth/courses/" + course.getCourseId() + "/materials/" + material.getFileName());
                    materialDTO.setDownloadUrl("http://localhost:9092/auth/courses/" + course.getCourseId() + "/materials/" + material.getFileName() + "?download=true");
                    return materialDTO;
                }).collect(Collectors.toList()));

        return dto;
    }

    public void updateUserStatus(Long id, boolean enabled) {
        userRepository.findById(id).ifPresent(user -> {
            user.setEnabled(enabled);
            userRepository.save(user);
        });
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean updateUserRole(Long userId, String role) {
        Optional<User> userOpt = userRepository.findById(userId);
        try {
            User.UserRole userRole = User.UserRole.valueOf(role.toUpperCase());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setRole(userRole);
                userRepository.save(user);
                return true;
            }
        } catch (IllegalArgumentException e) {

            return false;
        }
        return false;
    }

    public boolean assignInstructorRole(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRole(User.UserRole.INSTRUCTOR);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public boolean revokeInstructorRole(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRole(User.UserRole.USER);
            userRepository.save(user);
            return true;
        }
        return false;
    }
    
    public long countCourses() {
        return courseRepository.count();
    }
    
    public List<CourseResponseDTO> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        return courses.stream()
                .map(this::convertToCourseResponseDTO)
                .collect(Collectors.toList());
    }

    private CourseResponseDTO convertToCourseResponseDTO(Course course) {
        CourseResponseDTO dto = new CourseResponseDTO();
        dto.setCourseId(course.getCourseId());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setPrice(course.getPrice());
        dto.setCategory(course.getCategory());
        dto.setLevel(course.getLevel());
        dto.setLanguage(course.getLanguage());
        dto.setPublished(course.isPublished());

        if (course.getInstructor() != null) {
            dto.setInstructorEmail(course.getInstructor().getEmail());
        }

        dto.setThumbnailUrl("http://localhost:9092/auth/courses/" + course.getCourseId() + "/thumbnail");

        dto.setTopics(course.getTopics().stream()
                .map(topic -> {
                    CourseResponseDTO.TopicDTO topicDTO = new CourseResponseDTO.TopicDTO();
                    topicDTO.setName(topic.getName());
                    topicDTO.setSubtopics(topic.getSubtopics().stream()
                            .map(subtopic -> {
                                CourseResponseDTO.SubtopicDTO subtopicDTO = new CourseResponseDTO.SubtopicDTO();
                                subtopicDTO.setName(subtopic.getName());
                                subtopicDTO.setVideos(subtopic.getVideos().stream()
                                        .map(video -> {
                                            CourseResponseDTO.VideoDTO videoDTO = new CourseResponseDTO.VideoDTO();
                                            videoDTO.setTitle(video.getTitle());
                                            videoDTO.setDescription(video.getDescription());
                                            videoDTO.setUrl(video.getUrl());
                                            return videoDTO;
                                        }).collect(Collectors.toList()));
                                return subtopicDTO;
                            }).collect(Collectors.toList()));
                    return topicDTO;
                }).collect(Collectors.toList()));

        CourseOptions options = courseOptionRepository.findByCourse(course);
        if (options != null) {
            CourseResponseDTO.CourseOptionDTO optionDTO = new CourseResponseDTO.CourseOptionDTO();
            optionDTO.setBenefits(options.getBenefits());
            optionDTO.setPrerequisites(options.getPrerequisites());
            dto.setCourseOptions(optionDTO);
        }

        dto.setStudyMaterials(course.getStudyMaterials().stream()
                .map(material -> {
                    CourseResponseDTO.StudyMaterialDTO materialDTO = new CourseResponseDTO.StudyMaterialDTO();
                    materialDTO.setName(material.getName());
                    materialDTO.setFileName(material.getFileName());
                    materialDTO.setFileUrl("http://localhost:9092/auth/courses/" + course.getCourseId() + "/materials/" + material.getFileName());
                    materialDTO.setDownloadUrl("http://localhost:9092/auth/courses/" + course.getCourseId() + "/materials/" + material.getFileName() + "?download=true");
                    return materialDTO;
                }).collect(Collectors.toList()));

        return dto;
    }
    public boolean deleteCourse(Long courseId) {
        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isPresent()) {
            courseRepository.deleteById(courseId);
            return true; // Successfully deleted
        }
        return false; // Course not found
    }


}
