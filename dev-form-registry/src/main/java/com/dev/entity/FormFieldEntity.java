package com.dev.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "form_field")
@Getter
@Setter
public class FormFieldEntity {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "form_id", nullable = false)
    private UUID formId;

    @Column(name = "field_id", nullable = false)
    private UUID fieldId;

    @Column(name = "field_order", nullable = false)
    private Integer fieldOrder;

    @Column(name = "required_override")
    private Boolean requiredOverride;

    @Column(name = "override_json", columnDefinition = "nvarchar")
    private String overrideJson;

}