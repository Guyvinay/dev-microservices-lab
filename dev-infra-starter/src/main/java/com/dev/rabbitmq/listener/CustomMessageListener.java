package com.dev.rabbitmq.listener;

import com.dev.provider.JwtTokenProviderManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.text.ParseException;

@Slf4j
@RequiredArgsConstructor
public class CustomMessageListener implements MessageListener {

    private final MessageListener listenerBean;
    private final JwtTokenProviderManager jwtTokenProviderManager;

    @Override
    public void onMessage(Message message) {

        String authorization = (String) message.getMessageProperties().getHeader("Authorization");
        String messageId = message.getMessageProperties().getMessageId();

        if(StringUtils.isBlank(authorization)) {
            log.error("No Authorization header found for messageId={}", messageId);
            return;
        }

        String token = authorization.startsWith("Bearer ") ? authorization.substring(7) : authorization;

        try {
            Authentication authentication = jwtTokenProviderManager.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            listenerBean.onMessage(message);
            log.info("Message processed successfully. messageId={}", messageId);
        } catch (JsonProcessingException e) {
            log.error("Error while processing messageId={} : {}", messageId, e.getMessage(), e);
            throw new RuntimeException("Message processing failed", e);
        } catch (ParseException | JOSEException e) {
            log.error("Error while processing authenticating", e);
            throw new RuntimeException(e);
        } finally {
            SecurityContextHolder.clearContext();
            log.debug("SecurityContext cleared after messageId={}", messageId);
        }
    }
}
