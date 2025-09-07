package com.dev.rmq.binding;

import com.dev.rmq.annotation.RabbitListener;
import com.dev.rmq.configuration.RabbitConfig;
import com.dev.rmq.wrapper.RabbitListenerWrapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.SimpleResourceHolder;
import org.springframework.amqp.rabbit.connection.SimpleRoutingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RabbitQueueListenerBinding {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private RabbitProperties rabbitProperties;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final Map<String, Object> RMQ_BEANS = new ConcurrentHashMap<>();
    private final Map<String, AnnotatedBeanDefinition> A_BDF = new ConcurrentHashMap<>();
    private final Map<String, Map<String, SimpleMessageListenerContainer>> LISTENERS = new ConcurrentHashMap<>();

    private static final String PACKAGE_TO_SCAN = "com.dev";

    @PostConstruct
    public void findListenerBeans() {
        if (connectionFactory instanceof SimpleRoutingConnectionFactory) {
            log.info("RabbitMq connection factory initialized");
        }

        RMQ_BEANS.putAll(applicationContext.getBeansWithAnnotation(RabbitListener.class));
        log.info("Queue listener beans found : {}, {}", RMQ_BEANS.size(), RMQ_BEANS);

        log.info("scanning package {} for Queue Listeners", PACKAGE_TO_SCAN);
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(RabbitListener.class));
        Set<BeanDefinition> beanDefinitions = provider.findCandidateComponents(PACKAGE_TO_SCAN);

        for (BeanDefinition bD : beanDefinitions) {
            log.info("BeanDefinition: {}",bD);
            if (bD instanceof AnnotatedBeanDefinition) {
                AnnotatedBeanDefinition aBD = (AnnotatedBeanDefinition) bD;
                String[] implementingInterfaces = aBD.getMetadata().getInterfaceNames();
                Map<String, Object> annotationAttributes = aBD.getMetadata().getAnnotationAttributes(RabbitListener.class.getCanonicalName());
                String beanName = annotationAttributes.get("value").toString();

                if (!Arrays.asList(implementingInterfaces).contains(MessageListener.class.getName())) {
                    log.info("Listener should class should implement MessageListener interface");
                }
                A_BDF.put(beanName, aBD);
            }
        }
        RabbitConfig.TENANT_IDS.forEach(this::onBoardTenant);
    }

    private void onBoardTenant(String tenantId) {
        A_BDF.forEach((beanName, beanDefinition) -> {
            bindBeanWithTenant(beanName, beanDefinition, tenantId);
        });
    }

    private void bindBeanWithTenant(String beanName, AnnotatedBeanDefinition beanDefinition, String tenantId) {
        log.info("Binding start for bean {}", beanName);
        MessageListener messageListener = (MessageListener) RMQ_BEANS.get(beanName);

        if (messageListener == null) {
            log.warn("Listener for bean: {} not found", beanName);
            return;
        }

        LISTENERS.computeIfAbsent(tenantId, k -> new ConcurrentHashMap<>());

        Map<String, Object> attrs = beanDefinition.getMetadata()
                .getAnnotationAttributes(RabbitListener.class.getCanonicalName());

        String queueName   = (String) attrs.get("queue");
        String exchange    = (String) attrs.getOrDefault("exchange", "");
        String routingKey  = (String) attrs.getOrDefault("routingKey", "");
        String type        = (String) attrs.getOrDefault("type", "direct");

        log.info("Declaring queue={}, exchange={}, routingKey={}, type={} for tenant={}",
                queueName, exchange, routingKey, type, tenantId);

        Queue queue = QueueBuilder.durable(queueName).quorum().build();

        try {
            SimpleResourceHolder.bind(rabbitAdmin.getRabbitTemplate().getConnectionFactory(), tenantId);

            rabbitAdmin.declareQueue(queue);

            if (exchange != null && !exchange.isBlank()) {
                Exchange exch;
                switch (type.toLowerCase()) {
                    case "topic":
                        exch = ExchangeBuilder.topicExchange(exchange).durable(true).build();
                        break;
                    case "fanout":
                        exch = ExchangeBuilder.fanoutExchange(exchange).durable(true).build();
                        break;
                    case "direct":
                    default:
                        exch = ExchangeBuilder.directExchange(exchange).durable(true).build();
                        break;
                }
                rabbitAdmin.declareExchange(exch);

                if ("fanout".equalsIgnoreCase(type)) {
                    rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to((FanoutExchange) exch));
                } else {
                    rabbitAdmin.declareBinding(
                            BindingBuilder.bind(queue).to((Exchange) exch).with(routingKey).noargs()
                    );
                }
            } else {
                // fallback: bind to default direct exchange
                rabbitAdmin.declareBinding(new Binding(
                        queueName,
                        Binding.DestinationType.QUEUE,
                        "",   // default exchange
                        queueName,
                        null
                ));
            }
        } finally {
            SimpleResourceHolder.unbind(rabbitAdmin.getRabbitTemplate().getConnectionFactory());
        }
        log.info("Queue/exchange declaration finished for {}", queueName);

        final GenericApplicationContext genericApplicationContext = (GenericApplicationContext) applicationContext;

        String wrappedBeanName = beanName + "_" + tenantId;
        log.info("Starting consumer {} on tenant {}", wrappedBeanName, tenantId);

        genericApplicationContext.registerBean(
                wrappedBeanName,
                SimpleMessageListenerContainer.class,
                () -> {
                    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(
                            (ConnectionFactory) RabbitConfig.TENANT_CONNECTION_MAP.get(tenantId));
                    container.setLookupKeyQualifier(tenantId);
                    container.setMaxConcurrentConsumers(10);
                    container.setStartConsumerMinInterval(5000L);
                    container.setStopConsumerMinInterval(20000L);
                    container.setShutdownTimeout(20000L);
                    container.setAcknowledgeMode(AcknowledgeMode.AUTO);
                    container.setMessageListener(new RabbitListenerWrapper(tenantId, messageListener, this.applicationContext));
                    container.setQueueNames(queueName);
                    container.start();
                    return container;
                });

        LISTENERS.get(tenantId)
                .put(beanName, (SimpleMessageListenerContainer) genericApplicationContext.getBean(wrappedBeanName));

        log.info("Listeners {}", LISTENERS);
    }

}
