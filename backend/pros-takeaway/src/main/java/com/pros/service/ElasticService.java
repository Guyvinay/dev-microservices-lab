package com.pros.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pros.modal.Student;

import java.io.IOException;

public interface ElasticService {

    public void saveStudent(Student student) throws IOException;
    public Student findStudentById(String id) throws IOException;

}
