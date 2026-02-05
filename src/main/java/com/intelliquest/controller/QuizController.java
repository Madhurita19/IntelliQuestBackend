package com.intelliquest.controller;

import com.intelliquest.dto.QuizResultDTO;
import com.intelliquest.dto.QuizStatsDTO;
import com.intelliquest.dto.QuizSubmissionDTO;
import com.intelliquest.model.Quiz;
import com.intelliquest.model.User;
import com.intelliquest.service.QuizService;
import com.intelliquest.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;
    private final UserService userService;

    @PostMapping(value = "/create-quiz", consumes = {"multipart/form-data"})
    public ResponseEntity<?> createQuiz(
            @RequestPart("quizDTO") QuizSubmissionDTO quizDTO,
            @RequestPart("thumbnail") MultipartFile thumbnailFile,
            @AuthenticationPrincipal Object principal) {
        try {
            String email = null;

            if (principal instanceof OAuth2User oAuth2User) {
                email = oAuth2User.getAttribute("email");
            } else if (principal instanceof UserDetails userDetails) {
                email = userDetails.getUsername();
            } else if (principal instanceof String stringPrincipal) {
                email = stringPrincipal;
            }

            if (email == null) {
                return ResponseEntity.status(401).body("Email is null after principal inspection.");
            }

            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body("No user found with email = " + email);
            }

            User user = userOpt.get();

            if (!user.getRole().equals(User.UserRole.INSTRUCTOR)) {
                return ResponseEntity.status(403).body("Forbidden: Only instructors can create quizzes.");
            }

            quizService.createQuiz(quizDTO, user, thumbnailFile);
            return ResponseEntity.ok("Quiz created successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to create quiz: " + e.getMessage());
        }
    }

    @GetMapping("/get-quiz/{id}")
    public ResponseEntity<?> getQuizById(@PathVariable Long id) {
        try {
            Quiz quiz = quizService.getQuiz(id);
            QuizSubmissionDTO dto = mapToDTO(quiz);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("No quiz is found with id: " + id);
        }
    }
    
    @GetMapping("/get-all-quizzes")
    public ResponseEntity<?> getAllQuizzes() {
        try {
            List<Quiz> quizzes = quizService.getAllQuizzes();
            if (quizzes.isEmpty()) {
                return ResponseEntity.status(404).body("No quizzes available.");
            }

            List<QuizSubmissionDTO> dtoList = quizzes.stream()
                    .map(this::mapToDTO)
                    .toList();

            return ResponseEntity.ok(dtoList);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to retrieve quizzes: " + e.getMessage());
        }
    }
    
    @GetMapping("/get-all-instructor-quizzes")
    public ResponseEntity<?> getInstructorQuizzes(@AuthenticationPrincipal Object principal) {
        try {
            String email = null;

            if (principal instanceof OAuth2User oAuth2User) {
                email = oAuth2User.getAttribute("email");
            } else if (principal instanceof UserDetails userDetails) {
                email = userDetails.getUsername();
            } else if (principal instanceof String stringPrincipal) {
                email = stringPrincipal;
            }

            if (email == null) {
                return ResponseEntity.status(401).body("Unauthorized: Email not found.");
            }

            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body("Unauthorized: User not found.");
            }

            User user = userOpt.get();
            if (!user.getRole().equals(User.UserRole.INSTRUCTOR)) {
                return ResponseEntity.status(403).body("Forbidden: Only instructors can view their quizzes.");
            }

            List<Quiz> quizzes = quizService.getQuizzesByInstructor(user);

            if (quizzes.isEmpty()) {
                return ResponseEntity.status(404).body("You have not created any quizzes yet.");
            }

            List<QuizSubmissionDTO> dtoList = quizzes.stream()
                    .map(this::mapToDTO)
                    .toList();

            return ResponseEntity.ok(dtoList);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to retrieve your quizzes: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete-quizzes/{id}")
    public ResponseEntity<String> deleteQuiz(@PathVariable Long id, 
                                             @AuthenticationPrincipal Object principal) {
        try {
            String email = null;

            if (principal instanceof OAuth2User oAuth2User) {
                email = oAuth2User.getAttribute("email");
            } else if (principal instanceof UserDetails userDetails) {
                email = userDetails.getUsername();
            } else if (principal instanceof String stringPrincipal) {
                email = stringPrincipal;
            }

            if (email == null) {
                return ResponseEntity.status(401).body("Unauthorized: Email not found.");
            }

            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body("Unauthorized: User not found.");
            }

            User user = userOpt.get();

            if (!user.getRole().equals(User.UserRole.INSTRUCTOR)) {
                return ResponseEntity.status(403).body("Forbidden: Only instructors can delete quizzes.");
            }

            quizService.deleteQuiz(id, user);
            return ResponseEntity.ok("Quiz deleted successfully");

        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body("Failed to delete quiz: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal error: " + e.getMessage());
        }
    }
    
    @GetMapping("/total-quizzes")
    public ResponseEntity<?> getTotalQuizzesByInstructor(@AuthenticationPrincipal Object principal) {
        try {
            String email = null;

            if (principal instanceof OAuth2User oAuth2User) {
                email = oAuth2User.getAttribute("email");
            } else if (principal instanceof UserDetails userDetails) {
                email = userDetails.getUsername();
            } else if (principal instanceof String stringPrincipal) {
                email = stringPrincipal;
            }

            if (email == null) {
                return ResponseEntity.status(401).body("Unauthorized: Email not found.");
            }

            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body("Unauthorized: User not found.");
            }

            User user = userOpt.get();
            if (!user.getRole().equals(User.UserRole.INSTRUCTOR)) {
                return ResponseEntity.status(403).body("Forbidden: Only instructors can access quiz statistics.");
            }

            long count = quizService.getTotalQuizzesByInstructor(user.getId());

            return ResponseEntity.ok(Map.of("totalQuizzes", count));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to retrieve total quiz count: " + e.getMessage()));
        }
    }
    
    @PostMapping("/submit-quiz")
    public ResponseEntity<?> submitQuizResult(@RequestBody QuizResultDTO resultDTO) {
        try {
            quizService.saveResult(resultDTO);
            return ResponseEntity.ok("Quiz result saved successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to save quiz result: " + e.getMessage());
        }
    }

    @GetMapping("/quiz/{quizId}/participants")
    public ResponseEntity<?> getTotalParticipants(@PathVariable Long quizId) {
        try {
            long count = quizService.getUniqueParticipantCountOrThrow(quizId);
            return ResponseEntity.ok(Map.of("totalParticipants", count));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch participant count: " + e.getMessage()));
        }
    }
    
    @GetMapping("/total-marks")
    public ResponseEntity<?> getTotalMarksForLoggedInUser(@AuthenticationPrincipal Object principal) {
        try {
            String email = null;

            if (principal instanceof OAuth2User oAuth2User) {
                email = oAuth2User.getAttribute("email");
            } else if (principal instanceof UserDetails userDetails) {
                email = userDetails.getUsername();
            }

            if (email == null) {
                return ResponseEntity.status(400).body(Map.of("error", "Unable to identify user"));
            }

            long totalMarks = quizService.getTotalMarksForUserByEmail(email);
            return ResponseEntity.ok(Map.of("totalMarks", totalMarks));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch total marks: " + e.getMessage()));
        }
    }
    
    @GetMapping("/quiz-results")
    public ResponseEntity<List<QuizResultDTO>> getQuizResultsForCurrentUser(Principal principal) {
        Optional<User> userOpt = userService.findByEmail(principal.getName());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        User user = userOpt.get();
        List<QuizResultDTO> results = quizService.getQuizResultsForUser(user.getId());
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/quiz-stats")
    public ResponseEntity<QuizStatsDTO> getUserStats(@AuthenticationPrincipal Object principal) {
        String email = null;
        if (principal instanceof OAuth2User oAuth2User) {
            email = oAuth2User.getAttribute("email");
        } else if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        }

        if (email == null) {
            return ResponseEntity.status(400).build();
        }

        QuizStatsDTO stats = quizService.getStatsForUser(email);
        return ResponseEntity.ok(stats);
    }
    
    private String getTierForRank(int rank) {
        if (rank >= 1 && rank <= 3) {
            return "diamond";
        } else if (rank <= 10) {
            return "gold";
        } else if (rank <= 20) {
            return "silver";
        } else if (rank <= 50) {
            return "bronze";
        } else {
            return "participant";
        }
    }

    @GetMapping("/leaderboard-data")
    public ResponseEntity<List<Map<String, Object>>> getLeaderboard(@AuthenticationPrincipal Object principal) {
        try {
            String currentUserEmail = null;

            if (principal instanceof OAuth2User oAuth2User) {
                currentUserEmail = oAuth2User.getAttribute("email");
            } else if (principal instanceof UserDetails userDetails) {
                currentUserEmail = userDetails.getUsername();
            } else if (principal instanceof String stringPrincipal) {
                currentUserEmail = stringPrincipal;
            }

            if (currentUserEmail == null) {
                return ResponseEntity.status(401).body(List.of());
            }

            List<Map<String, Object>> rawLeaderboard = quizService.getLeaderboardData();

            rawLeaderboard.sort((a, b) -> {
                Long pointsB = (Long) b.get("points");
                Long pointsA = (Long) a.get("points");
                return pointsB.compareTo(pointsA);
            });

            List<Map<String, Object>> rankedLeaderboard = new java.util.ArrayList<>();
            int rank = 1;
            for (Map<String, Object> entry : rawLeaderboard) {
                String email = (String) entry.get("email");
                long points = (Long) entry.get("points");

                Map<String, Object> mapWithRank = new java.util.HashMap<>();
                mapWithRank.put("rank", rank);
                mapWithRank.put("username", entry.get("name") != null ? entry.get("name") : entry.get("username"));
                mapWithRank.put("email", email);
                mapWithRank.put("points", points);
                mapWithRank.put("avatar", "/placeholder.svg?height=40&width=40");
                mapWithRank.put("tier", getTierForRank(rank));
                mapWithRank.put("isCurrentUser", email.equals(currentUserEmail));
                rankedLeaderboard.add(mapWithRank);
                rank++;
            }
            return ResponseEntity.ok(rankedLeaderboard);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(List.of());
        }
    }

    @GetMapping("/quiz/{id}/thumbnail")
    public ResponseEntity<byte[]> getQuizThumbnail(@PathVariable Long id) {
        try {
            Quiz quiz = quizService.getQuiz(id);
            byte[] thumbnailBytes = quiz.getThumbnail();

            if (thumbnailBytes == null || thumbnailBytes.length == 0) {
                return ResponseEntity.notFound().build();
            }

            String formatName = detectImageFormat(thumbnailBytes);
            MediaType mediaType = switch (formatName) {
                case "jpeg", "jpg" -> MediaType.IMAGE_JPEG;
                case "png" -> MediaType.IMAGE_PNG;
                case "gif" -> MediaType.IMAGE_GIF;
                default -> MediaType.APPLICATION_OCTET_STREAM;
            };

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(mediaType);
            headers.setContentLength(thumbnailBytes.length);

            return new ResponseEntity<>(thumbnailBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    private String detectImageFormat(byte[] imageBytes) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes)) {
            BufferedImage image = ImageIO.read(bais);
            if (image == null) return "unknown";

            String[] formatNames = ImageIO.getReaderFormatNames();
            for (String format : formatNames) {
                if (ImageIO.getImageReadersByFormatName(format).hasNext()) {
                    return format.toLowerCase();
                }
            }
        } catch (Exception ignored) {
        }
        return "unknown";
    }    

    private QuizSubmissionDTO mapToDTO(Quiz quiz) {
        return QuizSubmissionDTO.builder()
        		.id(quiz.getId())
                .title(quiz.getTitle())
                .playingTime(quiz.getPlayingTime())
                .level(quiz.getLevel())                  
                .category(quiz.getCategory())
                .thumbnailUrl("http://localhost:9092/auth/quiz/" + quiz.getId() + "/thumbnail")
                .instructorId(quiz.getInstructor() != null ? quiz.getInstructor().getId() : null)
                .instructorName(quiz.getInstructor() != null ? quiz.getInstructor().getUsername() : null)
                .instructorEmail(quiz.getInstructor() != null ? quiz.getInstructor().getEmail() : null)
                .questions(quiz.getQuestions().stream().map(q ->
                        QuizSubmissionDTO.Question.builder()
                                .id(q.getId())
                                .question(q.getQuestion())
                                .correctOptionId(q.getCorrectOptionId())
                                .marksForCorrect(q.getMarksForCorrect())         
                                .marksForIncorrect(q.getMarksForIncorrect()) 
                                .options(q.getOptions().stream().map(o ->
                                        QuizSubmissionDTO.Option.builder()
                                                .id(o.getId())
                                                .text(o.getText())
                                                .build()
                                ).toList())
                                .build()
                ).toList())
                .build();
    }
}
