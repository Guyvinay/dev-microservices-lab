package com.dev.modal;

import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Profile {

//    @Id
    private String id;

    private String name;

    private String email;

    private Integer age;

}
