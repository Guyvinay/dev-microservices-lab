package com.dev.common.annotations;

import com.dev.dto.privilege.Action;
import com.dev.dto.privilege.Privilege;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Requires {

    Require[] value();

    @interface Require {
        Privilege privilege();
        Action[] actions();
    }

}
