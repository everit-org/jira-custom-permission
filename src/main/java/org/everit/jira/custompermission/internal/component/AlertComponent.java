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
package org.everit.jira.custompermission.internal.component;

import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.everit.jira.custompermission.internal.servlet.LocalizedTemplate;

public class AlertComponent {

  public static final AlertComponent INSTANCE;

  private static final LocalizedTemplate TEMPLATE_ALERT;

  private static final String TEMPLATE_ALERT_BASENAME = "META-INF/component/alert";

  static {
    ClassLoader classLoader = AlertComponent.class.getClassLoader();
    TEMPLATE_ALERT = new LocalizedTemplate(TEMPLATE_ALERT_BASENAME, classLoader);
    INSTANCE = new AlertComponent();
  }

  public void render(final Writer writer, final String message, final String alertType,
      final Locale locale) {
    Map<String, Object> vars = new HashMap<>();
    vars.put("message", message);
    vars.put("alertType", alertType);
    TEMPLATE_ALERT.render(writer, vars, locale, "body");
  }
}
