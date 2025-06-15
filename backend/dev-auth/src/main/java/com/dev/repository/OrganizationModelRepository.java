package com.dev.repository;

import com.dev.entity.OrganizationModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrganizationModelRepository extends JpaRepository<OrganizationModel, UUID> {

    Optional<OrganizationModel> findByOrgEmail(String orgEmail);

    boolean existsByOrgEmail(String orgEmail);

    boolean existsByOrgName(String orgName);

}
