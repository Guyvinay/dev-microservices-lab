package com.dev.entity;

import com.dev.utility.JpaJsonConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "form_field_definition")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormFieldDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "form_id", nullable = false)
    private Long formId;

    @Column(name = "field_definition_id", nullable = false)
    private UUID fieldDefinitionId;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "label_override", length = 255)
    private String labelOverride;

    @Column(name = "description_override", columnDefinition = "TEXT")
    private String descriptionOverride;

    @Column(name = "ui_override", columnDefinition = "TEXT")
    @Convert(converter = JpaJsonConverter.class)
    private Map<String,Object> uiOverride;

    @Column(name = "validation_override", columnDefinition = "TEXT")
    @Convert(converter = JpaJsonConverter.class)
    private Map<String,Object> validationOverride;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}