package com.jiwon.mylog.service;

import com.jiwon.mylog.domain.category.entity.Category;
import com.jiwon.mylog.domain.post.entity.Post;
import com.jiwon.mylog.domain.post.service.PostService;
import com.jiwon.mylog.domain.post.dto.request.PostRequest;
import com.jiwon.mylog.domain.post.dto.response.PostDetailResponse;
import com.jiwon.mylog.domain.tag.service.TagService;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.domain.category.repository.CategoryRepository;
import com.jiwon.mylog.domain.post.repository.PostRepository;
import com.jiwon.mylog.domain.user.repository.UserRepository;
import java.util.Set;
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
        PostRequest request = new PostRequest("title", "content", "contentPreview", "공개", 1L, null, false);
        User user = User.builder().id(id).username("testUser").build();
        Category category = Category.builder().id(id).name("category").postCount(1).build();
        Post post = Post.create(request, user, category, Set.of());

        given(userRepository.findById(eq(id))).willReturn(Optional.of(user));
        given(categoryRepository.findByUserIdAndId(eq(id), eq(id))).willReturn(Optional.of(category));
        given(postRepository.save(any(Post.class))).willReturn(post);

        // when
        PostDetailResponse response = postService.createPost(id, request);

        // then
        assertThat(response.getCategory().getName()).isEqualTo("category");
        assertThat(response.getUser().getUsername()).isEqualTo("testUser");

        verify(userRepository).findById(id);
        verify(categoryRepository).findByUserIdAndId(id, id);
        verify(postRepository).save(any(Post.class));
    }
}