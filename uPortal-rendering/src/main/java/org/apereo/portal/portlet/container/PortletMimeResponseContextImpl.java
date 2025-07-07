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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Locale;
import javax.portlet.CacheControl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang.Validate;
import org.apache.pluto.container.PortletContainer;
import org.apache.pluto.container.PortletMimeResponseContext;
import org.apache.pluto.container.PortletURLProvider;
import org.apache.pluto.container.PortletURLProvider.TYPE;
import org.apereo.portal.portlet.container.properties.IRequestPropertiesManager;
import org.apereo.portal.portlet.container.services.IPortletCookieService;
import org.apereo.portal.portlet.om.IPortletWindow;
import org.apereo.portal.portlet.om.IPortletWindowId;
import org.apereo.portal.portlet.rendering.IPortletRenderer;
import org.apereo.portal.portlet.rendering.PortletOutputHandler;
import org.apereo.portal.portlet.url.PortletURLProviderImpl;
import org.apereo.portal.url.IPortalUrlBuilder;
import org.apereo.portal.url.IPortalUrlProvider;
import org.apereo.portal.url.IPortletUrlBuilder;
import org.apereo.portal.url.UrlType;

/** */
public abstract class PortletMimeResponseContextImpl extends PortletResponseContextImpl
        implements PortletMimeResponseContext {

    private final IPortalUrlProvider portalUrlProvider;
    private final PortletOutputHandler portletOutputHandler;
    private final CacheControl cacheControl;

    public PortletMimeResponseContextImpl(
            PortletContainer portletContainer,
            IPortletWindow portletWindow,
            HttpServletRequest containerRequest,
            HttpServletResponse containerResponse,
            IRequestPropertiesManager requestPropertiesManager,
            IPortalUrlProvider portalUrlProvider,
            IPortletCookieService portletCookieService) {

        super(
                portletContainer,
                portletWindow,
                containerRequest,
                containerResponse,
                requestPropertiesManager,
                portletCookieService);

        Validate.notNull(portalUrlProvider, "portalUrlProvider can not be null");
        this.portalUrlProvider = portalUrlProvider;

        this.portletOutputHandler =
                (PortletOutputHandler)
                        containerRequest.getAttribute(
                                IPortletRenderer.ATTRIBUTE__PORTLET_OUTPUT_HANDLER);
        Validate.notNull(
                portletOutputHandler,
                "No "
                        + IPortletRenderer.ATTRIBUTE__PORTLET_OUTPUT_HANDLER
                        + " attribute found in request");

        this.cacheControl =
                (CacheControl)
                        containerRequest.getAttribute(
                                IPortletRenderer.ATTRIBUTE__PORTLET_CACHE_CONTROL);
        Validate.notNull(
                cacheControl,
                "No "
                        + IPortletRenderer.ATTRIBUTE__PORTLET_OUTPUT_HANDLER
                        + " attribute found in request");
    }

    protected final PortletOutputHandler getPortletOutputHandler() {
        return portletOutputHandler;
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletMimeResponseContext#getCacheControl()
     */
    @Override
    public CacheControl getCacheControl() {
        this.checkContextStatus();
        return this.cacheControl;
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletMimeResponseContext#flushBuffer()
     */
    @Override
    public void flushBuffer() throws IOException {
        this.checkContextStatus();
        this.portletOutputHandler.flushBuffer();
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletMimeResponseContext#getBufferSize()
     */
    @Override
    public int getBufferSize() {
        return this.portletOutputHandler.getBufferSize();
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletMimeResponseContext#getCharacterEncoding()
     */
    @Override
    public String getCharacterEncoding() {
        this.checkContextStatus();

        return this.servletResponse.getCharacterEncoding();
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletMimeResponseContext#getContentType()
     */
    @Override
    public String getContentType() {
        this.checkContextStatus();
        return this.servletResponse.getContentType();
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletMimeResponseContext#getLocale()
     */
    @Override
    public Locale getLocale() {
        this.checkContextStatus();
        return this.servletResponse.getLocale();
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletMimeResponseContext#getOutputStream()
     */
    @Override
    public OutputStream getOutputStream() throws IOException, IllegalStateException {
        this.checkContextStatus();
        return this.portletOutputHandler.getOutputStream();
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletMimeResponseContext#getPortletURLProvider(org.apache.pluto.container.PortletURLProvider.TYPE)
     */
    @Override
    public PortletURLProvider getPortletURLProvider(TYPE type) {
        final IPortletWindowId portletWindowId = this.portletWindow.getPortletWindowId();
        final UrlType urlType = UrlType.fromPortletUrlType(type);
        final IPortalUrlBuilder portalUrlBuilder =
                this.portalUrlProvider.getPortalUrlBuilderByPortletWindow(
                        containerRequest, portletWindowId, urlType);
        final IPortletUrlBuilder portletUrlBuilder =
                portalUrlBuilder.getPortletUrlBuilder(portletWindowId);
        return new PortletURLProviderImpl(portletUrlBuilder);
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletMimeResponseContext#getWriter()
     */
    @Override
    public PrintWriter getWriter() throws IOException, IllegalStateException {
        this.checkContextStatus();
        return this.portletOutputHandler.getPrintWriter();
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletMimeResponseContext#isCommitted()
     */
    @Override
    public boolean isCommitted() {
        return this.portletOutputHandler.isCommitted();
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletMimeResponseContext#reset()
     */
    @Override
    public void reset() {
        this.checkContextStatus();
        this.portletOutputHandler.reset();
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletMimeResponseContext#resetBuffer()
     */
    @Override
    public void resetBuffer() {
        this.checkContextStatus();
        this.portletOutputHandler.resetBuffer();
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletMimeResponseContext#setBufferSize(int)
     */
    @Override
    public void setBufferSize(int size) {
        this.checkContextStatus();
        this.portletOutputHandler.setBufferSize(size);
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletMimeResponseContext#setContentType(java.lang.String)
     */
    @Override
    public void setContentType(String contentType) {
        this.checkContextStatus();
        this.portletOutputHandler.setContentType(contentType);
    }
    
    // Interface compliance method - renamed to avoid override conflict with parent class
    public javax.servlet.http.HttpServletResponse getPortletServletResponse() {
        // Convert jakarta to javax servlet response for compatibility
        return convertToJavaxServletResponse(this.servletResponse);
    }
    
    private javax.servlet.http.HttpServletResponse convertToJavaxServletResponse(jakarta.servlet.http.HttpServletResponse jakartaResponse) {
        if (jakartaResponse == null) return null;
        return new JavaxServletResponseAdapter(jakartaResponse);
    }
    
    private static class JavaxServletResponseAdapter implements javax.servlet.http.HttpServletResponse {
        private final jakarta.servlet.http.HttpServletResponse jakartaResponse;
        
        public JavaxServletResponseAdapter(jakarta.servlet.http.HttpServletResponse jakartaResponse) {
            this.jakartaResponse = jakartaResponse;
        }
        
        @Override public void addCookie(javax.servlet.http.Cookie cookie) {}
        @Override public boolean containsHeader(String name) { return jakartaResponse.containsHeader(name); }
        @Override public String encodeURL(String url) { return jakartaResponse.encodeURL(url); }
        @Override public String encodeRedirectURL(String url) { return jakartaResponse.encodeRedirectURL(url); }
        @Override public String encodeUrl(String url) { return jakartaResponse.encodeURL(url); }
        @Override public String encodeRedirectUrl(String url) { return jakartaResponse.encodeRedirectURL(url); }
        @Override public void sendError(int sc, String msg) {}
        @Override public void sendError(int sc) {}
        @Override public void sendRedirect(String location) {}
        @Override public void setDateHeader(String name, long date) { jakartaResponse.setDateHeader(name, date); }
        @Override public void addDateHeader(String name, long date) { jakartaResponse.addDateHeader(name, date); }
        @Override public void setHeader(String name, String value) { jakartaResponse.setHeader(name, value); }
        @Override public void addHeader(String name, String value) { jakartaResponse.addHeader(name, value); }
        @Override public void setIntHeader(String name, int value) { jakartaResponse.setIntHeader(name, value); }
        @Override public void addIntHeader(String name, int value) { jakartaResponse.addIntHeader(name, value); }
        @Override public void setStatus(int sc) { jakartaResponse.setStatus(sc); }
        @Override public void setStatus(int sc, String sm) { jakartaResponse.setStatus(sc); }
        @Override public int getStatus() { return jakartaResponse.getStatus(); }
        @Override public String getHeader(String name) { return jakartaResponse.getHeader(name); }
        @Override public java.util.Collection<String> getHeaders(String name) { return jakartaResponse.getHeaders(name); }
        @Override public java.util.Collection<String> getHeaderNames() { return jakartaResponse.getHeaderNames(); }
        @Override public String getCharacterEncoding() { return jakartaResponse.getCharacterEncoding(); }
        @Override public String getContentType() { return jakartaResponse.getContentType(); }
        @Override public javax.servlet.ServletOutputStream getOutputStream() { return null; }
        @Override public java.io.PrintWriter getWriter() { return null; }
        @Override public void setCharacterEncoding(String charset) { jakartaResponse.setCharacterEncoding(charset); }
        @Override public void setContentLength(int len) { jakartaResponse.setContentLength(len); }
        @Override public void setContentLengthLong(long len) { jakartaResponse.setContentLengthLong(len); }
        @Override public void setContentType(String type) { jakartaResponse.setContentType(type); }
        @Override public void setBufferSize(int size) { jakartaResponse.setBufferSize(size); }
        @Override public int getBufferSize() { return jakartaResponse.getBufferSize(); }
        @Override public void flushBuffer() {}
        @Override public void resetBuffer() { jakartaResponse.resetBuffer(); }
        @Override public boolean isCommitted() { return jakartaResponse.isCommitted(); }
        @Override public void reset() { jakartaResponse.reset(); }
        @Override public void setLocale(java.util.Locale loc) { jakartaResponse.setLocale(loc); }
        @Override public java.util.Locale getLocale() { return jakartaResponse.getLocale(); }
    }
}
