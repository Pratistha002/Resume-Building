package com.saarthix.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "skillTests")
public class SkillTest {
    @Id
    private String id;
    private String roleName;
    private String skillName;
    private String studentId;
    private List<Question> questions;
    private Integer score;
    private Integer totalQuestions;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String status; // "in_progress", "completed", "not_started"

    @Data
    public static class Question {
        private String questionId;
        private String questionText;
        private List<String> options;
        private String correctAnswer;
        private String selectedAnswer;
        private Boolean isCorrect;
    }
}

