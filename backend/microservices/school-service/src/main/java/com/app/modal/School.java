package com.app.modal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "school")
public class School {

				@Id
				@GeneratedValue(strategy = GenerationType.AUTO)
				private int id;
				private String schoolName;
				private String location;
				private String principalName;

}
