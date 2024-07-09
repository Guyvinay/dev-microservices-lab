package com.dev.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Profile {
    private String id;

    private String name;

    private String email;

    private Integer age;

}
