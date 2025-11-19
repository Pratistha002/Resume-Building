package com.saarthix.controller;

import com.saarthix.model.blueprint.Blueprint;
import com.saarthix.model.SectionTemplate;
import com.saarthix.repository.SectionTemplateRepository;
import com.saarthix.service.AdminService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AdminController {

    private final AdminService adminService;
    private final SectionTemplateRepository sectionTemplateRepository;

    public AdminController(AdminService adminService, SectionTemplateRepository sectionTemplateRepository) {
        this.adminService = adminService;
        this.sectionTemplateRepository = sectionTemplateRepository;
    }

    @GetMapping("/blueprints")
    public List<Blueprint> getAllBlueprints() {
        return adminService.getAllBlueprints();
    }

    @GetMapping("/blueprints/{id}")
    public ResponseEntity<Blueprint> getBlueprintById(@PathVariable String id) {
        Blueprint blueprint = adminService.getBlueprintById(id);
        if (blueprint != null) {
            return ResponseEntity.ok(blueprint);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/blueprints")
    public ResponseEntity<Blueprint> createBlueprint(@RequestBody Blueprint blueprint) {
        try {
            Blueprint createdBlueprint = adminService.createBlueprint(blueprint);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBlueprint);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/blueprints/{id}")
    public ResponseEntity<Blueprint> updateBlueprint(@PathVariable String id, @RequestBody Blueprint blueprint) {
        try {
            Blueprint updatedBlueprint = adminService.updateBlueprint(id, blueprint);
            if (updatedBlueprint != null) {
                return ResponseEntity.ok(updatedBlueprint);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/blueprints/{id}")
    public ResponseEntity<Void> deleteBlueprint(@PathVariable String id) {
        boolean deleted = adminService.deleteBlueprint(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/blueprints/active")
    public List<Blueprint> getActiveBlueprints() {
        return adminService.getActiveBlueprints();
    }

    @GetMapping("/blueprints/type/{type}")
    public List<Blueprint> getBlueprintsByType(@PathVariable String type) {
        return adminService.getBlueprintsByType(type);
    }

    // Section Template endpoints
    @GetMapping("/section-templates")
    public List<SectionTemplate> getAllSectionTemplates() {
        return sectionTemplateRepository.findAll();
    }

    @GetMapping("/section-templates/active")
    public List<SectionTemplate> getActiveSectionTemplates() {
        return sectionTemplateRepository.findByIsActiveTrue();
    }

    @GetMapping("/section-templates/{id}")
    public ResponseEntity<SectionTemplate> getSectionTemplateById(@PathVariable String id) {
        return sectionTemplateRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/section-templates")
    public ResponseEntity<SectionTemplate> createSectionTemplate(@RequestBody SectionTemplate sectionTemplate) {
        try {
            SectionTemplate created = sectionTemplateRepository.save(sectionTemplate);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/section-templates/{id}")
    public ResponseEntity<SectionTemplate> updateSectionTemplate(@PathVariable String id, @RequestBody SectionTemplate sectionTemplate) {
        try {
            sectionTemplate.setId(id);
            SectionTemplate updated = sectionTemplateRepository.save(sectionTemplate);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/section-templates/{id}")
    public ResponseEntity<Void> deleteSectionTemplate(@PathVariable String id) {
        try {
            sectionTemplateRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
