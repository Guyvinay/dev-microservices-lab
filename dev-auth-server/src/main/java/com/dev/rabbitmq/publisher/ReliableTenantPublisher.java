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

    public void publishTenantCreated(String tenantId) {

        String correlationId = UUID.randomUUID().toString();
        MessageProperties props = new MessageProperties();
        props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        props.setMessageId(correlationId);
        props.setHeader("Authorization", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ7XCJ1c2VySWRcIjpudWxsLFwib3JnXCI6XCJiMzc4YjU4ZC1hYjAwLTQyMmQtOWYzOC0yZDFhZDkzN2U1OTNcIixcIm5hbWVcIjpudWxsLFwiZW1haWxcIjpcInZpa2FzQGdtYWlsLmNvbVwiLFwidGVuYW50SWRcIjpcIjY0MzQ2XCIsXCJjcmVhdGVkRGF0ZVwiOjE3NTg2NTM2MTY2MDEsXCJleHBpcnlEYXRlXCI6MTIxNzU4NjczNDE2NjAxLFwicm9sZXNcIjpbXCIyODc3NDcxMlwiLFwiNDk5NTAwMjRcIixcIjU0MjAwMDk5XCIsXCI2MTUyMDU4OFwiLFwiNjU3MzU5OTJcIixcIjg5NTcwNzEzXCIsXCI5NjMyNDY1N1wiXX0iLCJhdWQiOlsiZGV2LXRha2Vhd2F5IiwiZGV2LXJldmlzZWQiXSwibmJmIjoxNzU4NjczNDE2LCJpc3MiOiJkZXYtYXV0aCIsInBlcm1pc3Npb24iOlsiQURNSU4iLCJVU0VSIiwiTUFOQUdFUiJdLCJleHAiOjEyMTc1ODY3MzQxNiwiaWF0IjoxNzU4NjczNDE2LCJqdGkiOiJiMzY3MWIxZi04YmI1LTQ4NWMtOWZkOC1iYjVhZWM4Y2I5MGEifQ.4MYWXSEd4b7upGoqrdi2toZBIbSA4HGdAxA3XFpwhag");

        Message message = rabbitTemplate.getMessageConverter().toMessage(tenantId, props);
        CorrelationData correlationData = new CorrelationData(correlationId);

        try {
            ConnectionFactory cf = rabbitTemplate.getConnectionFactory();
            if (cf instanceof CachingConnectionFactory ccf) {
                log.info("Publishing using connection vhost={}", ccf.getVirtualHost());
            } else {
                log.info("Publishing using connectionFactory={}", cf.getClass().getSimpleName());
            }
            // SimpleResourceHolder.bind(rabbitTemplate.getConnectionFactory(), resolvePublishTenant(null));

            // You may call this explicitly to an exchange with routing key, otherwise defaults to rabbitTemplate created in config
            //  rabbitTemplate.convertAndSend(tenantEventsExchange.getName(), "tenant.created", message, correlationData);
            rabbitTemplate.correlationConvertAndSend(message, correlationData);
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
