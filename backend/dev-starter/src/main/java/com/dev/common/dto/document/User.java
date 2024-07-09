package com.dev.common.dto.document;

import lombok.Data;

@Data
public class User {
    private String id;
    private String name;
    private String email;
    private Address address;
    private int age;
    private String date_of_birth;
    private String created_at;
}
