package com.jiwon.mylog.entity.comment;

import com.jiwon.mylog.entity.post.Post;
import com.jiwon.mylog.entity.Visibility;
import com.jiwon.mylog.entity.base.BaseEntity;
import com.jiwon.mylog.entity.user.User;
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

@Entity
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_cooment_id")
    private Comment parentComment;
    @OneToMany(mappedBy = "parentComment")
    private List<Comment> childComments = new ArrayList<>();
    @Column(nullable = false)
    private int depth;
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;
    @Enumerated(value = EnumType.STRING)
    private CommentStatus commentStatus;
    @Enumerated(value = EnumType.STRING)
    private Visibility visibility;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
}