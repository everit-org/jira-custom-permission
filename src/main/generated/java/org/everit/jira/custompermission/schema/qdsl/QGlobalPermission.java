/*
 * Copyright (C) 2011 Everit Kft. (http://www.everit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.everit.jira.custompermission.schema.qdsl;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QGlobalPermission is a Querydsl query type for QGlobalPermission
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QGlobalPermission extends com.querydsl.sql.RelationalPathBase<QGlobalPermission> {

    private static final long serialVersionUID = 279596261;

    public static final QGlobalPermission globalPermission = new QGlobalPermission("everit_jira_global_permission");

    public class PrimaryKeys {

        public final com.querydsl.sql.PrimaryKey<QGlobalPermission> globalPermissionPK = createPrimaryKey(globalPermissionId);

    }

    public final NumberPath<Long> globalPermissionId = createNumber("globalPermissionId", Long.class);

    public final NumberPath<Long> groupId = createNumber("groupId", Long.class);

    public final StringPath permissionKey = createString("permissionKey");

    public final PrimaryKeys pk = new PrimaryKeys();

    public QGlobalPermission(String variable) {
        super(QGlobalPermission.class, forVariable(variable), "public", "everit_jira_global_permission");
        addMetadata();
    }

    public QGlobalPermission(String variable, String schema, String table) {
        super(QGlobalPermission.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QGlobalPermission(Path<? extends QGlobalPermission> path) {
        super(path.getType(), path.getMetadata(), "public", "everit_jira_global_permission");
        addMetadata();
    }

    public QGlobalPermission(PathMetadata metadata) {
        super(QGlobalPermission.class, metadata, "public", "everit_jira_global_permission");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(globalPermissionId, ColumnMetadata.named("global_permission_id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(groupId, ColumnMetadata.named("group_id").withIndex(3).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(permissionKey, ColumnMetadata.named("permission_key").withIndex(2).ofType(Types.VARCHAR).withSize(255).notNull());
    }

}

