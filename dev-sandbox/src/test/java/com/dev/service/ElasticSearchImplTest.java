package com.dev.service;

import com.dev.exception.ProfilingNotFoundException;
import com.dev.service.impl.ElasticServiceImpl;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchResponseSections;
import org.elasticsearch.action.search.ShardSearchFailure;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ElasticSearchImplTest {

    @Mock
    private RestHighLevelClient restHighLevelClient;

    @InjectMocks
    private ElasticServiceImpl elasticService;


    /**
     * This test case verifies the retrieval of all technical profiling documents with count which is set to 2.
     */
//    @Test
    @DisplayName("Test case to get all Technical profiling data with total count equal 2")
    public void get_All_Profiling_Documents_Test() throws IOException {
        String tenantId = "tenant1";
        long moduleId = 504349L;
        int pageNumber = 0;
        int pageSize = 10;
        String indexName = "server_" + moduleId + "_do_profile_" + tenantId + "_en";
        SearchResponse searchResponse = buildSearchResponseWithMockHits();

        //ACT
        when(restHighLevelClient.count(any(CountRequest.class), eq(RequestOptions.DEFAULT))).thenReturn(new CountResponse(2, null, null));
        when(restHighLevelClient.search(any(SearchRequest.class), eq(RequestOptions.DEFAULT)))
                .thenReturn(searchResponse);

        //Action
        Long count = elasticService.getTotalDocumentCount(indexName);
        ProfilingDocumentResponse response = elasticService.getAllProfilingDocuments(tenantId, moduleId, pageNumber, pageSize);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(count, response.getTechnicalProfiling().size(), "Expected number of profiling documents should match");
        assertEquals(count, response.getTotalCount(), "Expected number of profiling documents should match");
        verify(restHighLevelClient, times(1)).search(any(SearchRequest.class), eq(RequestOptions.DEFAULT));

    }

    /**
     * This test case verifies the retrieval empty technical profiling document list with count which is set to 2.
     */
//    @Test
    @DisplayName("Test case to get empty profiling data with total count equal 2")
    public void get_All_Profiling_Documents_Empty_Test() throws IOException {
        String tenantId = "tenant1";
        long moduleId = 504349L;
        int pageNumber = 0;
        int pageSize = 10;
        String indexName = "server_" + moduleId + "_do_profile_" + tenantId + "_en";
        SearchResponse searchResponse = mock(SearchResponse.class);

        //ACT
        when(restHighLevelClient.count(any(CountRequest.class), eq(RequestOptions.DEFAULT))).thenReturn(new CountResponse(1, null, null));
        when(restHighLevelClient.search(any(SearchRequest.class), eq(RequestOptions.DEFAULT)))
                .thenReturn(searchResponse);

        //Action
        Long count = elasticService.getTotalDocumentCount(indexName);
        ProfilingDocumentResponse response = elasticService.getAllProfilingDocuments(tenantId, moduleId, pageNumber, pageSize);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(0, response.getTechnicalProfiling().size(), "Expected number of profiling documents should 0");
        assertEquals(count, response.getTotalCount(), "Expected number of profiling documents should match");
        verify(restHighLevelClient, times(1)).search(any(SearchRequest.class), eq(RequestOptions.DEFAULT));

    }

    /**
     * This test case verifies with retrieval of single technical profiling document with id: FLD_798793538.
     */
//    @Test
    @DisplayName("Test case to get one technical profiling document with id:: FLD_798793538")
    public void get_Profiling_Document_By_Id_Test() throws IOException {
        String tenantId = "tenant1";
        long moduleId = 504349L;
        String fieldId = "";

        SearchResponse searchResponse = buildSearchResponseWithMockHits();

        //ACT
        when(restHighLevelClient.search(any(SearchRequest.class), eq(RequestOptions.DEFAULT)))
                .thenReturn(searchResponse);

        //Action
        ProfilingDocumentDTO response = elasticService.getProfilingDocumentById(tenantId, moduleId, fieldId);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals("FLD_798793538", response.getColumn(), "Column id should be: FLD_798793538");
        verify(restHighLevelClient, times(1)).search(any(SearchRequest.class), eq(RequestOptions.DEFAULT));

    }

    /**
     * This test case verifies with getting single profiling document and
     * not being found results in exception throwing and matching the exception message.
     */
//    @Test
    @DisplayName("Test case for technical profiling not to be found and exception to thrown")
    public void get_Profiling_Document_By_Id_Not_Found_Test() throws IOException {
        String tenantId = "tenant1";
        long moduleId = 504349L;
        String fieldId = "FLD_798793538";

        SearchResponse searchResponse = mock(SearchResponse.class);

        //ACT
        when(restHighLevelClient.search(any(SearchRequest.class), eq(RequestOptions.DEFAULT)))
                .thenReturn(searchResponse);

        Exception exception = assertThrows(ProfilingNotFoundException.class, () -> {
            // Call the method that should throw the exception
            elasticService.getProfilingDocumentById(tenantId, moduleId, fieldId);
        });

        // Assert
        assertEquals("Technical profiling not found for field: FLD_798793538", exception.getMessage(), "Resource not found exception should be thrown");
        verify(restHighLevelClient, times(1)).search(any(SearchRequest.class), eq(RequestOptions.DEFAULT));

    }


    /**
     * Prepares mock SearchResponse.
     * @return {@link SearchResponse}
     */
    private SearchResponse buildSearchResponseWithMockHits() {
        SearchHit[] hits = buildMockSearchHits();
        TotalHits totalHits = new TotalHits(2, TotalHits.Relation.EQUAL_TO);
        SearchHits searchHits = new SearchHits(hits, totalHits, 1);

        SearchResponseSections internalResponse = new SearchResponseSections(searchHits, null, null,
                false, false, null, 0);
        return new SearchResponse(internalResponse, null, 1, 1,
                0, 100, new ShardSearchFailure[]{}, SearchResponse.Clusters.EMPTY);
    }

    /**
     * prepared the array of {@link SearchHit} used in building {@link SearchResponse}.
     * @return array of {@link SearchHit} with mock data.
     */
    private SearchHit[] buildMockSearchHits() {
        SearchHit hit1 = new SearchHit(1);
        SearchHit hit2 = new SearchHit(2);

        ProfilingDocumentDTO doc1 = new ProfilingDocumentDTO();
        doc1.setColumn("FLD_798793538");

        ProfilingDocumentDTO doc2 = new ProfilingDocumentDTO();
        doc2.setColumn("FLD_836671767");


        String jsonSource1 = "{\"column\":\"FLD_798793538\", \"timestamp\": 1731063040833, \"null_count\": 1234, \"value_frequency\": {}}";
        String jsonSource2 = "{\"column\":\"FLD_836671767\", \"timestamp\": 1732061040833, \"null_count\": 1230, \"value_frequency\": {}}";

        hit1.sourceRef(new BytesArray(jsonSource1));
        hit2.sourceRef(new BytesArray(jsonSource2));

        return new SearchHit[]{hit1, hit2};
    }


}
