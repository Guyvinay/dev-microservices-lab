package com.dev.repository;

import com.dev.entity.UserProfileRoleMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserProfileRoleMappingRepository extends JpaRepository<UserProfileRoleMapping, UUID> {
}
