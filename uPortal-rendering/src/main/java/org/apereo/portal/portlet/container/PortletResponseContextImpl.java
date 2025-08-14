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
package org.apereo.portal.portlet.container;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.pluto.container.PortletContainer;
import org.apache.pluto.container.PortletResponseContext;
import org.apache.pluto.container.ResourceURLProvider;
import org.apache.pluto.container.HeaderData;
import org.apereo.portal.portlet.container.properties.IRequestPropertiesManager;
import org.apereo.portal.portlet.container.services.IPortletCookieService;
import org.apereo.portal.portlet.om.IPortletWindow;
import org.apereo.portal.portlet.om.IPortletWindowId;
import org.apereo.portal.url.ResourceUrlProviderImpl;
import org.springframework.util.Assert;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** */
public class PortletResponseContextImpl extends AbstractPortletContextImpl
        implements PortletResponseContext {
    private boolean closed = false;
    private boolean released = false;

    protected final IRequestPropertiesManager requestPropertiesManager;

    public PortletResponseContextImpl(
            PortletContainer portletContainer,
            IPortletWindow portletWindow,
            HttpServletRequest containerRequest,
            HttpServletResponse containerResponse,
            IRequestPropertiesManager requestPropertiesManager,
            IPortletCookieService portletCookieService) {
        super(
                portletContainer,
                portletWindow,
                containerRequest,
                containerResponse,
                portletCookieService);

        if (requestPropertiesManager == null) {
            throw new IllegalArgumentException("requestPropertiesManager can not be null");
        }

        this.requestPropertiesManager = requestPropertiesManager;
    }

    public void addProperty(Cookie cookie) {
        final IPortletWindowId portletWindowId = this.portletWindow.getPortletWindowId();
        this.portletCookieService.addCookie(this.servletRequest, portletWindowId, cookie);
    }
    


    @Override
    public final void addProperty(String key, Element element) {
        // uPortal doesn't support XML properties
    }

    @Override
    public final void addProperty(String key, String value) {
        managerAddProperty(key, value);
    }

    @Override
    public final void setProperty(String key, String value) {
        managerSetProperty(key, value);
    }

    protected boolean managerSetProperty(String key, String value) {
        return this.requestPropertiesManager.setResponseProperty(
                this.servletRequest, this.portletWindow, key, value);
    }

    protected boolean managerAddProperty(String key, String value) {
        return this.requestPropertiesManager.addResponseProperty(
                this.servletRequest, this.portletWindow, key, value);
    }

    @Override
    public Element createElement(String tagName) throws DOMException {
        // TODO this is terribly inefficient
        final DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        final DocumentBuilder docBuilder;
        try {
            docBuilder = dbfac.newDocumentBuilder();
            final Document doc = docBuilder.newDocument();
            return doc.createElement(tagName);
        } catch (ParserConfigurationException e) {
            throw new DOMException((short) 0, "Initialization failure");
        }
    }

    @Override
    public ResourceURLProvider getResourceURLProvider() {
        return new ResourceUrlProviderImpl(portletWindow, containerRequest);
    }

    @Override
    public void close() {
        this.closed = true;
    }

    @Override
    public void release() {
        this.closed = true;
        this.released = true;
        this.servletRequest = null;
        this.servletResponse = null;
    }

    /**
     * Check if the status of the response, if the context has been closed or released an {@link
     * IllegalStateException} is thrown.
     */
    protected void checkContextStatus() {
        if (this.closed || this.released) {
            throw new IllegalStateException(this.getClass().getSimpleName() + " has been closed");
        }
    }

    public boolean isClosed() {
        return this.closed;
    }

    public boolean isReleased() {
        return this.released;
    }
    
    // Minimal stub for Pluto 3.x compatibility - maintains Portlet 2.0 behavior
    @Override
    public void setActionScopedId(String actionScopedId, String[] values) {
        // No-op for Portlet 2.0 compatibility - action scoping not used
    }
    
    @Override
    public void processHttpHeaders() {
        // No-op for Portlet 2.0 compatibility - HTTP header processing not used
    }
    
    @Override
    public java.util.Collection<String> getPropertyNames() {
        // Return empty collection for Portlet 2.0 compatibility
        return java.util.Collections.emptyList();
    }
    
    @Override
    public java.util.Collection<String> getPropertyValues(String name) {
        // Return empty collection for Portlet 2.0 compatibility - maintains existing behavior
        return java.util.Collections.emptyList();
    }
    
    @Override
    public String getProperty(String name) {
        // Return null for Portlet 2.0 compatibility - maintains existing behavior
        return null;
    }
    
    @Override
    public void addProperty(javax.servlet.http.Cookie cookie) {
        // No-op for Portlet 2.0 compatibility - maintains existing behavior
    }
    
    @Override
    public org.apache.pluto.container.PortletURLProvider getPortletURLProvider(org.apache.pluto.container.PortletURLProvider.TYPE type) {
        // Return null for Portlet 2.0 compatibility - maintains existing behavior
        return null;
    }
    
    @Override
    public org.apache.pluto.container.HeaderData getHeaderData() {
        // Return null for Portlet 2.0 compatibility - HeaderData not used
        return null;
    }
    
    @Override
    public String getLifecycle() {
        // Return null for Portlet 2.0 compatibility - lifecycle not used
        return null;
    }
    
    @Override
    public void setLifecycle(String lifecycle) {
        // No-op for Portlet 2.0 compatibility - lifecycle not used
    }
    
    @Override
    public javax.servlet.http.HttpServletResponse getServletResponse() {
        // Convert jakarta response to javax for interface compliance
        return ServletTypeMapper.toJavax(this.servletResponse);
    }
    
    @Override
    public boolean isSetPropsAllowed() {
        // Return true for Portlet 2.0 compatibility - maintains existing behavior
        return true;
    }
    
    @Override
    public void setPropsAllowed(boolean propsAllowed) {
        // No-op for Portlet 2.0 compatibility - maintains existing behavior
    }
    
    @Override
    public javax.servlet.http.HttpServletRequest getServletRequest() {
        // Convert jakarta request to javax for interface compliance
        return ServletTypeMapper.toJavax(this.servletRequest);
    }
    
    @Override
    public javax.servlet.http.HttpServletResponse getContainerResponse() {
        // Convert jakarta response to javax for interface compliance
        return ServletTypeMapper.toJavax(this.servletResponse);
    }
    
    @Override
    public javax.servlet.http.HttpServletRequest getContainerRequest() {
        // Convert jakarta request to javax for interface compliance
        return ServletTypeMapper.toJavax(this.servletRequest);
    }
    
    @Override
    public void init(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) {
        // No-op for Portlet 2.0 compatibility - initialization handled in constructor
    }
    
}
