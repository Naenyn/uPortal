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
package org.apereo.portal.portlet.container.stubs;

/**
 * Stub implementation of Portlet 3.0 HeaderData for compatibility.
 * This class provides minimal functionality to satisfy interface contracts
 * while maintaining Portlet 2.0 behavior.
 */
public class HeaderDataStub {
    
    /**
     * Private constructor - instances should only be created through factory methods
     */
    private HeaderDataStub() {
        // Empty stub for compatibility
    }
    
    /**
     * Factory method to create a null-equivalent HeaderData stub
     */
    public static HeaderDataStub createNull() {
        return null; // Return null for Portlet 2.0 compatibility
    }
}