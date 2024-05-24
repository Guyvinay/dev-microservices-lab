package com.app.modal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "student")
public class Student {

				@Id
				@GeneratedValue(strategy = GenerationType.IDENTITY)
				private String id;
				private String name;
				private int age;
				private String gender;
				private Integer schoolId;

}
