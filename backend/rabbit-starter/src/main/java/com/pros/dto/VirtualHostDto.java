package com.pros.dto;

import lombok.Data;

@Data
public class VirtualHostDto {

    private String name;
    private String description;
    private String tags;
    private String defaultqueuetype;

}
