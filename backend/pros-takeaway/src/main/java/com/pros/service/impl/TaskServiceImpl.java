package com.pros.service.impl;

import com.pros.service.TaskService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;


    @Override
    public void searchTasks(String status, String assignee, String searchString, int from, int size, String searchAfter) {
        SearchSourceBuilder searchSourceBuilder  = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        if (status != null && !status.isEmpty()) {
            boolQueryBuilder.must(QueryBuilders.termQuery("task.status.text", status));
        }

//        if (assignee != null && !assignee.isEmpty()) {
//            boolQueryBuilder.must(QueryBuilders.termQuery("task.assignee.keyword", assignee));
//        }
//
//        if (searchString != null && !searchString.isEmpty()) {
//            QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryStringQuery("*" + searchString + "*");
//            queryStringQueryBuilder.field("description");
//            boolQueryBuilder.must(queryStringQueryBuilder);
//        }
//
        searchSourceBuilder.query(boolQueryBuilder);
//
//        searchSourceBuilder.from(from);
//        searchSourceBuilder.size(size);
//
//        if (searchAfter != null && !searchAfter.isEmpty()) {
//            searchSourceBuilder.searchAfter(new Object[]{searchAfter});
//        }
//
//        searchSourceBuilder.sort(SortBuilders.fieldSort("task.created_at").order(SortOrder.DESC));

        SearchRequest searchRequest = new SearchRequest("tasks");
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            System.out.println(searchResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
