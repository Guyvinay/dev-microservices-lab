package com.dev.modal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Student {

    private String studentId;

    @NotBlank(message = "Student Name Cannot be blank!!!")
    @Size(min = 2, max = 50, message = "Name must contain minimum 2 or maximum 50 characters")
    private String studentName;

    @NotBlank(message = "Student Email Cannot be blank!!!")
    @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Email format is invalid, should be in john@email.com")
    @Size(min = 10, max = 200, message = "Email must contain minimum 10 or maximum 200 characters")
    private  String email;

    @NotBlank(message = "Profile picture URL cannot blank!!!")
    @URL(message = "Profile picture URL must be valid")
    private String profilePic;

    @NotEmpty(message = "At least one tech should be included!!!")
    private List<Tech> techStacks;
}
