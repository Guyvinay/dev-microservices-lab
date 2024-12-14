package com.dev.modal;

import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
//import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@Document(indexName = "student")
//@ToString
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE) // Enable second-level cache
public class Profile {

//    @Id
    private String id;

    private String name;

    private String email;

    private Integer age;

}
