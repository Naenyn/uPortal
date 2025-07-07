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

import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.container.PortletContainer;
import org.apache.pluto.container.PortletResourceRequestContext;
import org.apereo.portal.portlet.container.properties.IRequestPropertiesManager;
import org.apereo.portal.portlet.container.services.IPortletCookieService;
import org.apereo.portal.portlet.container.services.RequestAttributeService;
import org.apereo.portal.portlet.om.IPortletWindow;
import org.apereo.portal.url.IPortalRequestInfo;

/** */
public class PortletResourceRequestContextImpl extends PortletRequestContextImpl
        implements PortletResourceRequestContext {

    protected final Log logger = LogFactory.getLog(this.getClass());

    public PortletResourceRequestContextImpl(
            PortletContainer portletContainer,
            IPortletWindow portletWindow,
            HttpServletRequest containerRequest,
            HttpServletResponse containerResponse,
            IRequestPropertiesManager requestPropertiesManager,
            IPortalRequestInfo portalRequestInfo,
            IPortletCookieService portletCookieService,
            RequestAttributeService requestAttributeService) {

        super(
                portletContainer,
                portletWindow,
                containerRequest,
                containerResponse,
                requestPropertiesManager,
                portalRequestInfo,
                portletCookieService,
                requestAttributeService);
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletResourceRequestContext#getCacheability()
     */
    @Override
    public String getCacheability() {
        if (this.portletRequestInfo != null) {
            return this.portletRequestInfo.getCacheability();
        }

        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletResourceRequestContext#getPrivateRenderParameterMap()
     */
    @Override
    public Map<String, String[]> getPrivateRenderParameterMap() {
        return this.portletWindow.getRenderParameters();
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletResourceRequestContext#getResourceID()
     */
    @Override
    public String getResourceID() {
        if (this.portletRequestInfo != null) {
            return this.portletRequestInfo.getResourceId();
        }

        return null;
    }
    
    // Minimal stub for Pluto 3.x compatibility - maintains Portlet 2.0 behavior
    @Override
    public void setBeanManager(javax.enterprise.inject.spi.BeanManager beanManager) {
        // No-op for Portlet 2.0 compatibility - CDI not used
    }
    
    @Override
    public javax.enterprise.inject.spi.BeanManager getBeanManager() {
        // Return null for Portlet 2.0 compatibility - CDI not used
        return null;
    }
    
    @Override
    public Object getPortletAsyncContext() {
        // Return null for Portlet 2.0 compatibility - async not supported
        return null;
    }
    
    @Override
    public javax.servlet.AsyncContext startAsync(javax.servlet.ServletRequest servletRequest, javax.servlet.ServletResponse servletResponse) {
        // Return null for Portlet 2.0 compatibility - async not supported
        return null;
    }
    

}
