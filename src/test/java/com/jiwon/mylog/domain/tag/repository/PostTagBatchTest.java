package com.jiwon.mylog.domain.tag.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jiwon.mylog.config.EmbeddedRedisConfig;
import com.jiwon.mylog.domain.post.entity.Post;
import com.jiwon.mylog.domain.tag.entity.PostTag;
import com.jiwon.mylog.domain.tag.entity.Tag;
import com.jiwon.mylog.domain.tag.repository.posttag.PostTagJdbcRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

@Import(EmbeddedRedisConfig.class)
@ActiveProfiles("test")
@SpringBootTest
class PostTagBatchTest {

    @Autowired
    TransactionTemplate tx;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    PostTagJdbcRepository postTagJdbcRepository;

    private static final Long[] TAGS = {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L};

    @BeforeEach
    void clean() {
        jdbcTemplate.update("TRUNCATE TABLE post_tag");
    }

    private List<PostTag> buildPostTags(Long postId, int tagCount) {
        List<PostTag> list = new ArrayList<>(tagCount);
        for (int i = 0; i < tagCount; i++) {
            Post post = Post.builder().id(postId).build();
            Tag tag = Tag.builder().id(TAGS[i]).build();

            PostTag pt = new PostTag();
            pt.setPost(post);
            pt.setTag(tag);
            list.add(pt);
        }
        return list;
    }

    private long measureMillis(Runnable r) {
        long start = System.nanoTime();
        r.run();
        return (System.nanoTime() - start) / 1_000_000;
    }

    private void insertIndividually(List<PostTag> postTags) {
        final String sql = "INSERT INTO post_tag (post_id, tag_id) VALUES (?, ?)";
        for (PostTag pt : postTags) {
            jdbcTemplate.update(sql, pt.getPost().getId(), pt.getTag().getId());
        }
    }

    private void insertBatch(List<PostTag> postTags) {
        postTagJdbcRepository.saveAll(postTags);
    }

    @Test
    void compare_single_post_with_max_tags() {
        final int TAG_COUNT = 10;
        final Long POST_ID = 1L;

        List<PostTag> postTags = buildPostTags(POST_ID, TAG_COUNT);
        long ms1 = measureMillis(() -> tx.executeWithoutResult(s -> insertIndividually(postTags)));

        jdbcTemplate.update("DELETE FROM post_tag WHERE post_id = ?", POST_ID);

        List<PostTag> postTags2 = buildPostTags(POST_ID, TAG_COUNT);
        long ms2 = measureMillis(() -> tx.executeWithoutResult(s -> insertBatch(postTags2)));

        System.out.println("단일 포스트 10개 태그");
        System.out.printf("개별 INSERT: %d ms%n", ms1);
        System.out.printf("배치 INSERT: %d ms%n", ms2);

        assertTrue(ms1 > 0 && ms2 > 0);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5, 7, 10})
    void test_different_tag_counts(int tagCount) {
        List<PostTag> postTags = buildPostTags(1L, tagCount);

        long ms1 = measureMillis(() -> tx.executeWithoutResult(s -> insertIndividually(postTags)));
        jdbcTemplate.update("DELETE FROM post_tag WHERE post_id = 1");

        List<PostTag> postTags2 = buildPostTags(1L, tagCount);
        long ms2 = measureMillis(() -> tx.executeWithoutResult(s -> insertBatch(postTags2)));
        jdbcTemplate.update("DELETE FROM post_tag WHERE post_id = 1");

        System.out.printf("태그 %d개: 개별=%dms, 배치=%dms%n", tagCount, ms1, ms2);
    }
}