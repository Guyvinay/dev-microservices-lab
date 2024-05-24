package com.app.controller;

import com.app.modal.School;
import com.app.service.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping(value = "/school")
@RestController
public class SchoolController {
				@Autowired
				private SchoolService schoolService;

				@PostMapping
				public School addSchool(@RequestBody School school){
								return schoolService.addSchool(school);
				}
				@GetMapping
				public List<School> fetchSchools(){
								return  schoolService.fetchSchools();
				}
				@GetMapping("/{id}")
				public School fetchSchoolById(@PathVariable int id){
								return schoolService.fetchSchoolById(id);
				}
}
