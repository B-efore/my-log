package com.jiwon.mylog.domain.tag.entity;

import com.jiwon.mylog.domain.post.entity.Post;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
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
