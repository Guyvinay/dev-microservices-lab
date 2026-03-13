package com.dev.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;


@Entity
@Table(name = "field_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldType {

    @Id
    @Column(length = 50)
    @Enumerated(value = EnumType.STRING)
    private FieldTypeEnum id;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}