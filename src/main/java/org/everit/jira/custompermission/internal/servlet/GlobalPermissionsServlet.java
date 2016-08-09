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
package org.everit.jira.custompermission.internal.servlet;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.everit.jira.custompermission.PermissionMetadata;
import org.everit.jira.custompermission.PermissionMetadataProvider;
import org.everit.jira.custompermission.internal.JiraCustomPermissionPluginActivator;
import org.everit.jira.custompermission.internal.component.AlertComponent;
import org.everit.jira.custompermission.internal.component.AreYouSureDialogComponent;
import org.everit.jira.custompermission.schema.qdsl.QGlobalPermission;
import org.everit.jira.querydsl.schema.QCwdGroup;
import org.everit.jira.querydsl.support.QuerydslSupport;
import org.everit.jira.querydsl.support.ri.QuerydslSupportImpl;
import org.everit.web.partialresponse.PartialResponseBuilder;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.dml.SQLDeleteClause;
import com.querydsl.sql.dml.SQLInsertClause;

public class GlobalPermissionsServlet extends AbstractPageServlet {

  public static class GlobalPermissionDTO {
    public Long groupId;

    public String permissionKey;
  }

  public static class JiraGroupDTO {
    public Long groupId;

    public String groupName;
  };

  public static final String GROUP_NAME_ANYONE = "Anyone";

  private static final Comparator<JiraGroupDTO> JIRA_GROUP_COMPARATOR = (o1, o2) -> {
    int result = o1.groupName.compareTo(o2.groupName);
    if (result != 0) {
      return result;
    }
    if (o1.groupId == null && o2.groupId == null) {
      return 0;
    } else if (o1.groupId == null) {
      return -1;
    } else if (o2.groupId == null) {
      return 1;
    }
    return o1.groupId.compareTo(o2.groupId);
  };

  private static final long serialVersionUID = -3629823886527321803L;

  private final QuerydslSupport querydslSupport;

