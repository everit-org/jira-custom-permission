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

import java.sql.Connection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.everit.jira.custompermission.PermissionChecker;
import org.everit.jira.custompermission.PermissionMetadataProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

import com.atlassian.jira.ofbiz.DefaultOfBizConnectionFactory;

import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.osgi.OSGiResourceAccessor;

public class JiraCustomPermissionPluginActivator implements BundleActivator {

  private static ServiceTracker<PermissionMetadataProvider, PermissionMetadataProvider> permissionMetadataProviderTracker;

  private static ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

  public static PermissionMetadataProvider[] getPermissionMetadataProviders() {
    ReadLock readLock = readWriteLock.readLock();
    readLock.lock();
    try {
      if (permissionMetadataProviderTracker != null) {
        return permissionMetadataProviderTracker.getServices(new PermissionMetadataProvider[0]);
      } else {
        return new PermissionMetadataProvider[0];
      }
    } finally {
      readLock.unlock();
    }
  }

  private ServiceRegistration<PermissionChecker> serviceRegistration;

  @Override
  public void start(final BundleContext context) throws Exception {
    try (Connection connection = DefaultOfBizConnectionFactory.getInstance().getConnection()) {
      DatabaseConnection databaseConnection = new JdbcConnection(connection);
      Liquibase liquibase =
          new Liquibase("META-INF/liquibase/org.everit.jira.custompermission.changelog.xml",
              new OSGiResourceAccessor(context.getBundle()), databaseConnection);

      liquibase.update("production");
    }

    WriteLock writeLock = readWriteLock.writeLock();
    writeLock.lock();
    try {
      permissionMetadataProviderTracker =
          new ServiceTracker<>(context, PermissionMetadataProvider.class, null);

      permissionMetadataProviderTracker.open();
    } finally {
      writeLock.unlock();
    }

    Dictionary<String, Object> properties = new Hashtable<>();
    serviceRegistration = context.registerService(PermissionChecker.class,
        new DefaultPermissionChecker(), properties);
  }

  @Override
  public void stop(final BundleContext context) throws Exception {
    serviceRegistration.unregister();
    WriteLock writeLock = readWriteLock.writeLock();
    writeLock.lock();
    try {
      permissionMetadataProviderTracker.close();
      permissionMetadataProviderTracker = null;
    } finally {
      writeLock.unlock();
    }
  }

}
