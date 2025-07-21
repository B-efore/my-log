package com.jiwon.mylog.domain.category.service;

import com.jiwon.mylog.domain.category.dto.request.CategoryRequest;
import com.jiwon.mylog.domain.category.repository.CategoryRepository;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.domain.user.repository.UserRepository;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.exception.DuplicateException;
import com.jiwon.mylog.global.common.error.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CategoryServiceMockTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @DisplayName("중복된 카테고리명으로 생성을 요청할 시 예외가 발생한다.")
    @Test
    void create_Duplicate() {
        // given
        Long userId = 1L;
        User user = User.builder().id(userId).username("testUser").build();
        CategoryRequest request = new CategoryRequest("나의 카테고리");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(categoryRepository.existsByUserIdAndName(user.getId(), request.getName())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> categoryService.createCategory(userId, request))
                .isInstanceOf(DuplicateException.class)
                .hasMessage(ErrorCode.DUPLICATE_CATEGORY.getMessage());
    }

    @DisplayName("존재하지 않는 카테고리 ID로 수정을 요청할 시 예외가 발생한다.")
    @Test
    void update_notFound() {
        // given
        Long userId = 1L;
        Long categoryId = 10L;
        CategoryRequest request = new CategoryRequest("뉴카테고리!!");

        given(categoryRepository.existsByUserIdAndName(userId, request.getName())).willReturn(false);
        given(categoryRepository.findByUserIdAndId(userId, categoryId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> categoryService.updateCategory(userId, categoryId, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining( ErrorCode.NOT_FOUND_CATEGORY.getMessage());
    }

    @DisplayName("중복된 카테고리명으로 수정을 요청할 시 예외가 발생한다.")
    @Test
    void update_Duplicate() {
        // given
        Long userId = 1L;
        Long categoryId = 1L;
        CategoryRequest request = new CategoryRequest("뉴카테고리!!");

        given(categoryRepository.existsByUserIdAndName(userId, request.getName())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> categoryService.updateCategory(userId, categoryId, request))
                .isInstanceOf(DuplicateException.class)
                .hasMessage(ErrorCode.DUPLICATE_CATEGORY.getMessage());
    }
}