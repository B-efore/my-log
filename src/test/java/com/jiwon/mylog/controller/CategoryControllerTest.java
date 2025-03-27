package com.jiwon.mylog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiwon.mylog.entity.category.dto.response.CategoryResponse;
import com.jiwon.mylog.entity.category.request.CategoryRequest;
import com.jiwon.mylog.entity.post.dto.request.PostCreateRequest;
import com.jiwon.mylog.resolver.LoginUserArgumentResolver;
import com.jiwon.mylog.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
    void createCategory() throws Exception {
        // given
        CategoryRequest request = new CategoryRequest("카테고리명");
        CategoryResponse response = new CategoryResponse(1L, "카테고리명");

        given(resolver.supportsParameter(any())).willReturn(true);
        given(resolver.resolveArgument(any(), any(), any(), any())).willReturn(1L);
        given(categoryService.createCategory(any(Long.class), any(CategoryRequest.class))).willReturn(response);

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
}