package com.jiwon.mylog.service;

import com.jiwon.mylog.entity.comment.Comment;
import com.jiwon.mylog.entity.comment.dto.request.CommentRequest;
import com.jiwon.mylog.entity.comment.dto.response.CommentResponse;
import com.jiwon.mylog.entity.post.Post;
import com.jiwon.mylog.entity.user.User;
import com.jiwon.mylog.exception.ErrorCode;
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
    public CommentResponse create(Long userId, CommentRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_POST));

        Comment parent = null;
        if (request.getParentId() != null) {
             parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_COMMENT));
        }

        Comment comment = Comment.create(request, parent, user, post);
        Comment savedComment = commentRepository.save(comment);

        return CommentResponse.fromComment(savedComment);
    }
}
