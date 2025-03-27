package com.jiwon.mylog.service;

import com.jiwon.mylog.entity.category.request.CategoryRequest;
import com.jiwon.mylog.entity.user.User;
import com.jiwon.mylog.exception.DuplicateException;
import com.jiwon.mylog.exception.ErrorCode;
import com.jiwon.mylog.repository.CategoryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserService userService;

    @DisplayName("중복된 카테고리명으로 생성을 요청할 시 예외가 발생한다.")
    @Test
    void createCategory_Duplicate() {
        // given
        Long userId = 1L;
        User user = User.builder().id(userId).username("testUser").build();
        CategoryRequest request = new CategoryRequest("나의 카테고리");

        given(userService.findUserById(userId)).willReturn(user);
        given(categoryRepository.existsByUserAndName(user, request.getName())).willReturn(true);

        // when & then
        Assertions.assertThatThrownBy(() -> categoryService.createCategory(userId, request))
                .isInstanceOf(DuplicateException.class)
                .hasMessage(ErrorCode.DUPLICATE_CATEGORY.getMessage());

    }
}