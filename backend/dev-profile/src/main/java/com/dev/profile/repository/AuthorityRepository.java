package com.dev.profile.repository;

import com.dev.profile.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthorityRepository extends JpaRepository<Authority, UUID> {
}
