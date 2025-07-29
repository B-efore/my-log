package com.jiwon.mylog.domain.post.service;

import com.jiwon.mylog.domain.category.entity.Category;
import com.jiwon.mylog.domain.comment.entity.Comment;
import com.jiwon.mylog.domain.event.dto.PostCreatedEvent;
import com.jiwon.mylog.domain.post.dto.request.PostRequest;
import com.jiwon.mylog.domain.post.dto.response.MainPostResponse;
import com.jiwon.mylog.domain.post.dto.response.NoticePostResponse;
import com.jiwon.mylog.domain.post.dto.response.PostDetailResponse;
import com.jiwon.mylog.global.common.entity.PageResponse;
import com.jiwon.mylog.domain.post.dto.response.PostSummaryResponse;
import com.jiwon.mylog.domain.post.entity.Post;
import com.jiwon.mylog.domain.post.repository.PostRepository;
import com.jiwon.mylog.domain.tag.entity.Tag;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.exception.ForbiddenException;
import com.jiwon.mylog.global.common.error.exception.NotFoundException;
import com.jiwon.mylog.domain.category.repository.CategoryRepository;
import com.jiwon.mylog.domain.user.repository.UserRepository;
import java.util.List;

import com.jiwon.mylog.domain.tag.service.TagService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    private final ApplicationEventPublisher eventPublisher;
    private final TagService tagService;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Caching(
            put = @CachePut(value = "post::detail", key = "#result.postId", unless = "#result.postId == null"),
            evict = {
                    @CacheEvict(value = "post::notice", allEntries = true, condition = "#postRequest.type.equals('공지')"),
                    @CacheEvict(value = "post::main", allEntries = true, condition = "#postRequest.type.equals('일반 글')"),
                    @CacheEvict(value = "post::list", allEntries = true, condition = "#postRequest.type.equals('일반 글')"),
                    @CacheEvict(value = "post::filter", allEntries = true, condition = "#postRequest.type.equals('일반 글')"),

                    @CacheEvict(value = "blog::home", key = "#userId", condition = "#userId != null")
            }
    )
    @Transactional
    public PostDetailResponse createPost(Long userId, PostRequest postRequest) {
        User user = getUserById(userId);
        Category category = getCategoryById(userId, postRequest.getCategoryId());
        List<Tag> tags = tagService.getTagsById(user, postRequest.getTagRequests());

        Post post = Post.create(postRequest, user, category, tags);
        Post savedPost = postRepository.save(post);

        increaseRelatedPostInfo(category, tags);
        eventPublisher.publishEvent(new PostCreatedEvent(userId, savedPost.getId()));

        return PostDetailResponse.fromPost(savedPost);
    }

    @Caching(
            put = @CachePut(value = "post::detail", key = "#postId", unless = "#postId == null"),
            evict = {
                    @CacheEvict(value = "post::notice", allEntries = true, condition = "#postRequest.type.equals('공지')"),
                    @CacheEvict(value = "post::main", allEntries = true, condition = "#postRequest.type.equals('일반 글')"),
                    @CacheEvict(value = "post::list", allEntries = true, condition = "#postRequest.type.equals('일반 글')"),
                    @CacheEvict(value = "post::filter", allEntries = true, condition = "#postRequest.type.equals('일반 글')"),

                    @CacheEvict(value = "blog::home", key = "#userId", condition = "#userId != null")
            }
    )
    @Transactional
    public PostDetailResponse updatePost(Long userId, Long postId, PostRequest postRequest) {
        Post post = getPostWithDetails(postId);
        validateOwner(post, userId);

        decreaseRelatedPostInfo(post);
        Category category = getCategoryById(userId, postRequest.getCategoryId());
        List<Tag> tags = tagService.getTagsById(post.getUser(), postRequest.getTagRequests());
        increaseRelatedPostInfo(category, tags);

        post.update(postRequest, category, tags);
        return PostDetailResponse.fromPost(post);
    }

    @Caching(evict = {
            @CacheEvict(value = "post::detail", key = "#postId"),
            @CacheEvict(value = "post::main", allEntries = true),
            @CacheEvict(value = "post::list", allEntries = true),
            @CacheEvict(value = "post::filter", allEntries = true),

            @CacheEvict(value = "blog::home", key = "#userId", condition = "#userId != null")
    })
    @Transactional
    public void deletePost(Long userId, Long postId) {
        Post post = getPostWithDetails(postId);
        validateOwner(post, userId);
        decreaseRelatedPostInfo(post);
        deleteRelatedPostInfo(post);
        post.delete();
    }

    /**
     * 포스트와 연관된 카테고리, 태그 게시글 카운트 증가
     * @param category
     * @param tags
     */
    private void increaseRelatedPostInfo(Category category, List<Tag> tags) {
        if (category != null) {
            category.incrementUsage();
        }
        tags.forEach(Tag::incrementUsage);
    }

    /**
     * 포스트와 연관된 카테고리, 태그 게시글 카운트 감소
     * @param post
     */
    private void decreaseRelatedPostInfo(Post post) {
        if (post.getCategory() != null) {
            post.getCategory().decrementUsage();
        }
        post.getPostTags().forEach(postTag -> postTag.getTag().decrementUsage());
    }

    /**
     * 포스트와 연관된 정보 삭제 (포스트태그, 댓글)
     * @param post
     */
    private void deleteRelatedPostInfo(Post post){
        post.getPostTags().forEach(postTag -> {
            postTag.setTag(null);
            postTag.setPost(null);
        });
        post.getPostTags().clear();

        post.getComments().forEach(Comment::delete);
    }

    @Cacheable(value = "post::notice",
            key = "'page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize",
            condition = "#pageable != null"
    )
    @Transactional(readOnly = true)
    public PageResponse getAllNotices(Pageable pageable) {
        Page<Post> noticePage = postRepository.findAllNotice(pageable);
        List<NoticePostResponse> notices = noticePage.stream()
                .map(NoticePostResponse::fromPost)
                .toList();
        return PageResponse.from(
                notices,
                noticePage.getNumber(),
                noticePage.getSize(),
                noticePage.getTotalPages(),
                noticePage.getTotalElements());
    }

    @Cacheable(value = "post::detail",
            key = "#postId",
            unless = "#result == null",
            condition = "#postId != null && #postId > 0"
    )
    @Transactional(readOnly = true)
    public PostDetailResponse getPost(Long postId) {
        return postRepository.findPostDetail(postId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_POST));
    }

    @Cacheable(value = "post::main",
            key = "'page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize",
            condition = "#pageable != null")
    @Transactional(readOnly = true)
    public PageResponse getPosts(Pageable pageable) {
        Page<Post> postPage = postRepository.findAll(pageable);
        List<MainPostResponse> posts = postPage.stream()
                .map(MainPostResponse::fromPost)
                .toList();
        return PageResponse.from(
                posts,
                postPage.getNumber(),
                postPage.getSize(),
                postPage.getTotalPages(),
                postPage.getTotalElements());
    }

    @Cacheable(value = "post::list",
            key = "'user:' + #userId + ':page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize",
            condition = "#userId != null && #pageable != null"
    )
    @Transactional(readOnly = true)
    public PageResponse getUserPosts(Long userId, Pageable pageable) {
        Page<Post> postPage = postRepository.findAllByUser(userId, pageable);
        List<PostSummaryResponse> posts = postPage.stream()
                .map(PostSummaryResponse::fromPost)
                .toList();

        return PageResponse.from(
                posts,
                postPage.getNumber(),
                postPage.getSize(),
                postPage.getTotalPages(),
                postPage.getTotalElements());
    }

    @Cacheable(value = "post::filter", keyGenerator = "postCacheKeyGenerator")
    @Transactional(readOnly = true)
    public PageResponse getPostsByCategoryAndTags(
            Long userId,
            Long categoryId, List<Long> tagIds, String keyword,
            Pageable pageable) {

        Page<Post> postPage;

        if (categoryId == null || categoryId.equals(0L)) {
            postPage = postRepository.findByTags(userId, tagIds, keyword, pageable);
        } else {
            postPage = postRepository.findByCategoryAndTags(userId, categoryId, tagIds, keyword, pageable);
        }

        List<PostSummaryResponse> posts = postPage.stream()
                .map(PostSummaryResponse::fromPost)
                .toList();

        return PageResponse.from(
                posts,
                postPage.getNumber(),
                postPage.getSize(),
                postPage.getTotalPages(),
                (int) postPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public PageResponse searchPosts(
            Long userId,
            Long categoryId, List<Long> tagIds, String keyword,
            Pageable pageable) {

        Page<Post> postPage;

        if (categoryId == null || categoryId.equals(0L)) {
            postPage = postRepository.findByTags(userId, tagIds, keyword, pageable);
        } else {
            postPage = postRepository.findByCategoryAndTags(userId, categoryId, tagIds, keyword, pageable);
        }

        List<PostSummaryResponse> posts = postPage.stream()
                .map(PostSummaryResponse::fromPost)
                .toList();

        return PageResponse.from(
                posts,
                postPage.getNumber(),
                postPage.getSize(),
                postPage.getTotalPages(),
                (int) postPage.getTotalElements());
    }

    private Post getPostWithDetails(Long postId) {
        Post post = postRepository.findWithUserAndCategory(postId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_POST));
        postRepository.findWithTags(post);
        postRepository.findWithComments(post);
        return post;
    }

    private Category getCategoryById(Long userId, Long categoryId) {
        if (categoryId == null || categoryId <= 0) return null;

        return categoryRepository.findByUserIdAndId(userId, categoryId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_CATEGORY));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
    }

    private void validateOwner(Post post, Long userId) {
        if(!post.getUser().getId().equals(userId)) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN);
        }
    }
}
