package com.saarthix.repository;

import com.saarthix.model.SkillTest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillTestRepository extends MongoRepository<SkillTest, String> {
    Optional<SkillTest> findByStudentIdAndRoleNameAndSkillName(String studentId, String roleName, String skillName);
    List<SkillTest> findByStudentId(String studentId);
    List<SkillTest> findByStudentIdAndRoleName(String studentId, String roleName);
}

