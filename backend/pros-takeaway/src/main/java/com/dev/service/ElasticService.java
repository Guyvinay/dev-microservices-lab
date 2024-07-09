package com.dev.service;

import com.dev.modal.Student;

public interface ElasticService {

    public void saveStudent(Student student);
    public Student findStudentById(String id);
    public void getAllStudents();
    public void indexRequest();
}
