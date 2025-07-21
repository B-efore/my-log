package com.jiwon.mylog.domain.post.repository;

import com.jiwon.mylog.TestDataFactory;
import com.jiwon.mylog.domain.category.entity.Category;
import com.jiwon.mylog.domain.category.repository.CategoryRepository;
import com.jiwon.mylog.domain.post.entity.Post;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.domain.user.repository.UserRepository;
import com.jiwon.mylog.global.common.config.JpaAuditingConfiguration;
import com.jiwon.mylog.global.common.config.QueryDSLConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DataJpaTest
@Import({QueryDSLConfig.class, JpaAuditingConfiguration.class})
class PostRepositoryTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        postRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void updatePostCategory() {
        // given
        User user = TestDataFactory.createUser("email", "accountId", "name");
        Category category = TestDataFactory.createCategory(user, "name");
        Post post = TestDataFactory.createPost("title", "content", user, category);

        userRepository.save(user);
        Category savedCategory = categoryRepository.save(category);
        Post savedPost = postRepository.save(post);

        // when
        postRepository.updatePostCategory(savedCategory.getId());

        // then
        em.clear();
        Post updatedPost = postRepository.findById(savedPost.getId()).get();
        assertThat(updatedPost.getCategory()).isNull();
    }
}