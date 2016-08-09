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

import java.util.Comparator;
import java.util.Locale;

import org.everit.jira.custompermission.PermissionMetadata;

public class PermissionMetadataComparator implements Comparator<PermissionMetadata> {

  private final Locale locale;

  public PermissionMetadataComparator(final Locale locale) {
    this.locale = locale;
  }

  @Override
  public int compare(final PermissionMetadata o1, final PermissionMetadata o2) {
    int result = o1.getLabel(locale).compareTo(o2.getLabel(locale));
    if (result != 0) {
      return result;
    }

    return o1.getKey().compareTo(o2.getKey());
  }

}
