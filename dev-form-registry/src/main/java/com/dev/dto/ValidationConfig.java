package com.dev.dto;

import lombok.Data;

@Data
public class ValidationConfig {

    private Boolean required;

    private Integer minLength;

    private Integer maxLength;

    private String regex;
}
