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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.portlet.Event;
import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.pluto.container.EventProvider;
import org.apache.pluto.container.HeaderData;
import org.apache.pluto.container.PortletContainer;
import org.apache.pluto.container.PortletStateAwareResponseContext;
import org.apache.pluto.container.driver.PortletContextService;
import org.apereo.portal.portlet.container.properties.IRequestPropertiesManager;
import org.apereo.portal.portlet.container.services.IPortletCookieService;
import org.apereo.portal.portlet.om.IPortletWindow;
import org.apereo.portal.url.IPortletUrlBuilder;

/** */
public class PortletStateAwareResponseContextImpl extends PortletResponseContextImpl
        implements PortletStateAwareResponseContext {
    private final List<Event> events = new LinkedList<Event>();
    protected final IPortletUrlBuilder portletUrlBuilder;
    protected final PortletContextService portletContextService;

    public PortletStateAwareResponseContextImpl(
            PortletContainer portletContainer,
            IPortletWindow portletWindow,
            HttpServletRequest containerRequest,
            HttpServletResponse containerResponse,
            IRequestPropertiesManager requestPropertiesManager,
            IPortletUrlBuilder portletUrlBuilder,
            PortletContextService portletContextService,
            IPortletCookieService portletCookieService) {

        super(
                portletContainer,
                portletWindow,
                containerRequest,
                containerResponse,
                requestPropertiesManager,
                portletCookieService);

        this.portletUrlBuilder = portletUrlBuilder;
        this.portletContextService = portletContextService;
        
        if (this.portletUrlBuilder == null) {
            throw new IllegalArgumentException("portletUrlBuilder cannot be null");
        }
        if (this.portletContextService == null) {
            throw new IllegalArgumentException("portletContextService cannot be null");
        }
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletStateAwareResponseContext#getEventProvider()
     */
    @Override
    public EventProvider getEventProvider() {
        return new EventProviderImpl(this.portletWindow, this.portletContextService);
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletStateAwareResponseContext#getEvents()
     */
    @Override
    public List<Event> getEvents() {
        return this.events;
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletStateAwareResponseContext#getPortletMode()
     */
    @Override
    public PortletMode getPortletMode() {
        this.checkContextStatus();
        return this.portletUrlBuilder.getPortletMode();
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletStateAwareResponseContext#getPublicRenderParameters()
     */
    public Map<String, String[]> getPublicRenderParameters() {
        this.checkContextStatus();
        return this.portletUrlBuilder.getPublicRenderParameters();
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletStateAwareResponseContext#getRenderParameters()
     */
    public Map<String, String[]> getRenderParameters() {
        this.checkContextStatus();
        return this.portletUrlBuilder.getParameters();
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletStateAwareResponseContext#getWindowState()
     */
    @Override
    public WindowState getWindowState() {
        this.checkContextStatus();
        return this.portletUrlBuilder.getWindowState();
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletStateAwareResponseContext#setPortletMode(javax.portlet.PortletMode)
     */
    @Override
    public void setPortletMode(PortletMode portletMode) {
        this.checkContextStatus();
        this.portletUrlBuilder.setPortletMode(portletMode);
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletStateAwareResponseContext#setWindowState(javax.portlet.WindowState)
     */
    @Override
    public void setWindowState(WindowState windowState) {
        this.checkContextStatus();
        this.portletUrlBuilder.setWindowState(windowState);
    }
    
    @Override
    public void reset() {
        // No-op for Portlet 2.0 compatibility - maintains existing behavior
    }
    
    @Override
    public javax.portlet.MutableRenderParameters getRenderParameters(String portletMode) {
        // Return null for Portlet 2.0 compatibility - maintains existing behavior
        return null;
    }
    
    @Override
    public void removeParameter(String name, String value) {
        // No-op for Portlet 2.0 compatibility - maintains existing behavior
    }
    
    @Override
    public void setParameter(String name, String value, String[] values) {
        // No-op for Portlet 2.0 compatibility - maintains existing behavior
    }
    
    @Override
    public String[] getParameterValues(String name, String portletMode) {
        // Return null for Portlet 2.0 compatibility - maintains existing behavior
        return null;
    }
    
    @Override
    public java.util.Set<String> getPrivateParameterNames(String portletMode) {
        // Return empty set for Portlet 2.0 compatibility - maintains existing behavior
        return java.util.Collections.emptySet();
    }
    
    @Override
    public boolean isPublicRenderParameter(String name, String portletMode) {
        // Return false for Portlet 2.0 compatibility - maintains existing behavior
        return false;
    }
    
    @Override
    public void removePublicRenderParameter(String name, String portletMode) {
        // No-op for Portlet 2.0 compatibility - maintains existing behavior
    }
    
    @Override
    public void addPublicRenderParameter(String name, String value, String[] values) {
        // No-op for Portlet 2.0 compatibility - maintains existing behavior
    }
    

}
