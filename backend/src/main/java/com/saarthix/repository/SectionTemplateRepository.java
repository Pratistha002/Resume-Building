package com.saarthix.repository;

import com.saarthix.model.SectionTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionTemplateRepository extends MongoRepository<SectionTemplate, String> {
    List<SectionTemplate> findByIsActiveTrue();
    List<SectionTemplate> findByTitle(String title);
}

