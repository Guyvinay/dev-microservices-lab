package com.dev.auth.repository;

import com.dev.auth.entity.UserProfileRoleMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserProfileRoleMappingRepository extends JpaRepository<UserProfileRoleMapping, UUID> {
}
