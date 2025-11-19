package com.saarthix.service;

import com.saarthix.model.SkillTest;
import com.saarthix.model.blueprint.Blueprint;
import com.saarthix.repository.SkillTestRepository;
import com.saarthix.service.blueprint.BlueprintService;
import com.saarthix.service.OpenAIService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SkillTestService {

    private final SkillTestRepository skillTestRepository;
    private final BlueprintService blueprintService;
    private final OpenAIService openAIService;

    public SkillTestService(
            SkillTestRepository skillTestRepository,
            BlueprintService blueprintService,
            OpenAIService openAIService) {
        this.skillTestRepository = skillTestRepository;
        this.blueprintService = blueprintService;
        this.openAIService = openAIService;
    }

    public SkillTest getOrCreateTest(String roleName, String skillName, String studentId) {
        Optional<SkillTest> existing = skillTestRepository.findByStudentIdAndRoleNameAndSkillName(studentId, roleName, skillName);
        if (existing.isPresent()) {
            SkillTest test = existing.get();
            if ("not_started".equals(test.getStatus()) || "in_progress".equals(test.getStatus())) {
                return test;
            }
        }

        // Get blueprint to understand skill requirements
        Blueprint blueprint = blueprintService.getRoleDetails(roleName);
        if (blueprint == null) {
            throw new RuntimeException("Role blueprint not found: " + roleName);
        }

        // Create new test
        SkillTest test = new SkillTest();
        test.setRoleName(roleName);
        test.setSkillName(skillName);
        test.setStudentId(studentId);
        test.setStatus("not_started");
        test.setStartedAt(LocalDateTime.now());

        // Generate questions using OpenAI (or use default questions)
        List<SkillTest.Question> questions = generateQuestions(roleName, skillName, blueprint);
        test.setQuestions(questions);
        test.setTotalQuestions(questions.size());

        return skillTestRepository.save(test);
    }

    public SkillTest startTest(String roleName, String skillName, String studentId) {
        SkillTest test = getOrCreateTest(roleName, skillName, studentId);
        test.setStatus("in_progress");
        test.setStartedAt(LocalDateTime.now());
        return skillTestRepository.save(test);
    }

    public SkillTest submitTest(String roleName, String skillName, String studentId, List<SkillTest.Question> answeredQuestions) {
        SkillTest test = skillTestRepository.findByStudentIdAndRoleNameAndSkillName(studentId, roleName, skillName)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        // Calculate score
        int correct = 0;
        for (SkillTest.Question question : answeredQuestions) {
            if (question.getCorrectAnswer() != null && question.getCorrectAnswer().equals(question.getSelectedAnswer())) {
                question.setIsCorrect(true);
                correct++;
            } else {
                question.setIsCorrect(false);
            }
        }

        test.setQuestions(answeredQuestions);
        test.setScore(correct);
        test.setStatus("completed");
        test.setCompletedAt(LocalDateTime.now());

        SkillTest saved = skillTestRepository.save(test);

        // Update role preparation with test score
        // This will be handled by the controller

        return saved;
    }

    private List<SkillTest.Question> generateQuestions(String roleName, String skillName, Blueprint blueprint) {
        // Generate basic questions - in a real system, you'd use OpenAI or have a question bank
        List<SkillTest.Question> questions = new ArrayList<>();

        // For now, create sample questions
        // In production, you'd fetch from a question bank or generate using AI
        for (int i = 1; i <= 5; i++) {
            SkillTest.Question question = new SkillTest.Question();
            question.setQuestionId("q" + i);
            question.setQuestionText("Sample question " + i + " about " + skillName + " for " + roleName + "?");
            question.setOptions(List.of("Option A", "Option B", "Option C", "Option D"));
            question.setCorrectAnswer("Option A");
            questions.add(question);
        }

        return questions;
    }
}

