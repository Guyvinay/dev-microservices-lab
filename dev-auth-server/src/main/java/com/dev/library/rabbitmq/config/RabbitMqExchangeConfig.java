package com.dev.library.rabbitmq.config;

import com.dev.library.rabbitmq.RabbitMqPublisherProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import java.util.List;

/**
 * RabbitMqExchangeConfig is responsible for declaring exchanges at application startup.
 * <p>
 * It reads the exchange configurations from {@link RabbitMqPublisherProperties} and ensures
 * the exchanges are declared using {@link RabbitAdmin}.
 * <p>
 * Supports exchange types:
 * <ul>
 *   <li>topic</li>
 *   <li>direct</li>
 * </ul>
 *
 * If an unsupported type is configured, an {@link IllegalArgumentException} is thrown.
 */
@Slf4j
@RequiredArgsConstructor
public class RabbitMqExchangeConfig {

    private final RabbitMqPublisherProperties rabbitMqProperties;

    private final RabbitAdmin rabbitAdmin;

    /**
     * Declares all configured exchanges during application startup.
     * Runs once after dependency injection is complete.
     */
    @PostConstruct
    public void declareExchanges() {
        List<RabbitMqPublisherProperties.ExchangeConfig> exchangeConfigs = rabbitMqProperties.getExchanges();

        if (exchangeConfigs == null || exchangeConfigs.isEmpty()) {
            log.info("No RabbitMQ exchanges configured to declare.");
            return;
        }

        log.info("Declaring {} RabbitMQ exchanges...", exchangeConfigs.size());

         exchangeConfigs.forEach((excConf)-> {
             Exchange exchange;
             if ("topic".equalsIgnoreCase(excConf.getType())) {
                 exchange = ExchangeBuilder.topicExchange(excConf.getName())
                         .durable(excConf.isDurable())
                         .build();
             } else if ("direct".equalsIgnoreCase(excConf.getType())) {
                 exchange = ExchangeBuilder.directExchange(excConf.getName())
                         .durable(excConf.isDurable())
                         .build();
             } else {
                 throw new IllegalArgumentException("Unsupported exchange type: " + excConf.getType());
             }
             rabbitAdmin.declareExchange(exchange);
             log.info("Declared {} exchange: '{}', durable={}",
                     excConf.getType(), excConf.getName(), excConf.isDurable());

         });
        log.info("All configured RabbitMQ exchanges declared successfully.");
    }
}
