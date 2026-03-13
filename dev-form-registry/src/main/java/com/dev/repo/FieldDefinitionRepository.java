package com.dev.repo;

import com.dev.entity.FieldDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FieldDefinitionRepository extends JpaRepository<FieldDefinition, UUID> {
    Optional<FieldDefinition> findByLabelAndSpaceId(String emailAddress, Long id);
}
