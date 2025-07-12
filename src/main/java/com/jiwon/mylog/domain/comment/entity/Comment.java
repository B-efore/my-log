package com.jiwon.mylog.domain.comment.entity;

import com.jiwon.mylog.domain.comment.dto.request.CommentCreateRequest;
import com.jiwon.mylog.domain.comment.dto.request.CommentUpdateRequest;
import com.jiwon.mylog.domain.post.entity.Post;
import com.jiwon.mylog.global.common.enums.Visibility;
import com.jiwon.mylog.global.common.entity.BaseEntity;
import com.jiwon.mylog.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @Builder.Default
    @OneToMany(mappedBy = "parent")
    private List<Comment> children = new ArrayList<>();

    @Builder.Default
    @Column(nullable = false)
    private int depth = 0;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private CommentStatus commentStatus;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Visibility visibility;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, updatable = false)
    private Post post;

    public static Comment create(CommentCreateRequest request, Comment parent, User user, Post post) {
        int depth = (parent == null) ? 0 : parent.getDepth() + 1;
        return Comment.builder()
                .parent(parent)
                .depth(depth)
                .content(request.getContent())
                .commentStatus(CommentStatus.WRITTEN)
                .visibility(Visibility.fromString(request.getVisibility()))
                .user(user)
                .post(post)
                .build();
    }

    public void update(CommentUpdateRequest request) {
        this.content = request.getContent();
        this.visibility = Visibility.fromString(request.getVisibility());
    }

    public void delete() {
        super.delete();
    }
}