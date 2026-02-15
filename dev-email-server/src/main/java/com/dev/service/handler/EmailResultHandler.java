package com.dev.service.handler;

import com.dev.dto.email.EmailCategory;
import com.dev.dto.email.EmailSendEvent;
import com.dev.dto.email.EmailStatusEvent;

public interface EmailResultHandler {
    EmailCategory getCategory();

    void onSuccess(EmailStatusEvent event);

    void onFailure(EmailStatusEvent event);

    EmailStatusEvent buildSuccessEvent(EmailSendEvent event);

    EmailStatusEvent buildFailureEvent(EmailSendEvent event, String errorMessage);
}
