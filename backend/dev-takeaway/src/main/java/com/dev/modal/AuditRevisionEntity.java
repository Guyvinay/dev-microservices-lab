package com.dev.modal;

import com.dev.configuration.CustomEntityListener;
import jakarta.persistence.*;
import lombok.ToString;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;


@Entity
@RevisionEntity(CustomEntityListener.class)
@ToString
@Table(name = "AUTH_REVINFO_TABLE")
public class AuditRevisionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RevisionNumber
    @Column(name="ID")
    private Long id;

    @RevisionTimestamp
    @Column(name= "TIMESTAMP")
    private long timestamp;

    @Column(name = "MODIFIEDBY", columnDefinition = "VARCHAR(150)")
    private String modifiedBy;

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }}
