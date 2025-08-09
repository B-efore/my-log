package com.jiwon.mylog.domain.tag.repository.posttag;

import com.jiwon.mylog.domain.tag.entity.PostTag;
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
public class PostTagJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<PostTag> postTags) {
        jdbcTemplate.batchUpdate("insert into post_tag (post_id, tag_id) values (?, ?)",
                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        PostTag postTag = postTags.get(i);
                        ps.setLong(1, postTag.getPost().getId());
                        ps.setLong(2, postTag.getTag().getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return postTags.size();
                    }
                });
    }
}
