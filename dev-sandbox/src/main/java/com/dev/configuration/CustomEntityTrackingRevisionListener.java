package com.dev.configuration;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.EntityTrackingRevisionListener;
import org.hibernate.envers.RevisionType;

@Slf4j
public class CustomEntityTrackingRevisionListener implements EntityTrackingRevisionListener {
    @Override
    public void entityChanged(Class aClass, String s, Object o, RevisionType revisionType, Object o1) {
        log.info("entityChanged called: class: {}, revisionType: {}, Object: {}", aClass, revisionType, o1);
    }

    @Override
    public void newRevision(Object o) {
        log.info("entityChanged called: Object {}", o);
    }
}

