package com.jiwon.mylog.domain.role;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRole is a Querydsl query type for Role
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRole extends EntityPathBase<Role> {

    private static final long serialVersionUID = -516471394L;

    public static final QRole role = new QRole("role");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final ListPath<RolePermission, QRolePermission> rolePermissions = this.<RolePermission, QRolePermission>createList("rolePermissions", RolePermission.class, QRolePermission.class, PathInits.DIRECT2);

    public final ListPath<com.jiwon.mylog.domain.user.entity.UserRole, com.jiwon.mylog.domain.user.entity.QUserRole> userRoles = this.<com.jiwon.mylog.domain.user.entity.UserRole, com.jiwon.mylog.domain.user.entity.QUserRole>createList("userRoles", com.jiwon.mylog.domain.user.entity.UserRole.class, com.jiwon.mylog.domain.user.entity.QUserRole.class, PathInits.DIRECT2);

    public QRole(String variable) {
        super(Role.class, forVariable(variable));
    }

    public QRole(Path<? extends Role> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRole(PathMetadata metadata) {
        super(Role.class, metadata);
    }

}