  public GlobalPermissionsServlet() {
    try {
      this.querydslSupport = new QuerydslSupportImpl();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void assign(final String permissionKey, final Long groupId) {
    querydslSupport.execute((connection, configuration) -> {
      QGlobalPermission globalPermission = QGlobalPermission.globalPermission;
      SQLInsertClause insert = new SQLInsertClause(connection, configuration, globalPermission)
          .set(globalPermission.permissionKey, permissionKey);

      if (groupId != null) {
        insert.set(globalPermission.groupId, groupId);
      }

      insert.execute();
      return null;
    });
  }

  private Map<Long, JiraGroupDTO> createJiraGroupByIdMap(final List<JiraGroupDTO> jiraGroups) {
    Map<Long, JiraGroupDTO> result = new HashMap<>();
    for (JiraGroupDTO jiraGroupDTO : jiraGroups) {
      result.put(jiraGroupDTO.groupId, jiraGroupDTO);
    }

    JiraGroupDTO anyOneGroup = new JiraGroupDTO();
    anyOneGroup.groupName = GROUP_NAME_ANYONE;
    result.put(null, anyOneGroup);
    return result;
  }

  private Map<String, PermissionMetadata> createPermissionMetadataByKeyMap(
      final Collection<PermissionMetadata> permissionMetadataCollection) {

    Map<String, PermissionMetadata> result = new HashMap<>();
    for (PermissionMetadata permissionMetadata : permissionMetadataCollection) {
      result.put(permissionMetadata.getKey(), permissionMetadata);
    }
    return result;
  }

  private Predicate createPredicateForEntry(final QGlobalPermission globalPermission,
      final String permissionKey, final Long groupId) {

    if (groupId == null) {
      return globalPermission.permissionKey.eq(permissionKey)
          .and(globalPermission.groupId.isNull());
    }

    return globalPermission.permissionKey.eq(permissionKey)
        .and(globalPermission.groupId.eq(groupId));

  }

  @Override
  protected void doGetInternal(final HttpServletRequest req, final HttpServletResponse resp,
      final Map<String, Object> vars) throws ServletException, IOException {

    Locale locale = resp.getLocale();
    Writer writer = resp.getWriter();

    renderContent(writer, vars, locale, null);
  }

  @Override
  protected void doPostInternal(final HttpServletRequest req, final HttpServletResponse resp)
      throws ServletException, IOException {

    String action = req.getParameter("action");

    switch (action) {
      case "assign":
        processAssign(req, resp);
        break;
      case "remove":
        processRemove(req, resp);
        break;
      default:
        break;
    }
  }

  private List<JiraGroupDTO> getJiraGroups() {
    return querydslSupport.execute((connection, configuration) -> {
      QCwdGroup cwdGroup = QCwdGroup.cwdGroup;
      return new SQLQuery<JiraGroupDTO>(connection, configuration)
          .select(Projections.fields(JiraGroupDTO.class, cwdGroup.id.as("groupId"),
              cwdGroup.groupName))
          .from(cwdGroup)
          .orderBy(cwdGroup.groupName.asc()).fetch();
    });
  }

  private Collection<PermissionMetadata> getPermissionMetadataCollection(final Locale locale) {
    PermissionMetadataProvider[] permissionMetadataProviders =
        JiraCustomPermissionPluginActivator.getPermissionMetadataProviders();

    Set<PermissionMetadata> result = new TreeSet<>(new PermissionMetadataComparator(locale));

    for (PermissionMetadataProvider permissionMetadataProvider : permissionMetadataProviders) {
      result.addAll(permissionMetadataProvider.getGlobalPermissionMetadataCollection());
    }
    return result;
  }

  @Override
  protected String getTemplateBase() {
    return "/META-INF/pages/global_permissions";
  }

  private boolean isAlreadyAssigned(final String permissionKey, final Long groupId) {
    return querydslSupport.execute((connection, configuration) -> {
      QGlobalPermission globalPermission = QGlobalPermission.globalPermission;
      return new SQLQuery<>(connection, configuration)
          .select(globalPermission.globalPermissionId.count())
          .from(globalPermission)
          .where(createPredicateForEntry(globalPermission, permissionKey, groupId))
          .fetchOne() > 0;
    });
  }

  @Override
  protected boolean isWebSudoNecessary() {
    return true;
  }

  private void processAssign(final HttpServletRequest req, final HttpServletResponse resp)
      throws IOException {
    String permissionKey = req.getParameter("permission");
    String groupIdParam = req.getParameter("group");

    Long groupId =
        (GROUP_NAME_ANYONE.equalsIgnoreCase(groupIdParam)) ? null : Long.valueOf(groupIdParam);

    Map<String, Object> vars = createCommonVars(req, resp);
    try (PartialResponseBuilder prb = new PartialResponseBuilder(resp)) {
      if (isAlreadyAssigned(permissionKey, groupId)) {
        renderAlertOnPrb("Group is already assigned to permission", "warning", prb,
            resp.getLocale());
      } else {
        assign(permissionKey, groupId);

        renderAlertOnPrb("Permission assigned", "info", prb, resp.getLocale());
      }

      prb.replace("#global-permission-content",
          (writer) -> renderContent(writer, vars, resp.getLocale(), "global-permission-content"));
    }
  }

  private void processRemove(final HttpServletRequest req, final HttpServletResponse resp)
      throws IOException {

    String permissionKey = req.getParameter("permission");
    String groupIdParam = req.getParameter("group");

    Long groupId =
        (GROUP_NAME_ANYONE.equalsIgnoreCase(groupIdParam)) ? null : Long.valueOf(groupIdParam);

    remove(permissionKey, groupId);

    Map<String, Object> vars = createCommonVars(req, resp);
    try (PartialResponseBuilder prb = new PartialResponseBuilder(resp)) {
      renderAlertOnPrb("Permission deleted", "info", prb, resp.getLocale());

      prb.replace("#global-permission-content",
          (writer) -> renderContent(writer, vars, resp.getLocale(), "global-permission-content"));
    }

  }

  private void remove(final String permissionKey, final Long groupId) {
    querydslSupport.execute((connection, configuration) -> {
      QGlobalPermission globalPermission = QGlobalPermission.globalPermission;
      new SQLDeleteClause(connection, configuration, globalPermission)
          .where(createPredicateForEntry(globalPermission, permissionKey, groupId)).execute();
      return null;
    });
  }

  private void renderAlertOnPrb(final String message, final String alertType,
      final PartialResponseBuilder prb, final Locale locale) {

    prb.append("#aui-message-bar",
        (writer) -> AlertComponent.INSTANCE.render(writer, message, alertType, locale));
  }

  private void renderContent(final Writer writer, final Map<String, Object> vars,
      final Locale locale,
      final String fragment) {

    vars.put("areYouSureDialogComponent", AreYouSureDialogComponent.INSTANCE);

    List<JiraGroupDTO> jiraGroups = getJiraGroups();
    vars.put("selectableJiraGroups", jiraGroups);

    Collection<PermissionMetadata> permissionMetadataCollection =
        getPermissionMetadataCollection(locale);
    vars.put("permissionMetadataCollection", permissionMetadataCollection);

    Map<PermissionMetadata, Collection<JiraGroupDTO>> permissionEntryMap =
        resolvePermissionEntries(permissionMetadataCollection, jiraGroups, locale);

    vars.put("permissionEntryMap", permissionEntryMap);

    pageTemplate.render(writer, vars, locale, fragment);
  }

  private Map<PermissionMetadata, Collection<JiraGroupDTO>> resolvePermissionEntries(
      final Collection<PermissionMetadata> permissionMetadataCollection,
      final List<JiraGroupDTO> jiraGroups, final Locale locale) {

    Map<Long, JiraGroupDTO> jiraGroupByIdMap = createJiraGroupByIdMap(jiraGroups);
    Map<String, PermissionMetadata> permissionMetadataByKeyMap =
        createPermissionMetadataByKeyMap(permissionMetadataCollection);

    List<GlobalPermissionDTO> resultSet = querydslSupport.execute((connection, configuration) -> {
      QGlobalPermission globalPermission = QGlobalPermission.globalPermission;
      return new SQLQuery<>(connection, configuration)
          .select(Projections.fields(GlobalPermissionDTO.class,
              globalPermission.groupId, globalPermission.permissionKey))
          .from(globalPermission)
          .fetch();
    });

    Map<PermissionMetadata, Collection<JiraGroupDTO>> result =
        new TreeMap<>(new PermissionMetadataComparator(locale));

    // Add all known permissions with empty collection

    for (PermissionMetadata permissionMetadata : permissionMetadataCollection) {
      result.put(permissionMetadata, new TreeSet<>(JIRA_GROUP_COMPARATOR));
    }

    // Add data from database
    for (GlobalPermissionDTO globalPermissionDTO : resultSet) {
      PermissionMetadata permissionMetadata =
          permissionMetadataByKeyMap.get(globalPermissionDTO.permissionKey);

      if (permissionMetadata == null) {
        permissionMetadata = new UndefinedPermissionMetadata(globalPermissionDTO.permissionKey);
        permissionMetadataByKeyMap.put(globalPermissionDTO.permissionKey, permissionMetadata);
      }

      Collection<JiraGroupDTO> jiraGroupCollection = result.get(permissionMetadata);
      if (jiraGroupCollection == null) {
        jiraGroupCollection = new TreeSet<>(JIRA_GROUP_COMPARATOR);
        result.put(permissionMetadata, jiraGroupCollection);
      }
      jiraGroupCollection.add(jiraGroupByIdMap.get(globalPermissionDTO.groupId));
    }
    return result;
  }
}
