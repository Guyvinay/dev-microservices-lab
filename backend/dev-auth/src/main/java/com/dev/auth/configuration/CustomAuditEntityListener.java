package com.dev.auth.configuration;

import com.dev.auth.entity.AuthAuditRevisionEntity;
import org.hibernate.envers.RevisionListener;

/**
 * CustomAuditEntityListener is a Hibernate Envers listener that captures
 * revision details whenever an audited entity is modified.
 * <p>
 * This listener is invoked automatically whenever an entity with auditing
 * enabled undergoes changes. It allows setting custom audit fields such as
 * the user who made the modification.
 *
 * @author Vinay Kr. Singh
 */
public class CustomAuditEntityListener implements RevisionListener {

    /**
     * Called when a new revision is created.
     * This method allows setting additional metadata for audit tracking.
     *
     * @param revisionEntity the entity representing the revision metadata.
     */
    @Override
    public void newRevision(Object revisionEntity) {
        AuthAuditRevisionEntity auditRevisionEntity = (AuthAuditRevisionEntity) revisionEntity;

        // TODO: Replace with actual logic to fetch the authenticated user
        auditRevisionEntity.setUserModified("Vinay Kr. Singh");

        // Additional fields can be configured here if needed.
    }
}
