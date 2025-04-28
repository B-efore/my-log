package com.jiwon.mylog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiwon.mylog.domain.category.controller.CategoryController;
import com.jiwon.mylog.domain.category.dto.response.CategoryResponse;
import com.jiwon.mylog.domain.category.dto.request.CategoryRequest;
import com.jiwon.mylog.global.security.auth.resolver.LoginUserArgumentResolver;
import com.jiwon.mylog.domain.category.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private LoginUserArgumentResolver resolver;

    @DisplayName("카테고리를 생성한다.")
    @Test
    void create() throws Exception {
        // given
        CategoryRequest request = new CategoryRequest("카테고리명");
        CategoryResponse response = new CategoryResponse(1L, "카테고리명");

        given(resolver.supportsParameter(any())).willReturn(true);
        given(resolver.resolveArgument(any(), any(), any(), any())).willReturn(1L);
        given(categoryService.create(any(Long.class), any(CategoryRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("카테고리명"))
                .andExpect(jsonPath("$.categoryId").value(1L));

    }

    @DisplayName("카테고리를 업데이트한다.")
    @Test
    void update() throws Exception {
        // given
        Long categoryId = 1L;
        CategoryRequest request = new CategoryRequest("새로운 카테고리명");
        CategoryResponse response = new CategoryResponse(categoryId, "새로운 카테고리명");

        given(resolver.supportsParameter(any())).willReturn(true);
        given(resolver.resolveArgument(any(), any(), any(), any())).willReturn(1L);
        given(categoryService.update(any(Long.class), eq(categoryId), any(CategoryRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(patch("/categories/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("새로운 카테고리명"))
                .andExpect(jsonPath("$.categoryId").value(categoryId));

    }

    @DisplayName("카테고리를 삭제한다.")
    @Test
    void delete_category() throws Exception {
        // given
        Long categoryId = 1L;

        given(resolver.supportsParameter(any())).willReturn(true);
        given(resolver.resolveArgument(any(), any(), any(), any())).willReturn(1L);

        // when & then
        mockMvc.perform(delete("/categories/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}