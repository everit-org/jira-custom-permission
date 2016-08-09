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
package org.everit.jira.custompermission;

import java.util.Locale;
import java.util.function.Function;

public class FunctionalPermissionMetadata implements PermissionMetadata {

  private final Function<Locale, String> descriptionProvider;

  private final String key;

  private final Function<Locale, String> labelProvider;

  public FunctionalPermissionMetadata(final String key,
      final Function<Locale, String> labelProvider,
      final Function<Locale, String> descriptionProvider) {
    this.key = key;
    this.labelProvider = labelProvider;
    this.descriptionProvider = descriptionProvider;
  }

  @Override
  public String getDescription(final Locale locale) {
    return descriptionProvider.apply(locale);
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public String getLabel(final Locale locale) {
    return labelProvider.apply(locale);
  }
}
