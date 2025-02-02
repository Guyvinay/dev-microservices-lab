package com.dev.auth.repository;

import com.dev.auth.entity.OrganizationModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrganizationModelRepository extends JpaRepository<OrganizationModel, UUID> {
}
