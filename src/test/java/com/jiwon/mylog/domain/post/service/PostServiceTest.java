package com.jiwon.mylog.domain.post.service;

import com.jiwon.mylog.TestDataFactory;
import com.jiwon.mylog.config.EmbeddedRedisConfig;
import com.jiwon.mylog.domain.category.entity.Category;
import com.jiwon.mylog.domain.category.repository.CategoryRepository;
import com.jiwon.mylog.domain.post.dto.request.PostRequest;
import com.jiwon.mylog.domain.post.dto.response.PostDetailResponse;
import com.jiwon.mylog.domain.post.entity.Post;
import com.jiwon.mylog.domain.post.entity.PostType;
import com.jiwon.mylog.domain.post.repository.PostRepository;
import com.jiwon.mylog.domain.tag.dto.request.TagRequest;
import com.jiwon.mylog.domain.tag.entity.PostTag;
import com.jiwon.mylog.domain.tag.entity.Tag;
import com.jiwon.mylog.domain.tag.repository.tag.TagRepository;
import com.jiwon.mylog.domain.tag.service.TagService;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@Import(EmbeddedRedisConfig.class)
@SpringBootTest
@ActiveProfiles("test")
class PostServiceTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private PostService postService;

    @Autowired
    private TagService tagService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        postRepository.deleteAll();
    }

    @DisplayName("게시글 생성 시 연관된 정보(카테고리, 태그 게시글 수)가 올바르게 업데이트 된다.")
    @Test
    void createPost_연관정보() {
        // given
        User savedUser = userRepository.save(TestDataFactory.createUser("email", "accountId", "username"));
        Category savedCategory = categoryRepository.save(TestDataFactory.createCategory(savedUser, "category1"));
        List<TagRequest> tagRequest = List.of(new TagRequest("tag1"), new TagRequest("tag2"));

        PostRequest postRequest = new PostRequest("title", "content", "preview", "공개",
                savedCategory.getId(),
                tagRequest,
                false,
                PostType.NORMAL.getStatus());

        // when
        PostDetailResponse response = postService.createPost(savedUser.getId(), postRequest);

        // then
        Post post = postRepository.findById(response.getPostId()).get();
        Category postCategory = post.getCategory();
        List<Tag> tags = post.getPostTags().stream()
                .map(PostTag::getTag)
                .toList();

        assertThat(postCategory.getId()).isEqualTo(savedCategory.getId());
        assertThat(postCategory.getPostCount()).isEqualTo(1L);

        assertThat(post.getPostTags()).hasSize(2);
        assertThat(tags.get(0).getUsageCount()).isEqualTo(1L);
        assertThat(tags.get(1).getUsageCount()).isEqualTo(1L);
    }

    @DisplayName("게시글 업데이트 시 연관된 정보(카테고리, 태그 게시글 수)가 올바르게 업데이트 된다.")
    @Test
    void updatePost_연관정보() {
        // given
        User savedUser = userRepository.save(TestDataFactory.createUser("email", "accountId", "username"));

        // old
        Category savedOldCategory = categoryRepository.save(TestDataFactory.createCategory(savedUser, "category1"));
        List<TagRequest> oldTagRequest = List.of(new TagRequest("tag1"), new TagRequest("tag2"));
        PostRequest oldPostRequest = new PostRequest("title", "content", "preview", "공개",
                savedOldCategory.getId(),
                oldTagRequest,
                false,
                PostType.NORMAL.getStatus());

        PostDetailResponse oldPostResponse = postService.createPost(savedUser.getId(), oldPostRequest);

        em.flush();
        em.clear();

        // new
        Category savedNewCategory = categoryRepository.save(TestDataFactory.createCategory(savedUser, "category2"));
        List<TagRequest> newTagRequest = List.of(new TagRequest("tag1"), new TagRequest("tag3"));
        PostRequest newPostRequest = new PostRequest("title", "content", "preview", "공개",
                savedNewCategory.getId(),
                newTagRequest,
                false,
                "일반 글");

        // when
        PostDetailResponse newPostResponse = postService.updatePost(savedUser.getId(), oldPostResponse.getPostId(), newPostRequest);

        em.flush();
        em.clear();

        // then
        Post newPost = postRepository.findById(newPostResponse.getPostId()).get();
        Category postCategory = newPost.getCategory();
        Category oldCategory = categoryRepository.findById(savedOldCategory.getId()).get();
        List<Tag> tags = tagRepository.findAllByUserId(savedUser.getId());

        // category
        assertThat(postCategory.getId()).isEqualTo(savedNewCategory.getId());
        assertThat(postCategory.getPostCount()).isEqualTo(1L);
        assertThat(oldCategory.getPostCount()).isEqualTo(0L);

        // tags
        assertThat(tags.size()).isEqualTo(3);
        assertThat(tags.stream().map(Tag::getName).toList()).containsExactly("tag1", "tag2", "tag3");
        assertThat(tags.stream().map(Tag::getUsageCount).toList()).containsExactly(1L, 0L, 1L);
    }


    @DisplayName("게시글 삭제 시 연관된 정보 (카테고리, 태그, 댓글) 정보가 올바르게 업데이트 된다.")
    @Test
    void deletePost_연관정보() {
        // given
        User savedUser = userRepository.save(TestDataFactory.createUser("email", "accountId", "username"));
        Category savedCategory = categoryRepository.save(TestDataFactory.createCategory(savedUser, "category1"));
        List<TagRequest> tagRequest = List.of(new TagRequest("tag1"), new TagRequest("tag2"));

        PostRequest postRequest = new PostRequest("title", "content", "preview", "공개",
                savedCategory.getId(),
                tagRequest,
                false,
                PostType.NORMAL.getStatus());
        PostDetailResponse response = postService.createPost(savedUser.getId(), postRequest);

        em.flush();
        em.clear();

        // when
        postService.deletePost(savedUser.getId(), response.getPostId());

        em.flush();
        em.clear();

        // then
        Category category = categoryRepository.findById(savedCategory.getId()).get();
        List<Tag> tags = tagRepository.findAllByUserId(savedUser.getId());

        assertThat(category.getPostCount()).isEqualTo(0L);

        assertThat(tags.size()).isEqualTo(2);
        assertThat(tags.stream().map(Tag::getName).toList()).containsExactly("tag1", "tag2");
        assertThat(tags.stream().map(Tag::getUsageCount).toList()).containsExactly(0L, 0L);
    }
}