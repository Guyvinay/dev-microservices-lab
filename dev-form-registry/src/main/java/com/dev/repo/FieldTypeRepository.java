package com.dev.repo;

import com.dev.entity.FieldType;
import com.dev.entity.FieldTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FieldTypeRepository extends JpaRepository<FieldType, FieldTypeEnum> {
}
