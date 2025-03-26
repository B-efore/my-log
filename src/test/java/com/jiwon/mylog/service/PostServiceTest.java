package com.jiwon.mylog.service;

import com.jiwon.mylog.entity.category.Category;
import com.jiwon.mylog.entity.post.Post;
import com.jiwon.mylog.entity.post.dto.request.PostCreateRequest;
import com.jiwon.mylog.entity.post.dto.response.PostDetailResponse;
import com.jiwon.mylog.entity.user.User;
import com.jiwon.mylog.repository.CategoryRepository;
import com.jiwon.mylog.repository.PostRepository;
import com.jiwon.mylog.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private TagService tagService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @DisplayName("게시글을 생성한다.")
    @Test
    void createPost() {
        // given
        Long id = 1L;
        PostCreateRequest request = new PostCreateRequest("title", "content", "contentPreview", "공개", 1L, null);
        User user = User.builder().id(id).username("testUser").build();
        Category category = Category.builder().id(id).name("category").postCount(1).build();
        Post post = Post.create(request, user, category);

        given(userRepository.findById(eq(id))).willReturn(Optional.of(user));
        given(categoryRepository.findById(eq(id))).willReturn(Optional.of(category));
        given(postRepository.save(any(Post.class))).willReturn(post);

        // when
        PostDetailResponse response = postService.createPost(id, request);

        // then
        assertThat(response.getCategory().getName()).isEqualTo("category");
        assertThat(response.getUser().getUsername()).isEqualTo("testUser");

        verify(userRepository).findById(id);
        verify(categoryRepository).findById(id);
        verify(postRepository).save(any(Post.class));
    }
}