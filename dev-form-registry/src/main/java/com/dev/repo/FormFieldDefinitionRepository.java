package com.dev.repo;

import com.dev.entity.FormFieldDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FormFieldDefinitionRepository extends JpaRepository<FormFieldDefinition, UUID> {
    boolean existsByFormIdAndFieldDefinitionId(Long id, UUID id1);

    Optional<FormFieldDefinition> findByFormIdAndFieldDefinitionId(Long id, UUID id1);

    List<FormFieldDefinition> findByFormId(Long formId);
}
