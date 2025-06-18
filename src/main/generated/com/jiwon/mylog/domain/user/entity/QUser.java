package com.jiwon.mylog.domain.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -1869957917L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser user = new QUser("user");

    public final com.jiwon.mylog.global.common.entity.QBaseEntity _super = new com.jiwon.mylog.global.common.entity.QBaseEntity(this);

    public final StringPath accountId = createString("accountId");

    public final StringPath bio = createString("bio");

    public final ListPath<com.jiwon.mylog.domain.category.entity.Category, com.jiwon.mylog.domain.category.entity.QCategory> categories = this.<com.jiwon.mylog.domain.category.entity.Category, com.jiwon.mylog.domain.category.entity.QCategory>createList("categories", com.jiwon.mylog.domain.category.entity.Category.class, com.jiwon.mylog.domain.category.entity.QCategory.class, PathInits.DIRECT2);

    public final ListPath<com.jiwon.mylog.domain.comment.entity.Comment, com.jiwon.mylog.domain.comment.entity.QComment> comments = this.<com.jiwon.mylog.domain.comment.entity.Comment, com.jiwon.mylog.domain.comment.entity.QComment>createList("comments", com.jiwon.mylog.domain.comment.entity.Comment.class, com.jiwon.mylog.domain.comment.entity.QComment.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath password = createString("password");

    public final ListPath<com.jiwon.mylog.domain.post.entity.Post, com.jiwon.mylog.domain.post.entity.QPost> posts = this.<com.jiwon.mylog.domain.post.entity.Post, com.jiwon.mylog.domain.post.entity.QPost>createList("posts", com.jiwon.mylog.domain.post.entity.Post.class, com.jiwon.mylog.domain.post.entity.QPost.class, PathInits.DIRECT2);

    public final com.jiwon.mylog.domain.image.entity.QProfileImage profileImage;

    public final StringPath provider = createString("provider");

    public final StringPath providerId = createString("providerId");

    public final EnumPath<UserStatus> status = createEnum("status", UserStatus.class);

    public final ListPath<com.jiwon.mylog.domain.tag.entity.Tag, com.jiwon.mylog.domain.tag.entity.QTag> tags = this.<com.jiwon.mylog.domain.tag.entity.Tag, com.jiwon.mylog.domain.tag.entity.QTag>createList("tags", com.jiwon.mylog.domain.tag.entity.Tag.class, com.jiwon.mylog.domain.tag.entity.QTag.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final StringPath username = createString("username");

    public final ListPath<UserRole, QUserRole> userRoles = this.<UserRole, QUserRole>createList("userRoles", UserRole.class, QUserRole.class, PathInits.DIRECT2);

    public QUser(String variable) {
        this(User.class, forVariable(variable), INITS);
    }

    public QUser(Path<? extends User> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser(PathMetadata metadata, PathInits inits) {
        this(User.class, metadata, inits);
    }

    public QUser(Class<? extends User> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.profileImage = inits.isInitialized("profileImage") ? new com.jiwon.mylog.domain.image.entity.QProfileImage(forProperty("profileImage"), inits.get("profileImage")) : null;
    }

}

