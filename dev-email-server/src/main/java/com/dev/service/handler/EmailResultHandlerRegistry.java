package com.dev.service.handler;

import com.dev.dto.email.EmailCategory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class EmailResultHandlerRegistry {

    private final Map<EmailCategory, EmailResultHandler> handlerMap;

    public EmailResultHandlerRegistry(List<EmailResultHandler> resultHandlers) {
        this.handlerMap = resultHandlers.stream()
                .collect(
                        Collectors.toMap(
                                EmailResultHandler::getCategory,
                                Function.identity()
                        )
                );
        log.info("EmailResultHandlerRegistry: {} ", this.handlerMap.keySet());

    }

    public EmailResultHandler getHandler(EmailCategory category) {
        if (handlerMap.containsKey(category)) return handlerMap.get(category);
        return handlerMap.get(EmailCategory.APPLICATION);
    }

}
