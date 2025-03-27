package com.jiwon.mylog.entity.category;

import com.jiwon.mylog.entity.category.request.CategoryRequest;
import com.jiwon.mylog.entity.post.Post;
import com.jiwon.mylog.entity.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String name;

    @Formula("(select count(*) from post p where p.category_id = id)")
    private int postCount;

    @OneToMany(mappedBy = "category")
    private List<Post> posts = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public static Category create(CategoryRequest request, User user) {
        return Category.builder()
                .name(request.getName())
                .postCount(0)
                .user(user)
                .build();
    }

    public void updateName(String name) {
        this.name = name;
    }
}
