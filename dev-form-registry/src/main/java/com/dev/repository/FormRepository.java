package com.dev.repository;

import com.dev.entity.FormEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FormRepository extends JpaRepository<FormEntity, UUID> {

    List<FormEntity> findBySpaceId(UUID spaceId);
}
