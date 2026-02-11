package com.dev.rabbitmq.publisher;

import com.dev.dto.rmq.RmqEvent;
import com.dev.logging.MDCKeys;
import com.dev.logging.MDCLoggingUtility;
import com.dev.utility.AuthContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.connection.SimpleResourceHolder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * RabbitMqPublisher provides a tenant-aware abstraction for publishing messages
 * to RabbitMQ exchanges and queues in a multi-tenant environment.
 *
 * <p>
 * The publisher ensures:
 * <ul>
 *   <li>Correct tenant routing via {@link SimpleResourceHolder}</li>
 *   <li>Correlation IDs for message tracking</li>
 *   <li>Convenient APIs for queue, direct exchange, and fanout publishing</li>
 * </ul>
 * </p>
 */
@Slf4j
public class RabbitMqPublisher {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMqPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(RmqEvent rmqEvent) {
        this.publish(null, rmqEvent.getExchange(), rmqEvent.getRoutingKey(), rmqEvent.getPayload(), null);
    }

    /**
     * Publish a message to a specific exchange and routing key for a given tenant.
     *
     * @param tenantId   the tenant identifier; if null/blank, resolved from {@link AuthContextUtil}
     * @param exchange   the exchange name; if blank, the default exchange ("") is used
     * @param routingKey the routing key (or queue name if using default exchange)
     * @param payload    the message payload (converted to JSON string)
     */
    public void publish(String tenantId, String exchange, String routingKey, Object payload, Message message) {
        String resolvedTenant = resolveTenant(tenantId);
        String resolvedExchange = StringUtils.isNotBlank(exchange) ? exchange : "";

        if(message == null) {
            message = MessageBuilder.withBody((String.valueOf(payload)).getBytes(StandardCharsets.UTF_8))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setMessageId(UUID.randomUUID().toString())
                    .build();
        }

        CorrelationData correlationData = new CorrelationData(message.getMessageProperties().getMessageId());

        log.info("Preparing to publish message [tenant={}, exchange={}, routingKey={}, correlationId={}]",
                resolvedTenant, resolvedExchange, routingKey, correlationData.getId());

        try {
            SimpleResourceHolder.bind(rabbitTemplate.getConnectionFactory(), resolvedTenant);

            MessageProperties props = message.getMessageProperties();

            putIfPresent(props, MDCKeys.HEADER_TRACE_ID, String.valueOf(MDC.get(MDCKeys.TRACE_ID)));
            putIfPresent(props, MDCKeys.HEADER_TENANT_ID, String.valueOf(MDC.get(MDCKeys.TENANT_ID)));
            putIfPresent(props, MDCKeys.HEADER_USER_ID, String.valueOf(MDC.get(MDCKeys.USER_ID)));


            rabbitTemplate.convertAndSend(
                    resolvedExchange,
                    routingKey, message, correlationData);

            log.info("Successfully published message [tenant={}, exchange={}, routingKey={}, correlationId={}]",
                    resolvedTenant, resolvedExchange, routingKey, correlationData.getId());

        } catch (Exception e) {
            log.error("Failed to publish message [tenant={}, exchange={}, routingKey={}]",
                    resolvedTenant, resolvedExchange, routingKey, e);
            throw e;
        } finally {
            SimpleResourceHolder.unbind(rabbitTemplate.getConnectionFactory());
            log.info("Unbound tenant={} from connection factory after publish", resolvedTenant);
            MDCLoggingUtility.removeVariablesFromMDCContext();
        }
    }

    private void putIfPresent(MessageProperties props, String key, String value) {
        if (StringUtils.isNotBlank(value)) {
            props.setHeader(key, value);
        }
    }

    /**
     * Convenience method to send a message directly to a queue using the default exchange ("").
     *
     * @param queueName the queue name
     * @param payload   the message payload
     */
    public void sendToQueue(String queueName, Object payload) {
        log.info("Sending message directly to queue='{}' [payloadClass={}]",
                queueName, payload != null ? payload.getClass().getSimpleName() : "null");
        publish(null, "", queueName, payload, null);
    }

    /**
     * Convenience method to send a message directly to a queue using the default exchange ("").
     *
     * @param queueName the queue name
     * @param payload   the message payload
     */
    public void sendToQueue(String exchange, String queueName, Object payload) {
        log.info("Sending message directly to exchange={} queue='{}' [payloadClass={}]", exchange,
                queueName, payload != null ? payload.getClass().getSimpleName() : "null");
        publish(null, exchange, queueName, payload, null);
    }

    /**
     * Convenience method to send a message directly to a queue using the default exchange ("").
     *
     * @param queueName the queue name
     */
    public void sendToQueueWithMessage(String exchange, String queueName, Message message) {
        log.info("Sending message directly to exchange={} queue='{}'", exchange,
                queueName);
        publish(null, exchange, queueName, null, message);
    }

    /**
     * Publish a message to a specified exchange and routing key for a tenant.
     *
     * @param tenantId   the tenant identifier
     * @param exchange   the exchange name
     * @param routingKey the routing key
     * @param payload    the message payload
     */
    public void publishToExchange(String tenantId, String exchange, String routingKey, Object payload) {
        log.info("Publishing to exchange='{}' with routingKey='{}' for tenant={}", exchange, routingKey, tenantId);
        publish(tenantId, exchange, routingKey, payload, null);
    }

    /**
     * Publish a broadcast message to a fanout exchange (routing key ignored).
     *
     * @param tenantId       the tenant identifier
     * @param fanoutExchange the fanout exchange name
     * @param payload        the message payload
     */
    public void publishBroadcast(String tenantId, String fanoutExchange, Object payload) {
        log.info("Publishing broadcast to fanoutExchange='{}' for tenant={}", fanoutExchange, tenantId);
        publish(tenantId, fanoutExchange, "", payload, null);
    }

    /**
     * Resolve the tenant ID, falling back to the current tenant context if not provided.
     *
     * @param tenantId the tenant identifier (maybe null/blank)
     * @return resolved tenant identifier
     */
    private String resolveTenant(String tenantId) {
        String resolved = StringUtils.isNotBlank(tenantId) ? tenantId : AuthContextUtil.getTenantId();
        log.debug("Resolved tenantId={}", resolved);
        return resolved;
    }
}
