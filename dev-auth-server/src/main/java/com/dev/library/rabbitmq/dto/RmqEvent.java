package com.dev.library.rabbitmq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RmqEvent {
    private String exchange;
    private String routingKey;
    private Object payload;
}
