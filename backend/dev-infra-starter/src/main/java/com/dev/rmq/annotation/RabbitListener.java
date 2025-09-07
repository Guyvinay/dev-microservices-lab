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
    String value();                // Bean name
    String queue() default "";     // queue name (optional)
    String exchange() default "";  // exchange name (optional)
    String routingKey() default ""; // routing key for topic (optional)
    String type() default "direct"; // exchange type: direct, topic, fanout
}
