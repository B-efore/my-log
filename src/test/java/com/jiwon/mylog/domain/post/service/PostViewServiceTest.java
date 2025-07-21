package com.jiwon.mylog.domain.post.service;

import com.jiwon.mylog.TestDataFactory;
import com.jiwon.mylog.config.EmbeddedRedisConfig;
import com.jiwon.mylog.domain.post.entity.Post;
import com.jiwon.mylog.domain.post.repository.PostRepository;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.domain.user.repository.UserRepository;
import com.jiwon.mylog.global.redis.RedisUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@Import(EmbeddedRedisConfig.class)
@ActiveProfiles("test")
@SpringBootTest
class PostViewServiceTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private PostViewService postViewService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisUtil redisUtil;

    private Long postId;

    @BeforeEach
    void setUp() {
        redisUtil.clearAll();
        userRepository.deleteAll();
        postRepository.deleteAll();

        User user = userRepository.save(TestDataFactory.createUser("email", "accountId", " name"));
        Post post = postRepository.save(TestDataFactory.createPost("title", "content", user, null));
        postId = post.getId();
    }

    @DisplayName("조회수 증가 테스트 (동시성)")
    @Test
    void incrementPostView() throws InterruptedException {
        int numberOfThreads = 1000;

        for (int i = 0; i < 10; i++) {
            executeConcurrentViewTest(numberOfThreads);
            int view = postViewService.getPostView(postId, 0);
            System.out.println(i+1 + "round = expected view: " + numberOfThreads + ", actual: " + view);
            assertThat(view)
                    .withFailMessage("Round %d failed: expected %d but was %d",
                            i + 1, numberOfThreads, view)
                    .isEqualTo(numberOfThreads);
            redisUtil.clearAll();
        }
    }

    private void executeConcurrentViewTest(int numberOfThreads) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch countDownLatch = new CountDownLatch(numberOfThreads);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            final int idx = i;
            executorService.submit(() -> {
                try {
                    cyclicBarrier.await();
                    String userKey = "user:" + idx;
                    postViewService.incrementPostView(postId, 0, userKey);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        executorService.shutdown();
    }

    @DisplayName("조회수 증가 테스트 (동시성 및 DB 반영)")
    @Test
    void incrementPostView_DB() throws InterruptedException {
        int first = 500;
        int second = 600;
        ExecutorService executorService = Executors.newFixedThreadPool(first + second);

        CountDownLatch latch1 = new CountDownLatch(first);
        for (int i = 0; i < first; i++) {
            final int idx = i;
            executorService.submit(() -> {
                try {
                    String userKey = "user:" + idx;
                    postViewService.incrementPostView(postId, 0, userKey);
                } finally {
                    latch1.countDown();
                }
            });
        }
        latch1.await();
        int firstSavedView = postViewService.getPostView(postId, 0);
        postRepository.updatePostView(postId, firstSavedView);
        em.flush();
        em.clear();

        int firstUpdatedView = postRepository.findById(postId).get().getViews();
        assertThat(firstUpdatedView).isEqualTo(first);

        CountDownLatch latch2 = new CountDownLatch(second);
        for (int i = first; i < first + second; i++) {
            final int idx = i;
            executorService.submit(() -> {
                try {
                    String userKey = "user:" + idx;
                    postViewService.incrementPostView(postId, 0, userKey);
                } finally {
                    latch2.countDown();
                }
            });
        }
        latch2.await();
        executorService.shutdown();
        int secondSavedView = postViewService.getPostView(postId, 0);
        postRepository.updatePostView(postId, secondSavedView);
        em.flush();
        em.clear();

        int redisView = postViewService.getPostView(postId, 0);
        int dbView = postRepository.findById(postId).get().getViews();

        assertThat(redisView).isEqualTo(dbView).isEqualTo(first + second);
    }
}