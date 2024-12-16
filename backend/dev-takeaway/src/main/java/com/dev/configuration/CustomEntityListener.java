package com.dev.configuration;

import com.dev.modal.AuditRevisionEntity;
import org.hibernate.envers.RevisionListener;

public class CustomEntityListener implements RevisionListener {
    @Override
    public void newRevision(Object o) {
        AuditRevisionEntity auditRevisionEntity = (AuditRevisionEntity) o;
        auditRevisionEntity.setModifiedBy("Vinay Kr. Singh");
//        auditRevisionEntity.setTimestamp(System.currentTimeMillis());
    }
}
