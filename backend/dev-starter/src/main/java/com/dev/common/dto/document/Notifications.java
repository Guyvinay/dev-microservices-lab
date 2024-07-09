package com.dev.common.dto.document;

import lombok.Data;

@Data
public class Notifications {
    private boolean email;
    private boolean sms;
    private boolean push;
}
