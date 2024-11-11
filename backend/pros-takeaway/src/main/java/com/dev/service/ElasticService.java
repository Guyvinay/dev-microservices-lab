package com.dev.service;

import com.dev.common.dto.document.Document;
import com.dev.dto.ProfilingDocumentDTO;
import com.dev.modal.Student;

import java.util.List;

public interface ElasticService {

    public void saveStudent(Student student);
    public Student findStudentById(String id);
    public void getAllStudents();
    public List<Document> getAllDocument(String index);
    public void indexRequest();
    default void ind() {
        System.out.println();
    }
    private void ined() {
        System.out.println();
    }
    public List<ProfilingDocumentDTO> indexBulkDocument();

    List<ProfilingDocumentDTO> getProfilingDocuments(int from, int page);

    List<ProfilingDocumentDTO> getAllProfilingDocuments(String tenantId, Long moduleId, Integer pageNumber, Integer pageSize);

    ProfilingDocumentDTO getProfilingDocumentById(String tenantId, Long moduleId, String fieldId);


}
