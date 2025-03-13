package com.jiwon.mylog.entity.post;


import com.jiwon.mylog.entity.category.Category;
import com.jiwon.mylog.entity.Visibility;
import com.jiwon.mylog.entity.base.BaseEntity;
import com.jiwon.mylog.entity.user.User;
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
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

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
    @ColumnDefault("0")
    private int views;
    private boolean pinned;
    @Enumerated(value = EnumType.STRING)
    private PostStatus postStatus;
    @Enumerated(value = EnumType.STRING)
    private Visibility visibility;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="category_id")
    private Category category;
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostTag> postTags = new ArrayList<>();
}
