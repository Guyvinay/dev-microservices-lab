package com.dev.common.dto.document;

import lombok.Data;

@Data
public class Settings {
    private String theme;
    private Notifications notifications;
    private Privacy privacy;
}
