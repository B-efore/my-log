package com.jiwon.mylog.domain.tag.repository.tag;

import com.jiwon.mylog.domain.tag.entity.Tag;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Repository
public class TagJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void upsert(List<Tag> tags) {
        jdbcTemplate.batchUpdate("insert into tag (name, usage_count, user_id) values (?, ?, ?) on duplicate key update id = LAST_INSERT_ID(id)",
                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Tag tag = tags.get(i);
                        ps.setString(1, tag.getName());
                        ps.setLong(2, tag.getUsageCount());
                        ps.setLong(3, tag.getUser().getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return tags.size();
                    }
                });
    }
}
