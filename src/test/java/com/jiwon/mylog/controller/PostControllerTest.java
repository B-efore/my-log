package com.jiwon.mylog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiwon.mylog.domain.post.controller.PostController;
import com.jiwon.mylog.global.common.enums.Visibility;
import com.jiwon.mylog.domain.category.dto.response.CategoryResponse;
import com.jiwon.mylog.domain.post.entity.PostStatus;
import com.jiwon.mylog.domain.post.dto.request.PostRequest;
import com.jiwon.mylog.domain.post.dto.response.PostDetailResponse;
import com.jiwon.mylog.domain.tag.dto.response.TagResponse;
import com.jiwon.mylog.domain.user.entity.UserStatus;
import com.jiwon.mylog.domain.user.dto.response.UserResponse;
import com.jiwon.mylog.global.security.auth.resolver.LoginUserArgumentResolver;
import com.jiwon.mylog.domain.post.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    private LoginUserArgumentResolver resolver;

    @DisplayName("게시글을 생성한다.")
    @Test
    void createPost() throws Exception {
        // given
        PostRequest request = new PostRequest("title", "content", "contentPreview", "공개", 1L, null, false);
        UserResponse user = createUser(1L, "testUser");
        CategoryResponse category = createCategory(1L, "category");
        List<TagResponse> tags = List.of(
                createTag(1L, "tag1"),
                createTag(2L, "tag2"));
        PostDetailResponse response = createResponse(user, category, tags);

        given(resolver.supportsParameter(any())).willReturn(true);
        given(resolver.resolveArgument(any(), any(), any(), any())).willReturn(1L);
        given(postService.createPost(any(Long.class), any(PostRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.content").value("content"))
                .andExpect(jsonPath("$.postStatus").value("게시됨"))
                .andExpect(jsonPath("$.visibility").value("공개"))
                .andExpect(jsonPath("$.tags[0].name").value("tag1"))
                .andExpect(jsonPath("$.tags[1].name").value("tag2"))
                .andExpect(jsonPath("$.category.name").value("category"))
                .andExpect(jsonPath("$.user.userId").value(1L));
    }

    private PostDetailResponse createResponse(UserResponse user, CategoryResponse category, List<TagResponse> tags) {
        return PostDetailResponse.builder()
                .postId(1L)
                .title("title")
                .content("content")
                .visibility(Visibility.PUBLIC)
                .postStatus(PostStatus.PUBLISHED)
                .tags(tags)
                .views(0)
                .user(user)
                .category(category)
                .build();
    }

    private CategoryResponse createCategory(Long categoryId, String name) {
        return CategoryResponse.builder()
                .categoryId(categoryId)
                .name(name)
                .build();
    }

    private UserResponse createUser(Long userId, String name) {
        return UserResponse.builder()
                .userId(userId)
                .username(name)
                .userStatus(UserStatus.ACTIVE)
                .build();
    }

    private TagResponse createTag(long id, String tag1) {
        return TagResponse.builder().id(id).name(tag1).build();
    }
}