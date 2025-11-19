package com.saarthix.service;

import com.saarthix.model.RolePreparation;
import com.saarthix.model.blueprint.Blueprint;
import com.saarthix.repository.RolePreparationRepository;
import com.saarthix.repository.blueprint.BlueprintRepository;
import com.saarthix.service.blueprint.BlueprintService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RolePreparationService {

    private final RolePreparationRepository rolePreparationRepository;
    private final BlueprintRepository blueprintRepository;
    private final BlueprintService blueprintService;

    public RolePreparationService(
            RolePreparationRepository rolePreparationRepository,
            BlueprintRepository blueprintRepository,
            BlueprintService blueprintService) {
        this.rolePreparationRepository = rolePreparationRepository;
        this.blueprintRepository = blueprintRepository;
        this.blueprintService = blueprintService;
    }

    public RolePreparation getPreparation(String roleName, String studentId) {
        return rolePreparationRepository.findByStudentIdAndRoleName(studentId, roleName)
                .orElse(null);
    }

    public List<RolePreparation> getAllPreparations(String studentId) {
        return rolePreparationRepository.findByStudentId(studentId);
    }

    public List<RolePreparation> getActivePreparations(String studentId) {
        List<RolePreparation> byIsActive = rolePreparationRepository.findByStudentIdAndIsActiveTrue(studentId);
        if (byIsActive.isEmpty()) {
            // Fallback to active field
            return rolePreparationRepository.findByStudentIdAndActiveTrue(studentId);
        }
        return byIsActive;
    }

    public RolePreparation startPreparation(String roleName, String studentId, Integer duration) {
        // Check if preparation already exists
        Optional<RolePreparation> existing = rolePreparationRepository.findByStudentIdAndRoleName(studentId, roleName);
        if (existing.isPresent()) {
            RolePreparation prep = existing.get();
            prep.setIsActive(true);
            if (duration != null) {
                prep.setTotalMonths(duration);
            }
            return rolePreparationRepository.save(prep);
        }

        // Get blueprint for the role to initialize skill requirements
        Blueprint blueprint = blueprintService.getRoleDetails(roleName);
        if (blueprint == null) {
            throw new RuntimeException("Role blueprint not found: " + roleName);
        }

        // Create new preparation
        RolePreparation preparation = new RolePreparation();
        preparation.setStudentId(studentId);
        preparation.setRoleName(roleName);
        preparation.setPreparationStartDate(LocalDate.now());
        preparation.setTotalMonths(duration != null ? duration : 6); // Default 6 months
        preparation.setIsActive(true);

        // Initialize skill progress from blueprint
        Map<String, RolePreparation.SkillProgress> skillProgress = new HashMap<>();
        if (blueprint.getSkillRequirements() != null) {
            for (Blueprint.SkillRequirement skillReq : blueprint.getSkillRequirements()) {
                if (skillReq.getSkillName() != null) {
                    RolePreparation.SkillProgress progress = new RolePreparation.SkillProgress();
                    progress.setCompleted(false);
                    skillProgress.put(skillReq.getSkillName(), progress);
                }
            }
        }
        preparation.setSkillProgress(skillProgress);

        return rolePreparationRepository.save(preparation);
    }

    public RolePreparation updateSkillStatus(String roleName, String skillName, String studentId, Boolean completed) {
        RolePreparation preparation = rolePreparationRepository.findByStudentIdAndRoleName(studentId, roleName)
                .orElseThrow(() -> new RuntimeException("Preparation not found"));

        if (preparation.getSkillProgress() == null) {
            preparation.setSkillProgress(new HashMap<>());
        }

        RolePreparation.SkillProgress progress = preparation.getSkillProgress().getOrDefault(skillName, new RolePreparation.SkillProgress());
        
        // If trying to mark as completed, require test first
        if (completed && !progress.getCompleted()) {
            // Check if skill test exists and is passed
            // For now, we'll allow it but in a real system, you'd check test results
            // This is handled by the frontend navigating to test page
        }

        progress.setCompleted(completed);
        if (completed) {
            progress.setCompletedDate(LocalDate.now());
        } else {
            progress.setCompletedDate(null);
            progress.setScore(null);
        }

        preparation.getSkillProgress().put(skillName, progress);
        return rolePreparationRepository.save(preparation);
    }

    public RolePreparation completeSkillTest(String roleName, String skillName, String studentId, Integer score) {
        RolePreparation preparation = rolePreparationRepository.findByStudentIdAndRoleName(studentId, roleName)
                .orElseThrow(() -> new RuntimeException("Preparation not found"));

        if (preparation.getSkillProgress() == null) {
            preparation.setSkillProgress(new HashMap<>());
        }

        RolePreparation.SkillProgress progress = preparation.getSkillProgress().getOrDefault(skillName, new RolePreparation.SkillProgress());
        progress.setCompleted(true);
        progress.setCompletedDate(LocalDate.now());
        progress.setScore(score);

        preparation.getSkillProgress().put(skillName, progress);
        return rolePreparationRepository.save(preparation);
    }
}

