package com.jiwon.mylog.domain.post.entity;


import com.jiwon.mylog.domain.category.entity.Category;
import com.jiwon.mylog.domain.image.entity.Image;
import com.jiwon.mylog.global.common.enums.Visibility;
import com.jiwon.mylog.global.common.entity.BaseEntity;
import com.jiwon.mylog.domain.comment.entity.Comment;
import com.jiwon.mylog.domain.post.dto.request.PostRequest;
import com.jiwon.mylog.domain.tag.entity.PostTag;
import com.jiwon.mylog.domain.tag.entity.Tag;
import com.jiwon.mylog.domain.user.entity.User;
import jakarta.persistence.CascadeType;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(length = 100)
    private String contentPreview;

    @Builder.Default
    private int views = 0;

    @Builder.Default
    private boolean pinned = false;

    @Enumerated(value = EnumType.STRING)
    private PostStatus postStatus;

    @Enumerated(value = EnumType.STRING)
    private Visibility visibility;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostTag> postTags = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

    public static Post create(PostRequest request, User user, Category category, List<Tag> tags) {
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .contentPreview(request.getContentPreview())
                .postStatus(PostStatus.PUBLISHED)
                .visibility(Visibility.fromString(request.getVisibility()))
                .user(user)
                .category(category)
                .pinned(request.isPinned())
                .build();
        post.setTags(tags);
        return post;
    }

    public void update(PostRequest request, Category category, List<Tag> tags) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.contentPreview = request.getContentPreview();
        this.visibility = Visibility.fromString(request.getVisibility());
        this.pinned = request.isPinned();
        this.category = category;
        updateTags(tags);
    }

    private void setTags(List<Tag> tags) {
        this.postTags = tags.stream()
                .map(tag -> PostTag.createPostTag(this, tag))
                .toList();
    }

    private void updateTags(List<Tag> tags) {
        this.postTags.clear();
        this.postTags.addAll(
                tags.stream()
                        .map(tag -> PostTag.createPostTag(this, tag))
                        .toList()
        );
    }

    public void delete() {
        super.delete();
    }

    public boolean isDeleted() {
        return super.isDeleted();
    }
}
