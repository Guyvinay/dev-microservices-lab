package com.dev.common.dto.document;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class Settings {
    private String theme;
    private Notifications notifications;
    private Privacy privacy;
}
