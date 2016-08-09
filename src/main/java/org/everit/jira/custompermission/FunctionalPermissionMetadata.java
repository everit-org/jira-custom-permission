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

public class ImmutablePermissionMetadata implements PermissionMetadata {

  private final String description;

  private final String key;

  private final String label;

  public ImmutablePermissionMetadata(final String key, final String label, final String description) {
    this.key = key;
    this.label = label;
    this.description = description;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public String getLabel() {
    return label;
  }
}
