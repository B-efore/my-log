package com.jiwon.mylog.domain.post.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPost is a Querydsl query type for Post
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPost extends EntityPathBase<Post> {

    private static final long serialVersionUID = -127371507L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPost post = new QPost("post");

    public final com.jiwon.mylog.global.common.entity.QBaseEntity _super = new com.jiwon.mylog.global.common.entity.QBaseEntity(this);

    public final com.jiwon.mylog.domain.category.entity.QCategory category;

    public final ListPath<com.jiwon.mylog.domain.comment.entity.Comment, com.jiwon.mylog.domain.comment.entity.QComment> comments = this.<com.jiwon.mylog.domain.comment.entity.Comment, com.jiwon.mylog.domain.comment.entity.QComment>createList("comments", com.jiwon.mylog.domain.comment.entity.Comment.class, com.jiwon.mylog.domain.comment.entity.QComment.class, PathInits.DIRECT2);

    public final StringPath content = createString("content");

    public final StringPath contentPreview = createString("contentPreview");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<com.jiwon.mylog.domain.image.entity.PostImage, com.jiwon.mylog.domain.image.entity.QPostImage> images = this.<com.jiwon.mylog.domain.image.entity.PostImage, com.jiwon.mylog.domain.image.entity.QPostImage>createList("images", com.jiwon.mylog.domain.image.entity.PostImage.class, com.jiwon.mylog.domain.image.entity.QPostImage.class, PathInits.DIRECT2);

    public final BooleanPath pinned = createBoolean("pinned");

    public final EnumPath<PostStatus> postStatus = createEnum("postStatus", PostStatus.class);

    public final ListPath<com.jiwon.mylog.domain.tag.entity.PostTag, com.jiwon.mylog.domain.tag.entity.QPostTag> postTags = this.<com.jiwon.mylog.domain.tag.entity.PostTag, com.jiwon.mylog.domain.tag.entity.QPostTag>createList("postTags", com.jiwon.mylog.domain.tag.entity.PostTag.class, com.jiwon.mylog.domain.tag.entity.QPostTag.class, PathInits.DIRECT2);

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final com.jiwon.mylog.domain.user.entity.QUser user;

    public final NumberPath<Integer> views = createNumber("views", Integer.class);

    public final EnumPath<com.jiwon.mylog.global.common.enums.Visibility> visibility = createEnum("visibility", com.jiwon.mylog.global.common.enums.Visibility.class);

    public QPost(String variable) {
        this(Post.class, forVariable(variable), INITS);
    }

    public QPost(Path<? extends Post> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPost(PathMetadata metadata, PathInits inits) {
        this(Post.class, metadata, inits);
    }

    public QPost(Class<? extends Post> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new com.jiwon.mylog.domain.category.entity.QCategory(forProperty("category"), inits.get("category")) : null;
        this.user = inits.isInitialized("user") ? new com.jiwon.mylog.domain.user.entity.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

