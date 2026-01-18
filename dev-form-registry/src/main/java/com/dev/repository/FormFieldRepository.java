package com.dev.repository;

import com.dev.entity.FormFieldEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface FormFieldRepository extends JpaRepository<FormFieldEntity, UUID> {

    List<FormFieldEntity> findByFormId(UUID formId);

    List<FormFieldEntity> findByFormIdOrderByFieldOrderAsc(UUID formId);
}