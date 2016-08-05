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
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.everit.jira.querydsl.support.QuerydslSupport;
import org.everit.jira.querydsl.support.ri.QuerydslSupportImpl;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.websudo.WebSudoManager;

/**
 * Abstract servlet that others can inherit from to implement a page.
 */
public abstract class AbstractPageServlet extends HttpServlet {

  private static final long serialVersionUID = -3661512830445457663L;

  protected final LocalizedTemplate pageTemplate;

  protected final QuerydslSupport querydslSupport;

  protected final TransactionTemplate transactionTemplate;

  /**
   * Initializes the compiled page template.
   */
  public AbstractPageServlet() {
    try {
      querydslSupport = new QuerydslSupportImpl();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    transactionTemplate =
        ComponentAccessor.getOSGiComponentInstanceOfType(TransactionTemplate.class);

    ClassLoader classLoader = this.getClass().getClassLoader();
    pageTemplate = new LocalizedTemplate(getTemplateBase(), classLoader);
  }

  protected boolean checkWebSudo(final HttpServletRequest req, final HttpServletResponse resp) {
    if (!isWebSudoNecessary()) {
      return true;
    }

    WebSudoManager webSudoManager =
        ComponentAccessor.getOSGiComponentInstanceOfType(WebSudoManager.class);

    if (!webSudoManager.canExecuteRequest(req)) {
      if ("XMLHttpRequest".equals(req.getHeader("X-Requested-With"))) {
        webSudoManager.enforceWebSudoProtection(req, new AjaxRedirectCatchServletResponse(resp));
      } else {
        webSudoManager.enforceWebSudoProtection(req, resp);
      }
      return false;
    }
    return true;
  }

  protected Map<String, Object> createCommonVars(final HttpServletRequest req,
      final HttpServletResponse resp) throws IOException {
    Map<String, Object> vars = new HashMap<String, Object>();
    vars.put("webResourceManager", ComponentAccessor.getWebResourceManager());
    vars.put("request", req);
    vars.put("response", resp);
    return vars;
  }

  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
      throws ServletException, IOException {

    if (!checkWebSudo(req, resp)) {
      return;
    }

    Map<String, Object> vars = createCommonVars(req, resp);
    doGetInternal(req, resp, vars);
  }

  protected void doGetInternal(final HttpServletRequest req, final HttpServletResponse resp,
      final Map<String, Object> vars) throws ServletException, IOException {

    pageTemplate.render(resp.getWriter(), vars, resp.getLocale(), null);
  }

  @Override
  protected final void doPost(final HttpServletRequest req, final HttpServletResponse resp)
      throws ServletException, IOException {

    if (!checkWebSudo(req, resp)) {
      return;
    }

    doPostInternal(req, resp);
  }

  protected void doPostInternal(final HttpServletRequest req, final HttpServletResponse resp)
      throws ServletException, IOException {
    super.doPost(req, resp);
  }

  protected abstract String getTemplateBase();

  protected boolean isWebSudoNecessary() {
    return false;
  }
}
