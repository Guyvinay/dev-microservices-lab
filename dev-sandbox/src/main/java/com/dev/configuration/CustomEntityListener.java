package com.dev.configuration;

import com.dev.dto.JwtTokenDto;
import com.dev.modal.AuditRevisionEntity;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class CustomEntityListener implements RevisionListener {
    @Override
    public void newRevision(Object o) {
        AuditRevisionEntity auditRevisionEntity = (AuditRevisionEntity) o;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null) {
            JwtTokenDto tokenDto = (JwtTokenDto) authentication.getDetails();
            auditRevisionEntity.setModifiedBy(tokenDto.getUserBaseInfo().getId().toString());
        }
    }
}
