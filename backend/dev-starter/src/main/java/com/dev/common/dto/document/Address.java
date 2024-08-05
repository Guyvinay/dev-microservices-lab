package com.dev.common.dto.document;

import lombok.Data;

@Data
public class Address {
    private String street;
    private String city;
    private String state;
    private int zip;
    private Location location;
}
