package com.jiwon.mylog.domain.tag.entity;

import com.jiwon.mylog.domain.post.entity.Post;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "post_tag",
        uniqueConstraints = @UniqueConstraint(
                name = "post_tag_uk",
                columnNames = {"post_id", "tag_id"}
        )
)
public class PostTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;
    @ManyToOne(fetch = FetchType.LAZY)
    private Tag tag;

    public static PostTag createPostTag(Post post, Tag tag) {
        PostTag postTag = new PostTag();
        postTag.setTag(tag);
        postTag.setPost(post);
        return postTag;
    }
}
