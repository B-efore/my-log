package com.jiwon.mylog.domain.comment.service;

import com.jiwon.mylog.domain.comment.dto.request.CommentCreateRequest;
import com.jiwon.mylog.domain.comment.dto.request.CommentUpdateRequest;
import com.jiwon.mylog.domain.comment.dto.response.CommentResponse;
import com.jiwon.mylog.domain.comment.entity.Comment;
import com.jiwon.mylog.domain.comment.repository.CommentRepository;
import com.jiwon.mylog.domain.event.dto.comment.CommentCreatedEvent;
import com.jiwon.mylog.domain.event.dto.comment.CommentDeletedEvent;
import com.jiwon.mylog.domain.post.entity.Post;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.exception.ForbiddenException;
import com.jiwon.mylog.global.common.error.exception.NotFoundException;
import com.jiwon.mylog.domain.post.repository.PostRepository;
import com.jiwon.mylog.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService {

    private final ApplicationEventPublisher eventPublisher;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @CacheEvict(value = "post::detail", key = "#postId")
    @Transactional
    public CommentResponse createComment(Long userId, Long postId, CommentCreateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_POST));

        Comment parent = null;
        if (request.getParentId() != null) {
            parent = getComment(request.getParentId());
        }

        Comment comment = Comment.create(request, parent, user, post);
        Comment savedComment = commentRepository.save(comment);

        Long receiverId = post.getUser().getId();

        eventPublisher.publishEvent(
                new CommentCreatedEvent(
                        postId,
                        receiverId,
                        savedComment.getId(),
                        userId,
                        user.getUsername(),
                        savedComment.getCreatedAt()
                )
        );

        return CommentResponse.fromComment(savedComment);
    }

    @CacheEvict(value = "post::detail", key = "#postId")
    @Transactional
    public CommentResponse updateComment(Long userId, Long postId, Long commentId, CommentUpdateRequest request) {
        Comment comment = getComment(commentId);
        validateOwner(userId, comment);
        comment.update(request);
        return CommentResponse.fromComment(comment);
    }

    @CacheEvict(value = "post::detail", key = "#postId")
    @Transactional
    public void deleteComment(Long userId, Long postId, Long commentId) {
        Comment comment = getComment(commentId);
        validateOwner(userId, comment);

        Long receiverId = comment.getPost().getUser().getId();

        eventPublisher.publishEvent(
                new CommentDeletedEvent(
                        postId,
                        receiverId,
                        comment.getId(),
                        userId,
                        comment.getCreatedAt()
                )
        );

        comment.delete();
    }

    private Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_COMMENT));
    }

    private void validateOwner(Long userId, Comment comment) {
        if (!comment.getUser().getId().equals(userId)) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN);
        }
    }
}
