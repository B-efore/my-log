package com.jiwon.mylog.domain.user.entity;

import com.jiwon.mylog.domain.category.entity.Category;
import com.jiwon.mylog.domain.comment.entity.Comment;
import com.jiwon.mylog.domain.image.entity.Image;
import com.jiwon.mylog.domain.post.entity.Post;
import com.jiwon.mylog.global.common.entity.BaseEntity;
import com.jiwon.mylog.domain.role.Role;
import com.jiwon.mylog.domain.tag.entity.Tag;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    @Builder.Default
    private String provider = "local";

    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @OneToOne
    private Image profileImage;

    @Builder.Default
    private String bio = "";

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserRole> userRoles = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> categories = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tag> tags = new ArrayList<>();

    public List<Role> getUserRoles() {
        return userRoles.stream()
                .map(UserRole::getRole)
                .collect(Collectors.toList());
    }

    public void init() {
        this.categories.add(
                Category.builder()
                        .user(this)
                        .name("전체")
                        .build()
        );
    }

    public void verifyUser() {
        this.status = UserStatus.ACTIVE;
    }

    public void updateInformation(String username, String bio) {
        this.username = username;
        this.bio = bio;
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}