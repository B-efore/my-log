package com.jiwon.mylog.entity.post.dto.request;

import com.jiwon.mylog.domain.post.dto.request.PostRequest;
import com.jiwon.mylog.domain.post.entity.PostType;
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

class PostRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @DisplayName("제목 길이에 따른 유효성을 검사한다.")
    @ParameterizedTest
    @MethodSource("titles")
    void validateTitleLength(String title, boolean validated) {
        // given
        PostRequest request = new PostRequest(title, "content", "preview", "visibility", 1L, null, false, PostType.NORMAL.getStatus());

        // when
        Set<ConstraintViolation<PostRequest>> violations = validator.validate(request);

        // then
        if (validated) {
            assertThat(violations).isEmpty();
        } else {
            assertThat(violations).anyMatch(v -> v.getMessage().equals("제목은 최대 255자까지 입력할 수 있습니다."));
        }
    }

    private static Stream<Arguments> titles() {
        return Stream.of(
                Arguments.of("a".repeat(255), true),
                Arguments.of("a".repeat(256), false)
        );
    }

    @DisplayName("제목이 비어있는 경우 오류가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void validateTitleIsNotBlank(String title) {
        // given
        PostRequest request = new PostRequest(title, "content", "preview", "visibility", 1L, null, false, PostType.NORMAL.getStatus());

        // when
        Set<ConstraintViolation<PostRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).anyMatch(v -> v.getMessage().equals("제목은 필수 입력값입니다."));
    }

    @DisplayName("내용이 비어있는 경우 오류가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void validateContentIsNotBlank(String content) {
        // given
        PostRequest request = new PostRequest("title", content, "preview", "visibility", 1L, null, false, PostType.NORMAL.getStatus());

        // when
        Set<ConstraintViolation<PostRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).anyMatch(v -> v.getMessage().equals("내용은 필수 입력값입니다."));
    }

    @DisplayName("공개 범위가 선택되지 않은 경우 오류가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void validateVisibilityIsNotBlank(String visibility) {
        // given
        PostRequest request = new PostRequest("title", "content", "preview", visibility, 1L, null, false, PostType.NORMAL.getStatus());

        // when
        Set<ConstraintViolation<PostRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).anyMatch(v -> v.getMessage().equals("게시글 공개 범위를 지정해주세요."));
    }

    @DisplayName("미리보기 길이에 따른 유효성을 검사한다.")
    @ParameterizedTest
    @MethodSource("contentPreviews")
    void validateContentPreviewLength(String contentPreview, boolean validated) {
        // given
        PostRequest request = new PostRequest("title", "content", contentPreview, "visibility", 1L, null, false, PostType.NORMAL.getStatus());

        // when
        Set<ConstraintViolation<PostRequest>> violations = validator.validate(request);

        // then
        if (validated) {
            assertThat(violations).isEmpty();
        } else {
            assertThat(violations).anyMatch(v -> v.getMessage().equals("미리보기는 최대 100자까지 입력할 수 있습니다."));
        }
    }

    private static Stream<Arguments> contentPreviews() {
        return Stream.of(
                Arguments.of("a".repeat(100), true),
                Arguments.of("a".repeat(101), false)
        );
    }
}