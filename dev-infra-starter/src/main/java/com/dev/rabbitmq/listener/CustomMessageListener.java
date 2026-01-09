package com.dev.rabbitmq.listener;

import com.dev.logging.MDCLoggingUtility;
import com.dev.provider.JwtTokenProviderManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.text.ParseException;

@Slf4j
public class CustomMessageListener implements MessageListener {

    private final MessageListener listenerBean;
    private final JwtTokenProviderManager jwtTokenProviderManager;
    private final String tenantId;

    public CustomMessageListener(MessageListener listenerBean, JwtTokenProviderManager jwtTokenProviderManager, String tenantId) {
        this.listenerBean = listenerBean;
        this.jwtTokenProviderManager = jwtTokenProviderManager;
        this.tenantId = tenantId;
    }

    @Override
    public void onMessage(Message message) {

        String messageId = message.getMessageProperties().getMessageId();
        MDCLoggingUtility.populateFromRmqHeaders(message.getMessageProperties());
        try {
            Authentication authentication = jwtTokenProviderManager.getAuthenticatedServiceToken(tenantId);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            listenerBean.onMessage(message);
            log.info("Message processed successfully. messageId={}", messageId);
        } catch (JsonProcessingException e) {
            log.error("Error while processing messageId={} : {}", messageId, e.getMessage(), e);
            throw new RuntimeException("Message processing failed", e);
        } catch (ParseException | JOSEException e) {
            log.error("Error while processing authenticating", e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error("Error while processing authenticating", e);
        } finally {
            SecurityContextHolder.clearContext();
            log.debug("SecurityContext cleared after messageId={}", messageId);
        }
    }
}
