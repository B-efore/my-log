package com.jiwon.mylog.domain.image.entity;

import com.jiwon.mylog.domain.post.entity.Post;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.global.common.entity.BaseEntity;
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
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Image extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileKey;

    @Enumerated(EnumType.STRING)
    private ImageType imageType;

    @OneToOne(mappedBy = "profileImage", cascade = CascadeType.ALL, orphanRemoval = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public static Image forUserProfile(User user, String key) {
        Image image = Image.builder()
                .fileKey(key)
                .imageType(ImageType.PROFILE)
                .user(user)
                .build();
        image.user.updateProfileImage(image);
        return image;
    }

    public static Image forPost(Post post, String key) {
        return Image.builder()
                .fileKey(key)
                .imageType(ImageType.POST)
                .post(post)
                .build();
    }
}
