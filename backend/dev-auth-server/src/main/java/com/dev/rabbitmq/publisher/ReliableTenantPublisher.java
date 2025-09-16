package com.dev.rabbitmq.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.connection.SimpleResourceHolder;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReliableTenantPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange tenantEventsExchange;
    private final RabbitAdmin rabbitAdmin;

    public void publishTenantCreated(String tenantId) {
        rabbitAdmin.declareExchange(tenantEventsExchange);

        // 2) Build message and correlation
        String correlationId = UUID.randomUUID().toString();
        MessageProperties props = new MessageProperties();
        props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        props.setMessageId(correlationId);
        Message message = rabbitTemplate.getMessageConverter().toMessage(tenantId, props);
        CorrelationData correlationData = new CorrelationData(correlationId);

        try {
            // Helpful debug: log which vhost we are using
            ConnectionFactory cf = rabbitTemplate.getConnectionFactory();
            if (cf instanceof CachingConnectionFactory ccf) {
                log.info("Publishing using connection vhost={}", ccf.getVirtualHost());
            } else {
                log.info("Publishing using connectionFactory={}", cf.getClass().getSimpleName());
            }
//            SimpleResourceHolder.bind(rabbitTemplate.getConnectionFactory(), resolvePublishTenant(null));
            rabbitTemplate.convertAndSend(tenantEventsExchange.getName(), "tenant.created", message, correlationData);
            log.info("Published correlationId={} tenant={}", correlationId, tenantId);
        } catch (Exception ex) {
            log.error("Publish failed correlationId={} tenant={}", correlationId, tenantId, ex);
            throw new RuntimeException(ex);
        } finally {
//            SimpleResourceHolder.unbind(rabbitTemplate.getConnectionFactory());
        }
    }

    private String resolvePublishTenant(String tenantId) {
        // If you publish to the public vhost default, return "public" (or null logic depending on your CF)
        return StringUtils.isNotBlank(tenantId) ? tenantId : "public";
    }
}
