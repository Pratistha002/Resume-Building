package com.saarthix.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
@Document(collection = "rolePreparations")
public class RolePreparation {
    @Id
    private String id;
    private String studentId;
    private String roleName;
    private LocalDate preparationStartDate;
    private Integer totalMonths;
    private Boolean isActive;
    private Boolean active; // Alias for isActive for compatibility
    private Map<String, SkillProgress> skillProgress;

    @Data
    public static class SkillProgress {
        private Boolean completed;
        private LocalDate completedDate;
        private Integer score; // Test score if applicable
    }

    public Boolean getIsActive() {
        return isActive != null ? isActive : (active != null ? active : false);
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
        this.active = isActive; // Keep both in sync
    }
}

