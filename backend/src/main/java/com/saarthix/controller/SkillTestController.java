package com.saarthix.controller;

import com.saarthix.model.SkillTest;
import com.saarthix.service.SkillTestService;
import com.saarthix.service.RolePreparationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skill-test")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class SkillTestController {

    private final SkillTestService skillTestService;
    private final RolePreparationService rolePreparationService;

    public SkillTestController(
            SkillTestService skillTestService,
            RolePreparationService rolePreparationService) {
        this.skillTestService = skillTestService;
        this.rolePreparationService = rolePreparationService;
    }

    @GetMapping("/{roleName}/{skillName}")
    public ResponseEntity<SkillTest> getTest(
            @PathVariable String roleName,
            @PathVariable String skillName,
            @RequestParam String studentId) {
        try {
            SkillTest test = skillTestService.getOrCreateTest(roleName, skillName, studentId);
            return ResponseEntity.ok(test);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{roleName}/{skillName}/start")
    public ResponseEntity<SkillTest> startTest(
            @PathVariable String roleName,
            @PathVariable String skillName,
            @RequestParam String studentId) {
        try {
            SkillTest test = skillTestService.startTest(roleName, skillName, studentId);
            return ResponseEntity.ok(test);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{roleName}/{skillName}/submit")
    public ResponseEntity<SkillTest> submitTest(
            @PathVariable String roleName,
            @PathVariable String skillName,
            @RequestParam String studentId,
            @RequestBody List<SkillTest.Question> answeredQuestions) {
        try {
            SkillTest test = skillTestService.submitTest(roleName, skillName, studentId, answeredQuestions);
            
            // Update role preparation with test completion
            if (test.getScore() != null && test.getTotalQuestions() != null) {
                int percentage = (test.getScore() * 100) / test.getTotalQuestions();
                // Mark skill as completed if score is >= 70%
                if (percentage >= 70) {
                    rolePreparationService.completeSkillTest(roleName, skillName, studentId, test.getScore());
                }
            }
            
            return ResponseEntity.ok(test);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

