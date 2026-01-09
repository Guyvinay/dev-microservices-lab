package com.dev.library.rabbitmq.publisher;

import com.dev.library.logging.MDCKeys;
import com.dev.security.details.UserBaseInfo;
import com.dev.security.dto.ServiceJwtToken;
import com.dev.security.provider.JwtTokenProviderManager;
import com.dev.utility.AuthContextUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RmqMessagePropertiesFactory {

    private final JwtTokenProviderManager jwtTokenProviderManager;

    public MessageProperties create(String correlationId)
            throws JOSEException, JsonProcessingException {

        MessageProperties props = new MessageProperties();
        props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        props.setMessageId(correlationId);

        UserBaseInfo userBaseInfo = AuthContextUtil.getUserBaseInfo();

        ServiceJwtToken serviceJwtToken = jwtTokenProviderManager.createServiceJwtToken(userBaseInfo, 200);

        String token = jwtTokenProviderManager.createJwtToken(serviceJwtToken);

        putIfPresent(props, MDCKeys.HEADER_TRACE_ID, MDC.get(MDCKeys.TRACE_ID));
        putIfPresent(props, MDCKeys.HEADER_TENANT_ID, MDC.get(MDCKeys.TENANT_ID));
        putIfPresent(props, MDCKeys.HEADER_USER_ID, MDC.get(MDCKeys.USER_ID));

        props.setHeader("Authorization", token);

        return props;
    }

    private void putIfPresent(MessageProperties props, String key, String value) {
        if (StringUtils.isNotBlank(value)) {
            props.setHeader(key, value);
        }
    }
}
