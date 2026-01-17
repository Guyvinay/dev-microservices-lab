package com.dev.repository;

import com.dev.entity.FieldDefinitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FieldDefinitionRepository extends JpaRepository<FieldDefinitionEntity, UUID> {

    List<FieldDefinitionEntity> findBySpaceId(UUID spaceId);

}