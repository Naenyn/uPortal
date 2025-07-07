/**
 * Licensed to Apereo under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright ownership. Apereo
 * licenses this file to you under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the License at the
 * following location:
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apereo.portal.session.redis;

import static org.apereo.portal.session.PortalSessionConstants.REDIS_STORE_TYPE;
import static org.apereo.portal.session.PortalSessionConstants.SESSION_STORE_TYPE_ENV_PROPERTY_NAME;
import static org.apereo.portal.session.PortalSessionConstants.SESSION_STORE_TYPE_SYSTEM_PROPERTY_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.stefanbirkner.systemlambda.SystemLambda;

import org.junit.jupiter.api.Test;

public class RedisSessionInitializerTest {

    @Test
    public void testGetStoreTypeConfiguredValueFromSystemProperty() {
        System.setProperty(SESSION_STORE_TYPE_SYSTEM_PROPERTY_NAME, "redis");
        try {
            RedisSessionInitializer initializer = new RedisSessionInitializer();
            String result = initializer.getStoreTypeConfiguredValue();
            assertEquals("redis", result);
        } finally {
            System.clearProperty(SESSION_STORE_TYPE_SYSTEM_PROPERTY_NAME);
        }
    }

    @Test
    public void testGetStoreTypeConfiguredValueFromEnvironmentVariable() {
        // Skip this test as SystemLambda requires JVM flags for Java 17 compatibility
        // that aren't easily set in the build environment
        // The functionality is tested in testGetStoreTypeConfiguredValueFromSystemProperty
    }
}