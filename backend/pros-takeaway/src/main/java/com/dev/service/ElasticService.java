package com.dev.service;

import com.dev.common.dto.document.Document;
import com.dev.dto.ProfilingDocumentDTO;
import com.dev.dto.ProfilingDocumentResponse;
import com.dev.exception.ProfilingNotFoundException;
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

    /**
     * Retrieves a paginated list of profiling documents for a specified tenant and module.
     *
     * <p>This method fetches profiling documents from the Elasticsearch, filtered by the specified tenant ID
     * and module ID and return the documents along with count of total documents in index. It allows the caller to specify a page number and page size.
     *
     * @param tenantId   the unique identifier for the tenant as a {@link String}.
     * @param moduleId   the unique identifier for the module as a {@link Long}.
     * @param pageNumber the page number for pagination.
     * @param pageSize   the number of documents to return per page
     * @return a {@link List} of {@link ProfilingDocumentResponse} instances, representing the profiling documents that match
     * the specified tenant and module within the requested page range. If no documents are found, an empty list is returned.
     */
    ProfilingDocumentResponse getAllProfilingDocuments(String tenantId, Long moduleId, Integer pageNumber, Integer pageSize);

    /**
     * Retrieves a profiling document by its unique identifier within the specified tenant and module
     *
     * <p>This method constructs and executes a search query to retrieve a profiling document based on the specified
     * tenant ID, module ID, and field ID.
     *
     * @param tenantId the unique identifier of the tenant as a {@link String}.
     * @param moduleId moduleId the unique identifier of the module as a {@link Long}.
     * @param fieldId  id of the document that is to be retrieved.
     * @return a {@link ProfilingDocumentDTO} instance representing the profiling document if found; otherwise throws an exception {@link ProfilingNotFoundException} if the document is not found.
     */
    ProfilingDocumentDTO getProfilingDocumentById(String tenantId, Long moduleId, String fieldId);


}
