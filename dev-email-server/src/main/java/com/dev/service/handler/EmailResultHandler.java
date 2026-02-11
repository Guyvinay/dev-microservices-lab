package com.dev.service.handler;

import com.dev.dto.email.EmailCategory;
import com.dev.dto.email.EmailSendEvent;

public interface EmailResultHandler {
    EmailCategory getCategory();

    void onSuccess(EmailSendEvent event);

    void onFailure(EmailSendEvent event, String errorMessage);
}
