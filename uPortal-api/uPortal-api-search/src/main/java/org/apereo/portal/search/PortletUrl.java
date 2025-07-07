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
package org.apereo.portal.search;

import java.util.List;

/**
 * Represents a portlet URL for search functionality
 */
public class PortletUrl {
    private PortletUrlType type;
    private String url;
    private List<PortletUrlParameter> parameters;
    private String portletMode;
    private String windowState;

    public PortletUrl() {}

    public PortletUrl(PortletUrlType type, String url, List<PortletUrlParameter> parameters) {
        this.type = type;
        this.url = url;
        this.parameters = parameters;
    }

    public PortletUrlType getType() {
        return type;
    }

    public void setType(PortletUrlType type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<PortletUrlParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<PortletUrlParameter> parameters) {
        this.parameters = parameters;
    }

    public String getPortletMode() {
        return portletMode;
    }

    public void setPortletMode(String portletMode) {
        this.portletMode = portletMode;
    }

    public String getWindowState() {
        return windowState;
    }

    public void setWindowState(String windowState) {
        this.windowState = windowState;
    }

    public List<PortletUrlParameter> getParam() {
        return parameters;
    }
}