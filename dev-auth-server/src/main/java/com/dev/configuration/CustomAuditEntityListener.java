package com.dev.configuration;

import com.dev.dto.JwtTokenDto;
import com.dev.entity.AuthAuditRevisionEntity;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            try {
                if (authentication.getDetails() instanceof JwtTokenDto) {
                    JwtTokenDto jwtTokenDto = (JwtTokenDto) authentication.getDetails();
                    if (jwtTokenDto != null && jwtTokenDto.getUserBaseInfo() != null) {
                        auditRevisionEntity.setUserModified(jwtTokenDto.getUserBaseInfo().getEmail());
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
