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

import org.everit.jira.custompermission.PermissionMetadataProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class JiraCustomPermissionPluginActivator implements BundleActivator {

  private ServiceTracker<PermissionMetadataProvider, PermissionMetadataProvider> permissionMetadataProviderTracker;

  @Override
  public void start(final BundleContext context) throws Exception {
    permissionMetadataProviderTracker =
        new ServiceTracker<>(context, PermissionMetadataProvider.class, null);

    permissionMetadataProviderTracker.open();

  }

  @Override
  public void stop(final BundleContext context) throws Exception {
    permissionMetadataProviderTracker.close();
  }

}
