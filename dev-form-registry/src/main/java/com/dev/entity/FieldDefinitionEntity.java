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
@Table(name = "field_definition")
@Getter
@Setter
public class FieldDefinitionEntity {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "space_id", nullable = false)
    private UUID spaceId;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "label", nullable = false, length = 150)
    private String label;

    @Column(name = "data_type", nullable = false, length = 50)
    private String dataType;

    @Column(name = "ui_json", columnDefinition = "nvarchar")
    private String uiJson;

    @Column(name = "validation_json", columnDefinition = "nvarchar")
    private String validationJson;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "created_at")
    private Long createdAt;

    @Column(name = "updated_at")
    private Long updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

}