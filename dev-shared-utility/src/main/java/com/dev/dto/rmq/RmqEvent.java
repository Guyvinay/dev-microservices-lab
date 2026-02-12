package com.dev.dto.rmq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RmqEvent {
    private String exchange;
    private String routingKey;
    private Object payload;
}
