package com.dev.rabbitmq.listener;

import com.dev.provider.JwtTokenProviderManager;
import com.dev.rabbitmq.annotation.TenantRabbitListener;
import com.dev.rabbitmq.configuration.RabbitMqConfiguration;
import com.dev.rabbitmq.configuration.RabbitMqManagement;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.SimpleResourceHolder;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
public class TenantRabbitListenerBinding {

    private final ApplicationContext applicationContext;
    private final RabbitAdmin rabbitAdmin;
    private final RabbitMqManagement rabbitMqManagement;
    private final JwtTokenProviderManager jwtTokenProviderManager;

    public final Map<String, Object> LISTENER_RMQ_BEANS = new ConcurrentHashMap<>();
    public final Map<String, AnnotatedBeanDefinition> ANNOTATED_BEAN_DEFINITIONS = new ConcurrentHashMap<>();
    public final Map<String, Map<String, SimpleMessageListenerContainer>> LISTENER_CONTAINER_MAP = new ConcurrentHashMap<>();

    // package to scan for annotated listeners - limit scan to keep startup fast
    @Value("${dev.rabbit.scan-package:com.dev}")
    private String packageToScan;

    @PostConstruct
    public void initMethod() {
        log.info("Starting initialization of TenantRabbitListeners...");
        Set<String> knownTenants = getInitialTenants();
        this.rabbitMqManagement.checkAndCreateVirtualHosts("public");
        knownTenants.forEach(rabbitMqManagement::checkAndCreateVirtualHosts);

        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(TenantRabbitListener.class);
        LISTENER_RMQ_BEANS.putAll(beans);
        log.info("Found {} listener beans with @TenantRabbitListener: {}", beans.size(), beans.keySet());

        ClassPathScanningCandidateComponentProvider componentProvider =
                new ClassPathScanningCandidateComponentProvider(false);
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(TenantRabbitListener.class);
        componentProvider.addIncludeFilter(annotationTypeFilter);

        Set<BeanDefinition> beanDefinitions = componentProvider.findCandidateComponents(packageToScan);
        for (BeanDefinition beanDefinition: beanDefinitions) {
            if (beanDefinition instanceof AnnotatedBeanDefinition annotatedBeanDefinition) {
                AnnotationMetadata annotationMetadata = annotatedBeanDefinition.getMetadata();
                String[] implementingInterfaces = annotationMetadata.getInterfaceNames();
                if(!Arrays.asList(implementingInterfaces).contains(MessageListener.class.getName())) {
                    log.info("Listener class not implementing MessageListener interface");
                    continue;
                }
                Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(TenantRabbitListener.class.getName());
                if (attributes == null) continue;
                String beanName = (String) attributes.get("value");
                ANNOTATED_BEAN_DEFINITIONS.put(beanName, annotatedBeanDefinition);
                log.info("Discovered annotated listener definition: {}", beanName);
            }
        }


        log.info("Onboarding {} tenants with listeners: {}", knownTenants.size(), knownTenants);
        knownTenants.forEach(this::onBoardTenant);
        this.onBoardTenant("public");

        log.info("TenantRabbitListener initialization completed successfully");
    }

    private void onBoardTenant(String tenantId) {
        ANNOTATED_BEAN_DEFINITIONS.forEach((beanName, beanDefinition)-> {
            log.info("Onboarding tenant: {}, beanName: {}", tenantId, beanName);
            registerListenerForTenant(beanName, beanDefinition, tenantId);
        });
    }

