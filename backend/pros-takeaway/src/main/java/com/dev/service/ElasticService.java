package com.dev.service;

import com.dev.common.dto.document.Document;
import com.dev.modal.Student;

import java.util.List;

public interface ElasticService {

    public void saveStudent(Student student);
    public Student findStudentById(String id);
    public void getAllStudents();
    public List<Document> getAllDocument(String index);
    public void indexRequest();
}
