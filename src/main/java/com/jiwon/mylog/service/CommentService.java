package com.jiwon.mylog.service;

import com.jiwon.mylog.entity.comment.Comment;
import com.jiwon.mylog.entity.comment.dto.request.CommentCreateRequest;
import com.jiwon.mylog.entity.comment.dto.request.CommentUpdateRequest;
import com.jiwon.mylog.entity.comment.dto.response.CommentResponse;
import com.jiwon.mylog.entity.post.Post;
import com.jiwon.mylog.entity.user.User;
import com.jiwon.mylog.exception.ErrorCode;
import com.jiwon.mylog.exception.ForbiddenException;
import com.jiwon.mylog.exception.NotFoundException;
import com.jiwon.mylog.repository.CommentRepository;
import com.jiwon.mylog.repository.PostRepository;
import com.jiwon.mylog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CommentResponse create(Long userId, CommentCreateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_POST));

        Comment parent = null;
        if (request.getParentId() != null) {
             parent = getComment(request.getParentId());
        }

        Comment comment = Comment.create(request, parent, user, post);
        Comment savedComment = commentRepository.save(comment);

        return CommentResponse.fromComment(savedComment);
    }

    @Transactional
    public CommentResponse update(Long userId, Long commentId, CommentUpdateRequest request) {
        Comment comment = getComment(commentId);
        validateOwner(userId, comment);
        comment.update(request);
        return CommentResponse.fromComment(comment);
    }

    @Transactional
    public void delete(Long userId, Long commentId) {
        Comment comment = getComment(commentId);
        validateOwner(userId, comment);
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
