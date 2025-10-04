package com.dev.rabbitmq.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to declare a tenant-aware Rabbit listener class.
 * Usage:
 * @TenantRabbitListener(
 *   value = "TenantInitListener",
 *   queue = "dev.sandbox.tenant.init.queue",
 *   exchange = "inndev.tenant.direct",
 *   routingKey = "tenant.created",
 *   type = "direct"
 * )
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Component
public @interface TenantRabbitListener {
    /**
     * bean name (unique identifier for this listener)
     */
    String value();

    /**
     * queue name to create / consume from (required)
     */
    String queue();

    /**
     * optionally declare/bind to this exchange; if empty, we fall back to default exchange binding by queue name
     */
    String exchange() default "";

    /**
     * routing key to bind queue to exchange (for direct/topic). For default exchange, you can set routingKey = queueName.
     */
    String routingKey() default "";

    /**
     * exchange type: direct/topic/fanout. Default is "direct" for exact-match routing.
     */
    String type() default "direct";

    /**
     * container tuning: max concurrent consumers
     */
    int maxConcurrentConsumers() default 10;

    /**
     * optional prefetch/pull count for consumers
     */
    int prefetch() default 1;

    /**
     * whether to declare the queue as a quorum queue (recommended for production durability)
     */
    boolean quorum() default true;

    /**
     * whether missing queues should be fatal for container startup
     */
    boolean missingQueuesFatal() default false;
}
