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
import org.everit.jira.querydsl.schema.QCwdUser;
import org.everit.jira.querydsl.support.QuerydslSupport;
import org.everit.jira.querydsl.support.ri.QuerydslSupportImpl;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
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

    ApplicationUser loggedInUser =
        ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();

    if (loggedInUser == null) {
      return false;
    }

    String username = loggedInUser.getUsername();

    return querydslSupport.execute((connection, configuration) -> {
      QGlobalPermission globalPermission = QGlobalPermission.globalPermission;
      QCwdGroup cwdGroup = QCwdGroup.cwdGroup;
      QCwdMembership membership = QCwdMembership.cwdMembership;
      QCwdUser cwdUser = QCwdUser.cwdUser;
      Long count = new SQLQuery<>(connection, configuration)
          .select(globalPermission.globalPermissionId.count())
          .from(globalPermission)
          .innerJoin(cwdGroup).on(cwdGroup.id.eq(globalPermission.groupId))
          .innerJoin(membership).on(membership.parentId.eq(cwdGroup.id))
          .innerJoin(cwdUser).on(cwdUser.id.eq(membership.childId))
          .where(globalPermission.permissionKey.eq(permissionKey)
              .and(cwdUser.userName.eq(username)))
          .fetchOne();

      return count > 0;
    });
  }
}
