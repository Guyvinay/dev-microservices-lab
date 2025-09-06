package com.dev.rmq.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Component()
@Target({ElementType.TYPE})
public @interface RabbitListener {
    String queue();
    String value();
}
