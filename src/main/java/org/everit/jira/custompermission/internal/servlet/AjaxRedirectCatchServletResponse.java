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

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class AjaxRedirectCatchServletResponse extends HttpServletResponseWrapper {

  private final HttpServletResponse httpServletResponse;

  public AjaxRedirectCatchServletResponse(final HttpServletResponse response) {
    super(response);
    httpServletResponse = response;
  }

  @Override
  public void sendRedirect(final String location) throws java.io.IOException {
    httpServletResponse.setStatus(278);
    httpServletResponse.setHeader("Location", location);
  };
}
