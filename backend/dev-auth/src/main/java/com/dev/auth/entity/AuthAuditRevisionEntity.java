package com.dev.auth.entity;

import com.dev.auth.configuration.CustomAuditEntityListener;
import jakarta.persistence.*;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

/**
 * AuthAuditRevisionEntity is a custom revision entity used for auditing
 * changes to other entities. It stores revision-related metadata such as
 * the revision number, timestamp, and the user who made the modification.
 * <p>
 * This entity is managed by Hibernate Envers and is automatically updated
 * whenever an audited entity undergoes a change.
 * <p>
 * The `CustomAuditEntityListener` is used to populate additional metadata
 * during a revision.
 *
 * @author Vinay Kr. Singh.
 * @since 26-01-2025
 */
@Entity
@Table(name = "AUTH_REV_INFO_TABLE")
@RevisionEntity(CustomAuditEntityListener.class)
public class AuthAuditRevisionEntity {

    /**
     * Unique identifier for the revision entry.
     * This ID is auto-generated and serves as the primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RevisionNumber
    @Column(name = "ID")
    private Long id;

    /**
     * Timestamp when the revision was created.
     * This is automatically populated by Hibernate Envers.
     */
    @RevisionTimestamp
    @Column(name = "TIMESTAMP")
    private long timestamp;

    /**
     * Stores the username of the user who made changes.
     * This value is set dynamically in the `CustomAuditEntityListener`.
     */
    @Column(name = "USERMODIFIED", columnDefinition = "NVARCHAR(150)")
    private String userModified;

    public void setUserModified(String userModified) {
        this.userModified = userModified;
    }

}
