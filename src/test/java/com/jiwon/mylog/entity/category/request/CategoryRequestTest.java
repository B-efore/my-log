package com.jiwon.mylog.entity.category.request;

import com.jiwon.mylog.entity.category.dto.request.CategoryRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @DisplayName("카테고리명이 비어있는 경우 오류가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void validateCategoryNameIsNotBlank(String name) {
        // given
        CategoryRequest request = new CategoryRequest(name);

        // when
        Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).anyMatch(v -> v.getMessage().equals("카테고리명은 필수 입력값입니다."));
    }

    @DisplayName("카테고리명에 따른 유효성을 검사한다.")
    @ParameterizedTest
    @MethodSource("categoryNames")
    void validateCategoryNameLength(String name, boolean validated) {
        // given
        CategoryRequest request = new CategoryRequest(name);

        // when
        Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(request);

        // then
        if (validated) {
            assertThat(violations).isEmpty();
        } else {
            assertThat(violations).anyMatch(v -> v.getMessage().equals("카테고리명은 최대 10자까지 입력할 수 있습니다."));
        }
    }

    private static Stream<Arguments> categoryNames() {
        return Stream.of(
                Arguments.of("a".repeat(10), true),
                Arguments.of("a".repeat(11), false)
        );
    }
}