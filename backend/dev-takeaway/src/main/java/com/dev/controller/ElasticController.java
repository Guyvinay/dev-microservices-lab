package com.dev.controller;

import com.dev.common.dto.GeneralResponseDTO;
import com.dev.common.dto.document.Document;
import com.dev.dto.ProfilingDocumentDTO;
import com.dev.dto.ProfilingDocumentResponse;
import com.dev.modal.Student;
import com.dev.modal.User;
import com.dev.service.ElasticService;
import com.dev.service.UserAuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Tag(name = "ElasticController", description = "REST controller to manage Elastic requests")
@RestController
@RequestMapping(value = "/elastic")
@CrossOrigin("*")
public class ElasticController {


    @Autowired
    private ElasticService elasticService;

    @PostMapping(value = "/student")
    public void saveStudent(@RequestBody Student student) {

        elasticService.saveStudent(student);
    }

    @GetMapping(value = "/student")
    public void getAllStudent() {

        elasticService.getAllStudents();
    }

    @GetMapping(value = "/student/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable("id") String id) throws IOException {
        return new ResponseEntity<>(elasticService.findStudentById(id), HttpStatus.OK);
    }

    @GetMapping(value = "/index")
    public void indexRequest() {
        elasticService.indexRequest();
    }

    @GetMapping(value = "/document/{index}")
    public ResponseEntity<List<Document>> getAllDocuments(@PathVariable("index") String index) {
        return new ResponseEntity<List<Document>>(elasticService.getAllDocument(index), HttpStatus.OK);
    }

    @PostMapping(value = "/document/bulk")
    public ResponseEntity<List<ProfilingDocumentDTO>> indexBulkDocument() {
        return new ResponseEntity<>(elasticService.indexBulkDocument(), HttpStatus.OK);
    }

    @GetMapping(value = "/documents/bulk")
    public ResponseEntity<List<ProfilingDocumentDTO>> getProfilingDocuments(int from, int page) {
        return new ResponseEntity<>(elasticService.getProfilingDocuments(from, page), HttpStatus.OK);
    }

    /**
     * Retrieves a paginated list of profiling documents for a specified module.
     *
     * <p>This method handles HTTP GET requests to retrieve profiling documents for a specified module, supporting pagination.
     * It returns  {@link ProfilingDocumentResponse} instances wrapped in a {@link GeneralResponseDTO}, which provides
     * additional metadata such as status or messages.
     *
     * @param moduleId   the unique identifier of the module as a {@link Long}.
     * @param pageNumber the page number for pagination. The method defaults to 0 (first page).
     * @param pageSize   the number of documents to return per page. The method defaults to 20 documents per page.
     * @return a {@link ResponseEntity} containing a {@link GeneralResponseDTO} with a list of {@link ProfilingDocumentDTO} instances,
     * or an error message if something goes wrong
     * <p>Example usage:
     * <pre>
     *          GET /profiling/api/v1.0?moduleId=123&pageNumber=0&pageSize=10
     * </pre>
     */
    @Operation(summary = "Retrieve profiling data from Elasticsearch by module ID",
            description = "Fetches technical profiling the data from Elasticsearch based on the provided module ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data retrieved successfully",
                    content = @Content(schema = @Schema(implementation = GeneralResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = GeneralResponseDTO.class)))
    })
    @GetMapping
    ResponseEntity<GeneralResponseDTO<ProfilingDocumentResponse>> getAllProfilingDocuments(
            @RequestParam(name = "tenantId",required = false, defaultValue = "346377") String tenantId,
            @RequestParam(name = "moduleId",required = false, defaultValue = "504349") Long moduleId,
            @RequestParam(name = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "20") Integer pageSize
            ) {
        return new ResponseEntity<>(GeneralResponseDTO.ok(
                elasticService.getAllProfilingDocuments(tenantId, moduleId, pageNumber, pageSize)),
                HttpStatus.OK
        );
    }

    /**
     * Retrieves a specific profiling document based on the provided module and field ID.
     * <p>This method handles HTTP GET requests to retrieve a single profiling document by its unique field identifier
     * * within a specified module.
     *
     * @param moduleId moduleId the unique identifier of the module as a {@link Long}.
     * @param fieldId  the unique identifier of the profiling document field as a {@link String}.
     * @return a {@link ResponseEntity} containing a {@link GeneralResponseDTO} with the {@link ProfilingDocumentDTO} instance
     * corresponding to the provided `fieldId` and `moduleId`.
     *
     * <p>Example usage:
     * <pre>
     *     GET /profiling/api/v1.0/field?moduleId=123&fieldId=profileId_456
     * </pre>
     */
    @Operation(summary = "Retrieve profiling data from Elasticsearch by module ID and field ID (recordNumber)",
            description = "Fetches technical profiling the data from Elasticsearch based on the provided module ID. and and field ID (recordNumber)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data retrieved successfully",
                    content = @Content(schema = @Schema(implementation = GeneralResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = GeneralResponseDTO.class)))
    })
    @GetMapping(value = "/field")
    ResponseEntity<GeneralResponseDTO<ProfilingDocumentDTO>> getProfilingDocumentById(
            @RequestParam(name = "tenantId",required = false, defaultValue = "346377") String tenantId,
            @RequestParam(name = "moduleId",required = false, defaultValue = "504349") Long moduleId,
            @RequestParam(name = "fieldId",required = false, defaultValue = "fld_963014021") String fieldId) {
        return new ResponseEntity<>(
                GeneralResponseDTO.ok(elasticService.getProfilingDocumentById(tenantId, moduleId, fieldId)),
                HttpStatus.OK
        );
    }

    @Autowired
    private UserAuditService userAuditService;

    @GetMapping(value = "/userAudit/{userId}")
    public List<User> getUserRevision(@PathVariable("userId") Long userId) {
        return userAuditService.printUserRevisionHistory(userId);
    }


}
