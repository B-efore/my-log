package com.jiwon.mylog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiwon.mylog.entity.Visibility;
import com.jiwon.mylog.entity.category.dto.response.CategoryResponse;
import com.jiwon.mylog.entity.post.PostStatus;
import com.jiwon.mylog.entity.post.dto.request.PostCreateRequest;
import com.jiwon.mylog.entity.post.dto.response.PostDetailResponse;
import com.jiwon.mylog.entity.tag.dto.response.TagResponse;
import com.jiwon.mylog.entity.user.UserStatus;
import com.jiwon.mylog.entity.user.dto.response.UserResponse;
import com.jiwon.mylog.resolver.LoginUserArgumentResolver;
import com.jiwon.mylog.service.PostService;
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
        PostCreateRequest request = new PostCreateRequest("title", "content", "contentPreview", "공개", 1L, null);
        UserResponse user = createUser(1L, "testUser");
        CategoryResponse category = createCategory("category");
        List<TagResponse> tags = List.of(
                createTag(1L, "tag1"),
                createTag(2L, "tag2"));
        PostDetailResponse response = createResponse(user, category, tags);

        given(resolver.supportsParameter(any())).willReturn(true);
        given(resolver.resolveArgument(any(), any(), any(), any())).willReturn(1L);
        given(postService.createPost(any(Long.class), any(PostCreateRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/posts")
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

    private CategoryResponse createCategory(String name) {
        return CategoryResponse.builder()
                .name(name)
                .postCount(1)
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