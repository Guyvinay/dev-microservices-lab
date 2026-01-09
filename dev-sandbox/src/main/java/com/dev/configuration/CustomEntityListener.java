package com.dev.configuration;

import com.dev.dto.JwtToken;
import com.dev.modal.AuditRevisionEntity;
import com.dev.utility.AuthContextUtil;
import org.hibernate.envers.RevisionListener;

public class CustomEntityListener implements RevisionListener {
    @Override
    public void newRevision(Object o) {
        AuditRevisionEntity auditRevisionEntity = (AuditRevisionEntity) o;
        JwtToken accessJwtToken = AuthContextUtil.getJwtFromSecurityContextOrNull();
        if(accessJwtToken != null) {
            auditRevisionEntity.setModifiedBy(accessJwtToken.getUserBaseInfo().getId().toString());
        }
    }
}
