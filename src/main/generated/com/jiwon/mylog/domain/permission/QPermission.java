package com.jiwon.mylog.domain.permission;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPermission is a Querydsl query type for Permission
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPermission extends EntityPathBase<Permission> {

    private static final long serialVersionUID = 1118435838L;

    public static final QPermission permission = new QPermission("permission");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final ListPath<com.jiwon.mylog.domain.role.RolePermission, com.jiwon.mylog.domain.role.QRolePermission> rolePermissions = this.<com.jiwon.mylog.domain.role.RolePermission, com.jiwon.mylog.domain.role.QRolePermission>createList("rolePermissions", com.jiwon.mylog.domain.role.RolePermission.class, com.jiwon.mylog.domain.role.QRolePermission.class, PathInits.DIRECT2);

    public QPermission(String variable) {
        super(Permission.class, forVariable(variable));
    }

    public QPermission(Path<? extends Permission> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPermission(PathMetadata metadata) {
        super(Permission.class, metadata);
    }

}

