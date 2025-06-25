package com.jiwon.mylog.domain.category.repository;

import com.jiwon.mylog.TestDataFactory;
import com.jiwon.mylog.domain.category.dto.response.CategoryCountResponse;
import com.jiwon.mylog.domain.category.entity.Category;
import com.jiwon.mylog.domain.post.entity.Post;
import com.jiwon.mylog.domain.post.repository.PostRepository;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.domain.user.repository.UserRepository;
import com.jiwon.mylog.global.common.config.JpaAuditingConfiguration;
import com.jiwon.mylog.global.common.config.QueryDSLConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({QueryDSLConfig.class, JpaAuditingConfiguration.class})
class CategoryRepositoryTest {

    private static final List<String> DEFAULT_CATEGORY_NAMES = List.of("category1", "category2", "category3");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("사용자의 카테고리(단일)를 조회한다.")
    void findByUserIdAndId() {
        // given
        User user = TestDataFactory.createUser("test@test.com", "testId", "test");

        User savedUser = userRepository.save(user);
        List<Category> savedCategories = saveCategories(user, DEFAULT_CATEGORY_NAMES);

        // when
        List<Category> findCategories = savedCategories.stream()
                .map(category -> categoryRepository.findByUserIdAndId(savedUser.getId(), category.getId()))
                .flatMap(Optional::stream)
                .toList();

        // then
        assertThat(findCategories).hasSize(3);
        assertThat(findCategories).extracting(Category::getName)
                .containsExactlyElementsOf(DEFAULT_CATEGORY_NAMES);
    }

    @Test
    @DisplayName("사용자의 카테고리(전체)를 조회한다.")
    void findAllByUserId() {
        // given
        User user = TestDataFactory.createUser("test@test.com", "testId", "test");

        User savedUser = userRepository.save(user);
        saveCategories(user, DEFAULT_CATEGORY_NAMES);

        // when
        List<Category> findCategories = categoryRepository.findAllByUserId(savedUser.getId());

        // then
        assertThat(findCategories).hasSize(3);
        assertThat(findCategories).extracting(Category::getName)
                .containsExactlyElementsOf(DEFAULT_CATEGORY_NAMES);
    }

    @Test
    @DisplayName("사용자의 전체 카테고리를 게시글 수와 함께 조회한다.")
    void findAllWithCountByUserId() {
        // given
        User user = TestDataFactory.createUser("test@test.com", "testId", "test");

        User savedUser = userRepository.save(user);
        List<Category> savedCategories = saveCategories(user, DEFAULT_CATEGORY_NAMES);

        Post post1 = TestDataFactory.createPost("title1", "content", user, savedCategories.get(0));
        Post post2 = TestDataFactory.createPost("title2", "content", user, savedCategories.get(0));
        Post post3 = TestDataFactory.createPost("title3", "content", user, savedCategories.get(1));

        postRepository.saveAll(List.of(post1, post2, post3));

        // when
        List<CategoryCountResponse> result = categoryRepository.findAllWithCountByUserId(savedUser.getId());

        // then
        assertThat(result).hasSize(3);
        assertThat(result)
                .extracting(CategoryCountResponse::getName, CategoryCountResponse::getPostCount)
                .containsExactly(
                        tuple("category1", 2L),
                        tuple("category2", 1L),
                        tuple("category3", 0L)
                );
    }

    @Test
    @DisplayName("카테고리가 없는 게시글의 경우 미분류로 그룹화해 조회한다.")
    void findAllWithCountByUserId_WithUncategorizedPosts() {
        // given
        User user = TestDataFactory.createUser("test@test.com", "testId", "test");

        User savedUser = userRepository.save(user);
        List<Category> savedCategories = saveCategories(user, DEFAULT_CATEGORY_NAMES);

        Post post1 = TestDataFactory.createPost("title1", "content", user, savedCategories.get(0));
        Post post2 = TestDataFactory.createPost("title2", "content", user, savedCategories.get(0));
        Post post3 = TestDataFactory.createPost("title3", "content", user, savedCategories.get(1));
        Post post4 = TestDataFactory.createPost("title4", "content", user, null);
        Post post5 = TestDataFactory.createPost("title5", "content", user, null);

        postRepository.saveAll(List.of(post1, post2, post3, post4, post5));

        // when
        List<CategoryCountResponse> result = categoryRepository.findAllWithCountByUserId(savedUser.getId());

        // then
        assertThat(result).hasSize(4);
        assertThat(result)
                .extracting(CategoryCountResponse::getName, CategoryCountResponse::getPostCount)
                .containsExactly(
                        tuple("category1", 2L),
                        tuple("category2", 1L),
                        tuple("category3", 0L),
                        tuple("미분류", 2L)
                );
    }

    @Test
    @DisplayName("삭제된 게시글은 카운트에서 제외하고 조회한다.")
    void findAllWithCountByUserId_ExcludeDeletedPosts() {
        // given
        User user = TestDataFactory.createUser("test@test.com", "testId", "test");

        User savedUser = userRepository.save(user);
        List<Category> savedCategories = saveCategories(user, DEFAULT_CATEGORY_NAMES);

        Post post1 = TestDataFactory.createPost("title1", "content", user, savedCategories.get(0));
        Post post2 = TestDataFactory.createPost("title2", "content", user, savedCategories.get(0));
        Post post3 = TestDataFactory.createPost("title3", "content", user, savedCategories.get(1));
        Post post4 = TestDataFactory.createPost("title4", "content", user, null);
        Post post5 = TestDataFactory.createPost("title5", "content", user, null);

        post1.delete();
        post2.delete();

        postRepository.saveAll(List.of(post1, post2, post3, post4, post5));

        // when
        List<CategoryCountResponse> result = categoryRepository.findAllWithCountByUserId(savedUser.getId());

        // then
        assertThat(result).hasSize(4);
        assertThat(result)
                .extracting(CategoryCountResponse::getName, CategoryCountResponse::getPostCount)
                .containsExactly(
                        tuple("category1", 0L),
                        tuple("category2", 1L),
                        tuple("category3", 0L),
                        tuple("미분류", 2L)
                );
    }

    private List<Category> saveCategories(User user, List<String> names) {
        return categoryRepository.saveAll(
                names.stream()
                        .map(name -> TestDataFactory.createCategory(user, name))
                        .toList());
    }
}