package com.intelliquest.service;

import com.intelliquest.dto.CourseRequestDTO;
import com.intelliquest.dto.CourseResponseDTO;
import com.intelliquest.model.User;
import com.intelliquest.repository.UserRepository;
import com.intelliquest.model.Course;
import com.intelliquest.model.Topic;
import com.intelliquest.model.Subtopic;
import com.intelliquest.model.Video;
import com.intelliquest.model.CourseOptions;
import com.intelliquest.model.StudyMaterial;
import com.intelliquest.repository.CourseRepository;
import com.intelliquest.repository.TopicRepository;
import com.intelliquest.repository.SubtopicRepository;
import com.intelliquest.repository.VideoRepository;
import com.intelliquest.repository.CourseOptionRepository;
import com.intelliquest.repository.StudyMaterialRepository;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final TopicRepository topicRepository;
    private final SubtopicRepository subtopicRepository;
    private final VideoRepository videoRepository;
    private final CourseOptionRepository courseOptionRepository;
    private final StudyMaterialRepository studyMaterialRepository;
    private final UserRepository userRepository; 

    @Override
    @Transactional
    public void createCourse(CourseRequestDTO dto, User user) throws IOException {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isEmpty() || existingUser.get().getRole() != User.UserRole.INSTRUCTOR) {
            throw new RuntimeException("User does not have the instructor role to create a course.");
        }

        Course course = new Course();
        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        course.setPrice(dto.getPrice());
        course.setCategory(dto.getCategory());
        course.setLevel(dto.getLevel());
        course.setLanguage(dto.getLanguage());
        course.setInstructor(user);

        if (dto.getThumbnail() != null && !dto.getThumbnail().isEmpty()) {
            course.setThumbnail(dto.getThumbnail().getBytes());
        }

        course = courseRepository.save(course);

        for (CourseRequestDTO.TopicDTO topicDto : dto.getTopics()) {
            Topic topic = new Topic();
            topic.setName(topicDto.getName());
            topic.setCourse(course);
            topic = topicRepository.save(topic);

            for (CourseRequestDTO.SubtopicDTO subtopicDto : topicDto.getSubtopics()) {
                Subtopic subtopic = new Subtopic();
                subtopic.setName(subtopicDto.getName());
                subtopic.setTopic(topic);
                subtopic = subtopicRepository.save(subtopic);

                for (CourseRequestDTO.VideoDTO videoDto : subtopicDto.getVideos()) {
                    Video video = new Video();
                    video.setTitle(videoDto.getTitle());
                    video.setDescription(videoDto.getDescription());
                    video.setUrl(videoDto.getUrl());
                    video.setSubtopic(subtopic);
                    videoRepository.save(video);
                }
            }
        }

        CourseOptions courseOption = new CourseOptions();
        courseOption.setBenefits(dto.getBenefits());
        courseOption.setPrerequisites(dto.getPrerequisites());
        courseOption.setCourse(course);
        courseOptionRepository.save(courseOption);

        for (CourseRequestDTO.StudyMaterialDTO materialDto : dto.getStudyMaterials()) {
            StudyMaterial material = new StudyMaterial();
            material.setName(materialDto.getName());
            material.setCourse(course);

            if (materialDto.getFile() != null && !materialDto.getFile().isEmpty()) {
                material.setFileData(materialDto.getFile().getBytes());
                material.setFileName(materialDto.getFile().getOriginalFilename());
            }

            studyMaterialRepository.save(material);
        }
    }
    
    @Override
    @Transactional
    public List<CourseResponseDTO> getCoursesForInstructor(User instructor) {
        List<Course> courses = courseRepository.findByInstructor(instructor);
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
                                        })
                                        .collect(Collectors.toList()));

                                return subtopicDTO;
                            })
                            .collect(Collectors.toList()));

                    return topicDTO;
                })
                .collect(Collectors.toList()));

        CourseOptions options = courseOptionRepository.findByCourse(course);
        if (options != null) {
            CourseResponseDTO.CourseOptionDTO optionDTO = new CourseResponseDTO.CourseOptionDTO();
            optionDTO.setBenefits(options.getBenefits());
            optionDTO.setPrerequisites(options.getPrerequisites());
            dto.setCourseOptions(optionDTO);
        }

        dto.setStudyMaterials(course.getStudyMaterials().stream()
                .map(material -> {
                    CourseResponseDTO.StudyMaterialDTO matDTO = new CourseResponseDTO.StudyMaterialDTO();
                    matDTO.setName(material.getName());
                    matDTO.setFileName(material.getFileName());
                    matDTO.setFileUrl("http://localhost:9092/auth/courses/" + course.getCourseId() + "/materials/" + material.getFileName());
                    matDTO.setDownloadUrl("http://localhost:9092/auth/courses/" + course.getCourseId() + "/materials/" + material.getFileName() + "?download=true");
                    return matDTO;
                })
                .collect(Collectors.toList()));

        return dto;
    }
    
    @Override
    @Transactional
    public void updateCourse(Long courseId, CourseRequestDTO dto) throws IOException {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (dto.getTitle() != null) {
            course.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            course.setDescription(dto.getDescription());
        }
        if (dto.getPrice() != null) {
            course.setPrice(dto.getPrice());
        }
        if (dto.getCategory() != null) {
            course.setCategory(dto.getCategory());
        }
        if (dto.getLevel() != null) {
            course.setLevel(dto.getLevel());
        }
        if (dto.getLanguage() != null) {
            course.setLanguage(dto.getLanguage());
        }
        if (dto.getThumbnail() != null && !dto.getThumbnail().isEmpty()) {
            course.setThumbnail(dto.getThumbnail().getBytes());
        }
        
        if (dto.getBenefits() != null && !dto.getBenefits().isEmpty()) {
            CourseOptions courseOptions = courseOptionRepository.findByCourse(course);
            if (courseOptions != null) {
                courseOptions.setBenefits(dto.getBenefits());
                System.out.println("Updated benefits to: " + dto.getBenefits());
                courseOptionRepository.save(courseOptions);
            }
        }

        if (dto.getPrerequisites() != null && !dto.getPrerequisites().isEmpty()) {
            CourseOptions courseOptions = courseOptionRepository.findByCourse(course);
            if (courseOptions != null) {
                courseOptions.setPrerequisites(dto.getPrerequisites());
                System.out.println("Updated prerequisites to: " + dto.getPrerequisites());
                courseOptionRepository.save(courseOptions);
            }
        }
               
        if (dto.getTopics() != null && !dto.getTopics().isEmpty()) {
            List<Topic> existingTopics = course.getTopics();

            for (int i = 0; i < dto.getTopics().size(); i++) {
                CourseRequestDTO.TopicDTO topicDTO = dto.getTopics().get(i);

                if (i < existingTopics.size()) {
                    Topic topic = existingTopics.get(i);

                    if (topicDTO.getName() != null && !topicDTO.getName().isEmpty()) {
                        topic.setName(topicDTO.getName());
                        System.out.println("Updated topic name to: " + topicDTO.getName());
                    }

                    if (topicDTO.getSubtopics() != null && !topicDTO.getSubtopics().isEmpty()) {
                        List<Subtopic> existingSubtopics = topic.getSubtopics();

                        for (int j = 0; j < topicDTO.getSubtopics().size(); j++) {
                            CourseRequestDTO.SubtopicDTO subtopicDTO = topicDTO.getSubtopics().get(j);

                            if (j < existingSubtopics.size()) {
                                Subtopic subtopic = existingSubtopics.get(j);

                                if (subtopicDTO.getName() != null && !subtopicDTO.getName().isEmpty()) {
                                    subtopic.setName(subtopicDTO.getName());
                                    System.out.println("Updated subtopic name to: " + subtopicDTO.getName());
                                }

                                if (subtopicDTO.getVideos() != null && !subtopicDTO.getVideos().isEmpty()) {
                                    List<Video> existingVideos = subtopic.getVideos();

                                    for (int k = 0; k < subtopicDTO.getVideos().size(); k++) {
                                        CourseRequestDTO.VideoDTO videoDTO = subtopicDTO.getVideos().get(k);

                                        if (k < existingVideos.size()) {
                                            Video video = existingVideos.get(k);

                                            if (videoDTO.getTitle() != null && !videoDTO.getTitle().isEmpty()) {
                                                video.setTitle(videoDTO.getTitle());
                                                System.out.println("Updated video title to: " + videoDTO.getTitle());
                                            }

                                            if (videoDTO.getDescription() != null && !videoDTO.getDescription().isEmpty()) {
                                                video.setDescription(videoDTO.getDescription());
                                                System.out.println("Updated video description to: " + videoDTO.getDescription());
                                            }

                                            if (videoDTO.getUrl() != null && !videoDTO.getUrl().isEmpty()) {
                                                video.setUrl(videoDTO.getUrl());
                                                System.out.println("Updated video URL to: " + videoDTO.getUrl());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (dto.getStudyMaterials() != null) {
            for (CourseRequestDTO.StudyMaterialDTO materialDto : dto.getStudyMaterials()) {
                Optional<StudyMaterial> optionalMaterial = studyMaterialRepository.findByIdAndCourse_CourseId(materialDto.getId(), course.getCourseId());

                if (optionalMaterial.isPresent()) {
                    StudyMaterial material = optionalMaterial.get();

                    if (materialDto.getFile() != null && !materialDto.getFile().isEmpty()) {
                        material.setFileData(materialDto.getFile().getBytes());
                        material.setFileName(materialDto.getFile().getOriginalFilename());
                    }

                    if (materialDto.getName() != null && !materialDto.getName().isEmpty()) {
                        material.setName(materialDto.getName());
                    }

                    studyMaterialRepository.save(material);
                }
            }
        }
        courseRepository.save(course);
    }
    
    @Override
    @Transactional
    public void deleteCourse(Long courseId, User instructor) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getInstructor().getId().equals(instructor.getId())) {
            throw new RuntimeException("Forbidden: You can only delete your own courses.");
        }
        courseRepository.delete(course);
    }
    
    @Override
    @Transactional
    public void setCoursePublishedStatus(Long courseId, boolean published, User instructor) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getInstructor().getId().equals(instructor.getId())) {
            throw new RuntimeException("Unauthorized: You are not the owner of this course");
        }

        course.setPublished(published);

        courseRepository.save(course);
    }

}
