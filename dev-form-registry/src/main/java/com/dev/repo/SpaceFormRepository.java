package com.dev.repo;

import com.dev.entity.SpaceForm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpaceFormRepository extends JpaRepository<SpaceForm, Long> {
    Optional<SpaceForm> findByTitleAndSpaceId(String jobApplication, Long id);
}
