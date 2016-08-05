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

import java.util.Collection;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * <pre>
 *  <b>IMPORTANT:</b> the implementation is read only!
 * </pre>
 *
 * {@link Map} implementation wraps a {@link ResourceBundle} and provides its
 * {@link ResourceBundle#getString(String)} functionality via the {@link Map#get(Object)} function.
 */
public class ResourceBundleMap implements Map<String, String> {

  private final ResourceBundle wrapped;

  public ResourceBundleMap(final ResourceBundle wrapped) {
    this.wrapped = wrapped;
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean containsKey(final Object key) {
    return wrapped.containsKey(String.valueOf(key));
  }

  @Override
  public boolean containsValue(final Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Set<Entry<String, String>> entrySet() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String get(final Object key) {
    String stringKey = String.valueOf(key);
    if (!wrapped.containsKey(stringKey)) {
      return "??" + stringKey + "??";
    }
    return wrapped.getString(stringKey);
  }

  @Override
  public boolean isEmpty() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Set<String> keySet() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String put(final String key, final String value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void putAll(final Map<? extends String, ? extends String> m) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String remove(final Object key) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int size() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Collection<String> values() {
    throw new UnsupportedOperationException();
  }

}
