package com.dev.common.dto.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class User {
    private String id;
    private String name;
    private String email;
    private Address address;
    private int age;
    private String dateOfBirth;
    private String createdAt;
}
