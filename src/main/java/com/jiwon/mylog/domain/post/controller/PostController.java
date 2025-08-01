package com.jiwon.mylog.domain.post.controller;

import com.jiwon.mylog.domain.post.dto.response.PostNavigationResponse;
import com.jiwon.mylog.domain.post.service.PostService;
import com.jiwon.mylog.domain.post.service.PostViewService;
import com.jiwon.mylog.global.security.auth.annotation.AllUser;
import com.jiwon.mylog.global.security.auth.annotation.LoginUser;
import com.jiwon.mylog.domain.post.dto.request.PostRequest;
import com.jiwon.mylog.domain.post.dto.response.PostDetailResponse;
import com.jiwon.mylog.global.common.entity.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
@Tag(name = "posts", description = "게시글 API")
public class PostController {

    private final PostService postService;
    private final PostViewService postViewService;

    @PostMapping("/posts")
    @Operation(
            summary = "게시글 생성",
            responses = {
                    @ApiResponse(responseCode = "201", description = "게시글 생성 성공")
            })
    public ResponseEntity<PostDetailResponse> createPost(
            @LoginUser Long userId,
            @Valid @RequestBody PostRequest postRequest) {
        PostDetailResponse response = postService.createPost(userId, postRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/posts/{postId}")
    @Operation(
            summary = "게시글 수정",
            responses = {
                    @ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
                    @ApiResponse(responseCode = "403", description = "게시글 수정 권한이 없음"),
                    @ApiResponse(responseCode = "404", description = "해당 게시글을 찾을 수 없음")
            }
    )
    public ResponseEntity<PostDetailResponse> updatePost(
            @LoginUser Long userId,
            @PathVariable("postId") Long postId,
            @Valid @RequestBody PostRequest postRequest) {
        PostDetailResponse response = postService.updatePost(userId, postId, postRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/posts/{postId}")
    @Operation(
            summary = "게시글 삭제",
            responses = {
                    @ApiResponse(responseCode = "204", description = "게시글 삭제 성공"),
                    @ApiResponse(responseCode = "403", description = "게시글 삭제 권한이 없음"),
                    @ApiResponse(responseCode = "404", description = "해당 게시글을 찾을 수 없음")
            }
    )
    public ResponseEntity<Void> deletePost(
            @LoginUser Long userId,
            @PathVariable("postId") Long postId) {
        postService.deletePost(userId, postId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/posts/notices")
    @Operation(
            summary = "공지글 페이징 조회"
    )
    public ResponseEntity<PageResponse> getAllNotices(
            @PageableDefault(size = 10, page = 0,
                    sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        PageResponse response = postService.getAllNotices(pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/posts/{postId}")
    @Operation(
            summary = "게시글 단건 조회",
            responses = {
                    @ApiResponse(responseCode = "200", description = "게시글 단건 조회"),
                    @ApiResponse(responseCode = "404", description = "해당 게시글을 찾을 수 없음")
            }
    )
    public ResponseEntity<PostDetailResponse> getPost(
            @AllUser String userKey,
            @PathVariable("postId") Long postId) {
        PostDetailResponse response = postService.getPost(postId);
        int view = postViewService.incrementPostView(response.getPostId(), response.getViews(), userKey);
        response.setViews(view);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/posts")
    public ResponseEntity<PageResponse> getPosts(
            @PageableDefault(size = 10, page = 0,
                    sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        PageResponse response = postService.getPosts(pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/posts/{postId}/navigation")
    @Operation(
            summary = "현재 게시글의 정보(카테고리 ID, 카테고리 내에서의 현재 page, offset)를 획득한다."
    )
    public ResponseEntity<PostNavigationResponse> getPostNavigation(@PathVariable("postId") Long postId) {
        PostNavigationResponse response = postService.getPostNavigation(postId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories/{categoryId}/posts")
    public ResponseEntity<PageResponse> getCategorizedPosts(
            @PathVariable("categoryId") Long categoryId,
            @RequestParam Long userId,
            @PageableDefault(size = 5, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        PageResponse response = postService.getCategorizedPosts(categoryId, userId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}/categories/{categoryId}/posts")
    @Operation(
            summary = "특정 유저의 카테고리별 게시글 조회 (태그 필터링 및 키워드 검색 포함)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자 혹은 카테고리")
            }
    )
    public ResponseEntity<PageResponse> getPostsByCategoryAndTags(
            @PathVariable("userId") Long userId,
            @PathVariable("categoryId") Long categoryId,
            @RequestParam(value = "tags", required = false) List<Long> tags,
            @RequestParam(value = "keyword", required = false) String keyword,
            @PageableDefault(size = 10, page = 0,
                    sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        if (keyword == null || keyword.isEmpty()) {
            return ResponseEntity.ok(postService.getFilteredPosts(
                    userId, categoryId, tags, "", pageable));
        } else {
            return ResponseEntity.ok(postService.searchPosts(
                    userId, categoryId, tags, keyword, pageable));
        }
    }
}
