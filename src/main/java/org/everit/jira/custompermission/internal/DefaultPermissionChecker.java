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
package org.everit.jira.custompermission.internal;

import org.everit.jira.custompermission.PermissionChecker;
import org.everit.jira.custompermission.schema.qdsl.QGlobalPermission;
import org.everit.jira.querydsl.schema.QCwdGroup;
import org.everit.jira.querydsl.schema.QCwdMembership;
import org.everit.jira.querydsl.support.QuerydslSupport;
import org.everit.jira.querydsl.support.ri.QuerydslSupportImpl;

import com.atlassian.jira.component.ComponentAccessor;
import com.querydsl.sql.SQLQuery;

public class DefaultPermissionChecker implements PermissionChecker {

  private final QuerydslSupport querydslSupport;

  public DefaultPermissionChecker() {
    try {
      this.querydslSupport = new QuerydslSupportImpl();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private boolean hasAnyonePermission(final String permissionKey) {
    return querydslSupport.execute((connection, configuration) -> {
      QGlobalPermission globalPermission = QGlobalPermission.globalPermission;
      long count = new SQLQuery<>(connection, configuration)
          .select(globalPermission.globalPermissionId.count()).from(globalPermission).where(
              globalPermission.permissionKey.eq(permissionKey)
                  .and(globalPermission.groupId.isNull()))
          .fetchOne();
      return count > 0;
    });
  }

  @Override
  public boolean hasGlobalPermission(final String permissionKey) {

    if (hasAnyonePermission(permissionKey)) {
      return true;
    }

    Long loggedInUserId =
        ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser().getId();

    if (loggedInUserId == null) {
      return false;
    }

    return querydslSupport.execute((connection, configuration) -> {
      QGlobalPermission globalPermission = QGlobalPermission.globalPermission;
      QCwdGroup cwdGroup = QCwdGroup.cwdGroup;
      QCwdMembership membership = QCwdMembership.cwdMembership;
      Long count = new SQLQuery<>(connection, configuration)
          .select(globalPermission.globalPermissionId.count())
          .from(globalPermission)
          .innerJoin(cwdGroup).on(cwdGroup.id.eq(globalPermission.groupId))
          .innerJoin(membership).on(membership.parentId.eq(cwdGroup.id))
          .where(globalPermission.permissionKey.eq(permissionKey)
              .and(membership.childId.eq(loggedInUserId)))
          .fetchOne();

      return count > 0;
    });
  }
}
