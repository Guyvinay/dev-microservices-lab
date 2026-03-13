package com.dev.entity;

import com.dev.utility.JpaJsonConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;


@Entity
@Table(name = "field_definition")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "space_id", nullable = false)
    private Long spaceId;

    @Column(name = "field_type_id", length = 50, nullable = false)
    @Enumerated(value = EnumType.STRING)
    private FieldTypeEnum fieldTypeId;

    @Column(nullable = false, length = 255)
    private String label;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = JpaJsonConverter.class)
    private Map<String, Object> ui;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = JpaJsonConverter.class)
    private Map<String, Object> validation;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

}