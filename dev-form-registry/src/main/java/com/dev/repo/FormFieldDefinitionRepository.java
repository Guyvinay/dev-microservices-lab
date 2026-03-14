package com.dev.repo;

import com.dev.dto.FormFieldWithDefinition;
import com.dev.entity.FormFieldDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FormFieldDefinitionRepository extends JpaRepository<FormFieldDefinition, UUID> {

    Optional<FormFieldDefinition> findByFormIdAndFieldDefinitionId(Long id, UUID id1);

    @Query("""
        SELECT new com.dev.dto.FormFieldWithDefinition(ffd, fd)
        FROM FormFieldDefinition ffd
        JOIN FieldDefinition fd
        ON fd.id = ffd.fieldDefinitionId
        WHERE ffd.formId = :formId
    """)
    List<FormFieldWithDefinition> findFields(Long formId);
}
