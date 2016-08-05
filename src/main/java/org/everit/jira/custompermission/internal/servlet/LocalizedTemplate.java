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
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.everit.expression.ExpressionCompiler;
import org.everit.expression.ParserConfiguration;
import org.everit.expression.jexl.JexlExpressionCompiler;
import org.everit.templating.CompiledTemplate;
import org.everit.templating.TemplateCompiler;
import org.everit.templating.html.HTMLTemplateCompiler;
import org.everit.templating.util.InheritantMap;

public class LocalizedTemplate {

  private static final TemplateCompiler TEMPLATE_COMPILER;

  static {
    ExpressionCompiler expressionCompiler = new JexlExpressionCompiler();
    TEMPLATE_COMPILER = new HTMLTemplateCompiler(expressionCompiler);
  }

  private final String baseName;

  private final ClassLoader classLoader;

  private final CompiledTemplate compiledTemplate;

  public LocalizedTemplate(final String baseName, final ClassLoader classLoader) {
    this.baseName = baseName;
    this.classLoader = classLoader;
    try {
      ParserConfiguration parserConfiguration = new ParserConfiguration(classLoader);
      parserConfiguration.setName(baseName + ".html");
      compiledTemplate = TEMPLATE_COMPILER.compile(
          IOUtils.toString(classLoader.getResource(baseName + ".html"), "UTF8"),
          parserConfiguration);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public void render(final Writer writer, final Map<String, Object> vars, final Locale locale,
      final String fragmentId) {

    Map<String, Object> localVars = new InheritantMap<>(vars, false);
    ResourceBundleMap messages =
        TemplatingUtil.getMessages(baseName, classLoader, locale);
    localVars.put("messages", messages);

    compiledTemplate.render(writer, localVars, fragmentId);
  }
}
