package com.dev.library.jdbc;

import com.dev.library.jdbc.dto.QueryStatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/db-stats")
@RequiredArgsConstructor
public class DbStatsController {

    private final JdbcFetch jdbcFetch;

    @GetMapping("/slow-queries")
    public List<Map<String, Object>> getSlowQueries() {
        return jdbcFetch.getTopSlowQueries();
    }

    @GetMapping("/queries-stats")
    public List<QueryStatDto> getQueriesStats() {
        return jdbcFetch.findTopByTotalExecTime();
    }
}