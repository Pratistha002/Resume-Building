package com.saarthix.controller;

import com.saarthix.model.RolePreparation;
import com.saarthix.service.RolePreparationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role-preparation")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class RolePreparationController {

    private final RolePreparationService rolePreparationService;

    public RolePreparationController(RolePreparationService rolePreparationService) {
        this.rolePreparationService = rolePreparationService;
    }

    @GetMapping("/{roleName}")
    public ResponseEntity<RolePreparation> getPreparation(
            @PathVariable String roleName,
            @RequestParam String studentId) {
        RolePreparation preparation = rolePreparationService.getPreparation(roleName, studentId);
        if (preparation != null) {
            return ResponseEntity.ok(preparation);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/all")
    public List<RolePreparation> getAllPreparations(@RequestParam String studentId) {
        return rolePreparationService.getAllPreparations(studentId);
    }

    @PostMapping("/start/{roleName}")
    public ResponseEntity<RolePreparation> startPreparation(
            @PathVariable String roleName,
            @RequestParam String studentId,
            @RequestParam(required = false) Integer duration) {
        try {
            RolePreparation preparation = rolePreparationService.startPreparation(roleName, studentId, duration);
            return ResponseEntity.ok(preparation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/skill/{roleName}/{skillName}")
    public ResponseEntity<RolePreparation> updateSkillStatus(
            @PathVariable String roleName,
            @PathVariable String skillName,
            @RequestParam String studentId,
            @RequestParam Boolean completed) {
        try {
            RolePreparation preparation = rolePreparationService.updateSkillStatus(roleName, skillName, studentId, completed);
            return ResponseEntity.ok(preparation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/skill/{roleName}/{skillName}/test")
    public ResponseEntity<RolePreparation> completeSkillTest(
            @PathVariable String roleName,
            @PathVariable String skillName,
            @RequestParam String studentId,
            @RequestParam Integer score) {
        try {
            RolePreparation preparation = rolePreparationService.completeSkillTest(roleName, skillName, studentId, score);
            return ResponseEntity.ok(preparation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

