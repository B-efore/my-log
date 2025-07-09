package com.jiwon.mylog.domain.category.entity;

import com.jiwon.mylog.domain.category.dto.request.CategoryRequest;
import com.jiwon.mylog.domain.post.entity.Post;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.global.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(
        name = "category",
        uniqueConstraints = @UniqueConstraint(
                name = "category_uk",
                columnNames = {"user_id", "name"}
        )
)
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String name;

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long postCount = 0L;

    @OneToMany(mappedBy = "category")
    private List<Post> posts = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public static Category create(CategoryRequest request, User user) {
        return Category.builder()
                .name(request.getName())
                .user(user)
                .postCount(0L)
                .build();
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void incrementUsage() {
        this.postCount++;
    }

    public void decrementUsage() {
        this.postCount = Math.max(0, this.postCount - 1);
    }
}
