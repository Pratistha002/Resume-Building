package com.saarthix.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "sectionTemplates")
public class SectionTemplate {
    @Id
    private String id;
    private String title;
    private String content;
    private String contentType; // "text", "list", etc.
    private String icon;
    private String color;
    private Boolean isActive;
}

