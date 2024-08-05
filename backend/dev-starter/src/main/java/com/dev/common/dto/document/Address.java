package com.dev.common.dto.document;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class Address {
    private String street;
    private String city;
    private String state;
    private int zip;
    private Location location;
}
