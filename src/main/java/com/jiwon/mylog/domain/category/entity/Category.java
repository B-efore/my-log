package com.jiwon.mylog.domain.category.entity;

import com.jiwon.mylog.domain.category.dto.request.CategoryRequest;
import com.jiwon.mylog.domain.post.entity.Post;
import com.jiwon.mylog.domain.user.entity.User;
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
import org.hibernate.annotations.Formula;

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
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String name;

//    @Formula("(select count(*) from post p where p.category_id = id)")
//    private int postCount;

    @OneToMany(mappedBy = "category")
    private List<Post> posts = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public static Category create(CategoryRequest request, User user) {
        return Category.builder()
                .name(request.getName())
//                .postCount(0)
                .user(user)
                .build();
    }

    public void updateName(String name) {
        this.name = name;
    }
}