    private void registerListenerForTenant(String beanName, AnnotatedBeanDefinition beanDefinition, String tenantId) {
        LISTENER_CONTAINER_MAP.computeIfAbsent(tenantId, value-> new ConcurrentHashMap<>());

        if(LISTENER_CONTAINER_MAP.get(tenantId).containsKey(beanName)) {
            log.debug("Listener {} already registered for tenant {}", beanName, tenantId);
            return;
        }

        // Get the actual listener bean instance
        Object listenerBean = LISTENER_RMQ_BEANS.get(beanName);
        if (listenerBean == null) {
            log.warn("Listener bean instance not found for name {}", beanName);
            return;
        }

        Map<String, Object> attributes = beanDefinition.getMetadata().getAnnotationAttributes(TenantRabbitListener.class.getName());
        String queueName = (String) attributes.get("queue");
        String exchangeName = (String) attributes.getOrDefault("exchange", "");
        String routingKey = (String) attributes.getOrDefault("routingKey", "");
        String type = (String) attributes.getOrDefault("type", "direct");
        int maxConsumers = (Integer) attributes.getOrDefault("maxConcurrentConsumers", 10);
        int prefetch = (Integer) attributes.getOrDefault("prefetch", 1);
        boolean quorum = (Boolean) attributes.getOrDefault("quorum", true);
        log.info(
                "Registering listener [beanName={}, queue={}, exchangeName={}, routingKey={}, type={}, maxConsumers={}, prefetch={}, quorum={}] for tenant {}",
                beanName, queueName, exchangeName, routingKey, type, maxConsumers, prefetch, quorum, tenantId
        );

        Queue queue = quorum ?
                QueueBuilder.durable(queueName).quorum().build() :
                QueueBuilder.durable(queueName).build();

        try {
            SimpleResourceHolder.bind(rabbitAdmin.getRabbitTemplate().getConnectionFactory(), tenantId);

            rabbitAdmin.declareQueue(queue);
            if(StringUtils.isNotBlank(exchangeName)) {
                Exchange exchange = switch (type) {
                    case "topic" -> ExchangeBuilder.topicExchange(exchangeName).durable(true).build();
                    case "fanout" -> ExchangeBuilder.fanoutExchange(exchangeName).durable(true).build();
                    default -> ExchangeBuilder.directExchange(exchangeName).durable(true).build();
                };
                rabbitAdmin.declareExchange(exchange);
                log.info("Declared exchange={} [type={}] for tenant={}", exchangeName, type, tenantId);

                rabbitMqManagement.checkExistingBindingForQueueAndDelete(tenantId, exchangeName, queueName, routingKey);

                if("fanout".equalsIgnoreCase(type)) {
                    rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to((FanoutExchange)exchange));
                    log.info("Bound queue={} to fanout exchange={} for tenant={}", queueName, exchangeName, tenantId);

                } else {
                    rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(routingKey).noargs());
                    log.info("Bound queue={} to exchange={} with routingKey={} for tenant={}",
                            queueName, exchangeName, routingKey, tenantId);
                }
            } else {
                // Default binding to the default exchange
                rabbitAdmin.declareBinding(new Binding(queueName, Binding.DestinationType.QUEUE, "", queueName, Collections.emptyMap()));
                log.info("Declared default binding for queue={} to default exchange for tenant={}", queueName, tenantId);
            }
        } catch (Exception e) {
            log.error("Failed to declare queue/exchange/binding for tenant={} and bean={}", tenantId, beanName, e);
            throw e;
        } finally {
            SimpleResourceHolder.unbind(rabbitAdmin.getRabbitTemplate().getConnectionFactory());
        }

        final GenericApplicationContext genericApplicationContext = (GenericApplicationContext) applicationContext;
        String containerName = beanName + "_" + tenantId;
        genericApplicationContext.registerBean(containerName, SimpleMessageListenerContainer.class, ()-> {

            ConnectionFactory connectionFactory = RabbitMqConfiguration.TENANT_CONNECTION_MAP.get(tenantId);
            SimpleMessageListenerContainer messageListenerContainer = new SimpleMessageListenerContainer(connectionFactory);
            messageListenerContainer.setQueueNames(queueName);
            messageListenerContainer.setMessageListener((MessageListener) (message)-> {
                message.getMessageProperties().setUserId(tenantId);
                ((MessageListener) listenerBean).onMessage(message);
            });
            messageListenerContainer.setMessageListener(new CustomMessageListener((MessageListener) listenerBean, jwtTokenProviderManager));
            messageListenerContainer.setMessageAckListener(new CustomMessageAckListener());
            messageListenerContainer.setConcurrentConsumers(1);
            messageListenerContainer.setMaxConcurrentConsumers(maxConsumers);
            messageListenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);
            messageListenerContainer.setMissingQueuesFatal(false);
            messageListenerContainer.setShutdownTimeout(20000L);
            messageListenerContainer.setPrefetchCount(prefetch);
            messageListenerContainer.setRecoveryInterval(50000L);

            messageListenerContainer.start();
            log.info("Started listener container={} for tenant={} [queue={}, maxConsumers={}, prefetch={}]",
                    containerName, tenantId, queueName, maxConsumers, prefetch);

            return messageListenerContainer;
        });
        LISTENER_CONTAINER_MAP.get(tenantId).put(beanName, (SimpleMessageListenerContainer) genericApplicationContext.getBean(containerName));
        log.info("Completed listener registration for tenant={} and bean={}", tenantId, beanName);
    }


    private Set<String> getInitialTenants() {
        log.info("Fetching initial tenant IDs from RabbitMqConfiguration");
        return RabbitMqConfiguration.TENANT_IDS;
    }

}
