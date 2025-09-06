package com.dev.common.dto.document;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class Notifications {
    private boolean email;
    private boolean sms;
    private boolean push;
}
