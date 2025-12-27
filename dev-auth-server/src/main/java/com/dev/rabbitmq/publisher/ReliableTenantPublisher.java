package com.dev.rabbitmq.publisher;

import com.dev.security.dto.ServiceJwtToken;
import com.dev.security.dto.TokenType;
import com.dev.security.provider.JwtTokenProviderManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReliableTenantPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final JwtTokenProviderManager jwtTokenProviderManager;

    public void publishTenantCreated(String tenantId) {
        String correlationId = UUID.randomUUID().toString();

        try {
            MessageProperties props = getMessageProperties(correlationId);

            Message message = rabbitTemplate.getMessageConverter().toMessage(tenantId, props);
            CorrelationData correlationData = new CorrelationData(correlationId);

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

    private MessageProperties getMessageProperties(String correlationId) throws JOSEException, JsonProcessingException {
        MessageProperties props = new MessageProperties();
        props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        props.setMessageId(correlationId);

        ServiceJwtToken payload = ServiceJwtToken.builder()
                .jwtId(UUID.randomUUID())
                .tokenType(TokenType.SERVICE)
                .serviceName("tenant-service")
                .scopes(List.of("tenant.create"))
                .createdAt(System.currentTimeMillis())
                .expiresAt(System.currentTimeMillis() + Duration.ofMinutes(3).toMillis())
                .build();

        String token = jwtTokenProviderManager.createJwtToken(payload);


        props.setHeader("Authorization", token);
        return props;
    }

    private String resolvePublishTenant(String tenantId) {
        // If you publish to the public vhost default, return "public" (or null logic depending on your CF)
        return StringUtils.isNotBlank(tenantId) ? tenantId : "public";
    }
}
