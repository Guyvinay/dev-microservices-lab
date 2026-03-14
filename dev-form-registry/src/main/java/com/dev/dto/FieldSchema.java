package com.dev.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldSchema {

    private String name;
    private String type;

    private boolean nullable;
    private boolean unique;
    private boolean indexed;

}
