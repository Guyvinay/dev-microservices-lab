package com.dev.repository;

import com.dev.entity.SpaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpaceRepository extends JpaRepository<SpaceEntity, UUID> {

    List<SpaceEntity> findByTenantId(String tenantId);

}