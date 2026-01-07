package com.dev.library.jdbc;

import com.dev.library.jdbc.dto.QueryStatDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component
public class JdbcFetch {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String BASE_SELECT = """
        SELECT queryid, query, calls, total_exec_time, mean_exec_time, rows,
               shared_blks_read, shared_blks_hit, blk_read_time, blk_write_time, wal_bytes
        FROM pg_stat_statements
        """;

    public List<Map<String, Object>> getTopSlowQueries() {
        return jdbcTemplate.queryForList(BASE_SELECT);
    }

    public List<QueryStatDto> findTopByTotalExecTime() {
        String sql = BASE_SELECT + " ORDER BY calls";
        return jdbcTemplate.query(sql, this::mapRow);
    }
    private QueryStatDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new QueryStatDto(
                rs.getObject("queryid", Long.class),
                rs.getString("query"),
                rs.getObject("calls", Long.class),
                rs.getObject("total_exec_time", Double.class),
                rs.getObject("mean_exec_time", Double.class),
                rs.getObject("rows", Long.class),
                rs.getObject("shared_blks_read", Long.class),
                rs.getObject("shared_blks_hit", Long.class),
                rs.getObject("blk_read_time", Double.class),
                rs.getObject("blk_write_time", Double.class)
        );
    }

}
