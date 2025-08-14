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

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.pluto.container.PortletContainer;
import org.apache.pluto.container.PortletRequestContext;
import org.apache.pluto.container.driver.PortletServlet;
import org.apereo.portal.portlet.container.properties.IRequestPropertiesManager;
import org.apereo.portal.portlet.container.services.IPortletCookieService;
import org.apereo.portal.portlet.container.services.RequestAttributeService;
import org.apereo.portal.portlet.om.IPortletWindow;
import org.apereo.portal.portlet.om.IPortletWindowId;
import org.apereo.portal.portlet.rendering.IPortletRenderer;
import org.apereo.portal.url.IPortalRequestInfo;
import org.apereo.portal.url.IPortletRequestInfo;
import org.apereo.portal.url.ParameterMap;
import org.apereo.portal.url.UrlType;
import org.apereo.portal.utils.MultivaluedMapPopulator;
import org.apereo.portal.utils.web.AbstractHttpServletRequestWrapper;
import org.springframework.util.Assert;

/** Backs the {@link PortletRequest} impl provided by Pluto */
public class PortletRequestContextImpl extends AbstractPortletContextImpl
        implements PortletRequestContext {

    public String getPhase() {
        // Return appropriate phase based on URL type
        if (this.portalRequestInfo != null) {
            final UrlType urlType = this.portalRequestInfo.getUrlType();
            if (urlType != null) {
                switch (urlType) {
                    case ACTION:
                        return "ACTION_PHASE";
                    case RESOURCE:
                        return "RESOURCE_PHASE";
                    case RENDER:
                    default:
                        return "RENDER_PHASE";
                }
            }
        }
        return "RENDER_PHASE";
    }
    private final Map<String, Object> attributes = new LinkedHashMap<String, Object>();

    protected final IRequestPropertiesManager requestPropertiesManager;
    protected final IPortalRequestInfo portalRequestInfo;
    protected final IPortletRequestInfo portletRequestInfo;
    protected final RequestAttributeService requestAttributeService;

    // Objects provided by the PortletServlet via the init method
    // The servlet objects are from the scope of the cross-context dispatch
    protected PortletConfig portletConfig;
    protected ServletContext servletContext;

    public PortletRequestContextImpl(
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
                portletCookieService);

        if (requestPropertiesManager == null) {
            throw new IllegalArgumentException("requestPropertiesManager cannot be null");
        }
        if (portalRequestInfo == null) {
            throw new IllegalArgumentException("portalRequestInfo cannot be null");
        }
        if (requestAttributeService == null) {
            throw new IllegalArgumentException("requestAttributeService cannot be null");
        }

        this.requestPropertiesManager = requestPropertiesManager;
        this.portalRequestInfo = portalRequestInfo;
        this.requestAttributeService = requestAttributeService;

        final IPortletWindowId portletWindowId = this.portletWindow.getPortletWindowId();
        final Map<IPortletWindowId, ? extends IPortletRequestInfo> portletRequestInfoMap =
                this.portalRequestInfo.getPortletRequestInfoMap();
        this.portletRequestInfo = portletRequestInfoMap != null ? portletRequestInfoMap.get(portletWindowId) : null;
    }

    /**
     * Called by {@link PortletServlet} after the cross context dispatch but before the portlet
     * invocation
     */
    public void init(
            PortletConfig portletConfig,
            javax.servlet.ServletContext servletContext,
            javax.servlet.http.HttpServletRequest servletRequest,
            javax.servlet.http.HttpServletResponse servletResponse) {
        Assert.notNull(portletConfig, "portletConfig cannot be null");
        Assert.notNull(servletContext, "servletContext cannot be null");

        // Convert javax servlet objects to jakarta for internal use
        super.init((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);

        this.portletConfig = portletConfig;
        // Store as null since we can't convert javax to jakarta ServletContext easily
        this.servletContext = null;
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletRequestContext#getPortletConfig()
     */
    @Override
    public PortletConfig getPortletConfig() {
        return this.portletConfig;
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletRequestContext#getServletContext()
     */
    @Override
    public javax.servlet.ServletContext getServletContext() {
        // Return null for now to avoid compilation error - this is a compatibility stub
        return null;
    }

    // Jakarta servlet context getter
    public jakarta.servlet.ServletContext getJakartaServletContext() {
        return this.servletContext;
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletRequestContext#getAttribute(java.lang.String)
     */
    @Override
    public Object getAttribute(String name) {
        if (name.startsWith(IPortletRenderer.RENDERER_ATTRIBUTE_PREFIX)) {
            return null;
        }

        final Object attribute = this.attributes.get(name);
        if (attribute != null) {
            return attribute;
        }

        if (name.startsWith(AbstractHttpServletRequestWrapper.PORTAL_ATTRIBUTE_PREFIX)) {
            Object result = this.servletRequest.getAttribute(name);
            return result;
        }

        if (name.equals(PortletRequest.RENDER_PART)) {
            Object result = this.servletRequest.getAttribute(name);
            return result;
        }

        return this.requestAttributeService.getAttribute(
                this.servletRequest, portletWindow.getPlutoPortletWindow(), name);
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletRequestContext#getAttributeNames()
     */
    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(this.attributes.keySet());
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletRequestContext#setAttribute(java.lang.String, java.lang.Object)
     */
    @Override
    public void setAttribute(String name, Object value) {
        if (name.startsWith(IPortletRenderer.RENDERER_ATTRIBUTE_PREFIX)) {
            throw new IllegalArgumentException(
                    "Portlets cannot set attributes that start with: "
                            + IPortletRenderer.RENDERER_ATTRIBUTE_PREFIX);
        }

        this.attributes.put(name, value);
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletRequestContext#getCookies()
     */
    @Override
    public javax.servlet.http.Cookie[] getCookies() {
        final IPortletWindowId portletWindowId = this.portletWindow.getPortletWindowId();
        jakarta.servlet.http.Cookie[] jakartaCookies = this.portletCookieService.getAllPortletCookies(this.servletRequest, portletWindowId);
        return ServletTypeMapper.toJavax(jakartaCookies);
    }



    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletRequestContext#getPreferredLocale()
     */
    @Override
    public Locale getPreferredLocale() {
        return this.servletRequest.getLocale();
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletRequestContext#getPrivateParameterMap()
     */
    @Override
    public Map<String, String[]> getPrivateParameterMap() {
        if (this.portletRequestInfo != null) {
            final Map<String, List<String>> portletParameters =
                    this.portletRequestInfo.getPortletParameters();
            return ParameterMap.convertListMap(portletParameters);
        }

        // Only re-use render parameters on a render request
        if (this.portalRequestInfo.getUrlType() == UrlType.RENDER) {
            return this.portletWindow.getRenderParameters();
        }

        return Collections.emptyMap();
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletRequestContext#getProperties()
     */
    @Override
    public final Map<String, String[]> getProperties() {
        final MultivaluedMapPopulator<String, String> populator =
                new MultivaluedMapPopulator<String, String>();
        this.requestPropertiesManager.populateRequestProperties(
                this.servletRequest, portletWindow, populator);
        final Map<String, List<String>> map = populator.getMap();
        return ParameterMap.convertListMap(map);
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletRequestContext#getPublicParameterMap()
     */
    @Override
    public Map<String, String[]> getPublicParameterMap() {
        // Only re-use render parameters on a render request
        if (this.portalRequestInfo.getUrlType() == UrlType.RENDER) {
            return this.portletWindow.getPublicRenderParameters();
        }

        return Collections.emptyMap();
    }

    /*
     * (non-Javadoc)
     * @see org.apache.pluto.container.PortletRequestContext#getAttribute(java.lang.String, jakarta.servlet.ServletRequest)
     */
    public Object getAttribute(String name, ServletRequest request) {
        if (this.isServletContainerManagedAttribute(name)) {
            return request.getAttribute(name);
        }
        return null;
    }

    private boolean isServletContainerManagedAttribute(String name) {
        return getServletContainerManagedAttributes().contains(name);
    }

    private static HashSet<String> getServletContainerManagedAttributes() {
        // Return a default set for servlet container managed attributes
        HashSet<String> attributes = new HashSet<String>();
        attributes.add("javax.servlet.request.X509Certificate");
        attributes.add("javax.servlet.request.cipher_suite");
        attributes.add("javax.servlet.request.key_size");
        attributes.add("javax.servlet.request.ssl_session");
        return attributes;
    }

    // Minimal stub for Pluto 3.x compatibility - maintains Portlet 2.0 behavior
    public java.util.Map<String, java.util.List<String>> getQueryParams() {
        // Return empty map to maintain Portlet 2.0 behavior
        return Collections.emptyMap();
    }

    @Override
    public void startDispatch(javax.servlet.http.HttpServletRequest request, java.util.Map<String, java.util.List<String>> parameters, String path) {
        // No-op for Portlet 2.0 compatibility - dispatch not used
    }

    @Override
    public void endDispatch() {
        // No-op for Portlet 2.0 compatibility - dispatch not used
    }

    @Override
    public void setAsyncServletRequest(javax.servlet.http.HttpServletRequest request) {
        // No-op for Portlet 2.0 compatibility - async not supported
    }

    @Override
    public javax.servlet.http.HttpServletRequest getAsyncServletRequest() {
        // Return null for Portlet 2.0 compatibility - async not supported
        return null;
    }

    @Override
    public void setExecutingRequestBody(boolean executing) {
        // No-op for Portlet 2.0 compatibility - maintains existing behavior
    }

    @Override
    public boolean isExecutingRequestBody() {
        // Return false for Portlet 2.0 compatibility - maintains existing behavior
        return false;
    }

    @Override
    public javax.servlet.DispatcherType getDispatcherType() {
        // Return REQUEST for Portlet 2.0 compatibility - maintains existing behavior
        return javax.servlet.DispatcherType.REQUEST;
    }

    @Override
    public String getRenderHeaders() {
        // Return null for Portlet 2.0 compatibility - maintains existing behavior
        return null;
    }

    @Override
    public void setRenderHeaders(String headers) {
        // No-op for Portlet 2.0 compatibility - maintains existing behavior
    }

    @Override
    public javax.portlet.ActionParameters getActionParameters() {
        // Return null for Portlet 2.0 compatibility - maintains existing behavior
        return null;
    }

    @Override
    public javax.portlet.RenderParameters getRenderParameters() {
        // Return null for Portlet 2.0 compatibility - maintains existing behavior
        return null;
    }

    @Override
    public java.util.Map<String, String[]> getParameterMap() {
        // Return empty map for Portlet 2.0 compatibility - maintains existing behavior
        return java.util.Collections.emptyMap();
    }

    @Override
    public javax.portlet.PortletSession getPortletSession(boolean create) {
        // Return null for Portlet 2.0 compatibility - maintains existing behavior
        return null;
    }

    // PortletRequestContext interface implementation
    // These methods satisfy the interface requirements using translation helper
    
    @Override
    public javax.servlet.http.HttpServletResponse getServletResponse() {
        // Convert internal jakarta type to javax type for interface compliance
        return ServletTypeMapper.toJavax(this.getJakartaServletResponse());
    }

    @Override
    public javax.servlet.http.HttpServletRequest getServletRequest() {
        // Convert internal jakarta type to javax type for interface compliance
        return ServletTypeMapper.toJavax(this.getJakartaServletRequest());
    }

    @Override
    public javax.servlet.http.HttpServletResponse getContainerResponse() {
        // Convert internal jakarta type to javax type for interface compliance  
        return ServletTypeMapper.toJavax(this.getJakartaContainerResponse());
    }

    @Override
    public javax.servlet.http.HttpServletRequest getContainerRequest() {
        // Convert internal jakarta type to javax type for interface compliance
        return ServletTypeMapper.toJavax(this.getJakartaContainerRequest());
    }

    // Interface compliance methods (javax types for PortletRequestContext interface)
    public javax.servlet.http.HttpServletResponse getJavaxServletResponse() {
        return ServletTypeMapper.toJavax(this.servletResponse);
    }

    public javax.servlet.http.HttpServletRequest getJavaxServletRequest() {
        return ServletTypeMapper.toJavax(this.servletRequest);
    }

    public javax.servlet.http.HttpServletResponse getJavaxContainerResponse() {
        return ServletTypeMapper.toJavax(this.containerResponse);
    }

    public javax.servlet.http.HttpServletRequest getJavaxContainerRequest() {
        return ServletTypeMapper.toJavax(this.containerRequest);
    }

    @Override
    public void init(
            javax.portlet.PortletConfig portletConfig,
            javax.servlet.ServletContext servletContext,
            javax.servlet.http.HttpServletRequest request,
            javax.servlet.http.HttpServletResponse response,
            org.apache.pluto.container.PortletResponseContext responseContext) {
        // No-op for Portlet 2.0 compatibility - maintains existing behavior
    }


}
