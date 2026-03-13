package com.dev.repo;

import com.dev.entity.FormFieldDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FormFieldDefinitionRepository extends JpaRepository<FormFieldDefinition, UUID> {
    boolean existsByFormIdAndFieldDefinitionId(Long id, UUID id1);
}
