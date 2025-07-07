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
package org.apereo.portal.portlet.rendering.worker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpSession;

/**
 * Wrapper for a {@link HttpServletRequest} that makes the request inaccessible once the worker has
 * been canceled.
 */
public class GuardingHttpServletRequest extends HttpServletRequestWrapper {
    private final AtomicBoolean canceled;

    public GuardingHttpServletRequest(HttpServletRequest request, AtomicBoolean canceled) {
        super(request);
        this.canceled = canceled;
    }

    private final void checkState() {
        if (this.canceled.get()) {
            throw new IllegalStateException(
                    "The portlet worker has been canceled, the request is no longer in a valid state");
        }
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletRequestWrapper#getAuthType()
     */
    @Override
    public String getAuthType() {
        this.checkState();
        return super.getAuthType();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletRequestWrapper#getCookies()
     */
    @Override
    public Cookie[] getCookies() {
        this.checkState();
        return super.getCookies();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletRequestWrapper#getDateHeader(java.lang.String)
     */
    @Override
    public long getDateHeader(String name) {
        this.checkState();
        return super.getDateHeader(name);
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletRequestWrapper#getHeader(java.lang.String)
     */
    @Override
    public String getHeader(String name) {
        this.checkState();
        return super.getHeader(name);
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletRequestWrapper#getHeaders(java.lang.String)
     */
    @Override
    public Enumeration getHeaders(String name) {
        this.checkState();
        return super.getHeaders(name);
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletRequestWrapper#getHeaderNames()
     */
    @Override
    public Enumeration getHeaderNames() {
        this.checkState();
        return super.getHeaderNames();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletRequestWrapper#getIntHeader(java.lang.String)
     */
    @Override
    public int getIntHeader(String name) {
        this.checkState();
        return super.getIntHeader(name);
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletRequestWrapper#getMethod()
     */
    @Override
    public String getMethod() {
        this.checkState();
        return super.getMethod();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletRequestWrapper#getPathInfo()
     */
    @Override
    public String getPathInfo() {
        this.checkState();
        return super.getPathInfo();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletRequestWrapper#getPathTranslated()
     */
    @Override
    public String getPathTranslated() {
        this.checkState();
        return super.getPathTranslated();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletRequestWrapper#getContextPath()
     */
    @Override
    public String getContextPath() {
        this.checkState();
        return super.getContextPath();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletRequestWrapper#getQueryString()
     */
    @Override
    public String getQueryString() {
        this.checkState();
        return super.getQueryString();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletRequestWrapper#getRemoteUser()
     */
    @Override
    public String getRemoteUser() {
        this.checkState();
        return super.getRemoteUser();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletRequestWrapper#isUserInRole(java.lang.String)
     */
    @Override
    public boolean isUserInRole(String role) {
        this.checkState();
        return super.isUserInRole(role);
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletRequestWrapper#getUserPrincipal()
     */
    @Override
    public Principal getUserPrincipal() {
        this.checkState();
        return super.getUserPrincipal();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletRequestWrapper#getRequestedSessionId()
     */
    @Override
    public String getRequestedSessionId() {
        this.checkState();
        return super.getRequestedSessionId();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletRequestWrapper#getRequestURI()
     */
    @Override
    public String getRequestURI() {
        this.checkState();
        return super.getRequestURI();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletRequestWrapper#getRequestURL()
     */
    @Override
    public StringBuffer getRequestURL() {
        this.checkState();
        return super.getRequestURL();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletRequestWrapper#getServletPath()
     */
    @Override
    public String getServletPath() {
        this.checkState();
        return super.getServletPath();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletRequestWrapper#getSession(boolean)
     */
    @Override
    public HttpSession getSession(boolean create) {
        this.checkState();
        return super.getSession(create);
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletRequestWrapper#getSession()
     */
    @Override
    public HttpSession getSession() {
        this.checkState();
        return super.getSession();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletRequestWrapper#isRequestedSessionIdValid()
     */
    @Override
    public boolean isRequestedSessionIdValid() {
        this.checkState();
        return super.isRequestedSessionIdValid();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletRequestWrapper#isRequestedSessionIdFromCookie()
     */
    @Override
    public boolean isRequestedSessionIdFromCookie() {
        this.checkState();
        return super.isRequestedSessionIdFromCookie();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletRequestWrapper#isRequestedSessionIdFromURL()
     */
    @Override
    public boolean isRequestedSessionIdFromURL() {
        this.checkState();
        return super.isRequestedSessionIdFromURL();
    }



    /* (non-Javadoc)
     * @see jakarta.servlet.ServletRequestWrapper#getRequest()
     */
    @Override
    public ServletRequest getRequest() {
        this.checkState();
        return super.getRequest();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletRequestWrapper#setRequest(jakarta.servlet.ServletRequest)
     */
    @Override
    public void setRequest(ServletRequest request) {
        this.checkState();
        super.setRequest(request);
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletRequestWrapper#getAttribute(java.lang.String)
     */
    @Override
    public Object getAttribute(String name) {
        this.checkState();
        return super.getAttribute(name);
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletRequestWrapper#getAttributeNames()
     */
    @Override
    public Enumeration getAttributeNames() {
        this.checkState();
        return super.getAttributeNames();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletRequestWrapper#getCharacterEncoding()
     */
    @Override
    public String getCharacterEncoding() {
        this.checkState();
        return super.getCharacterEncoding();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletRequestWrapper#setCharacterEncoding(java.lang.String)
     */
    @Override
    public void setCharacterEncoding(String enc) throws UnsupportedEncodingException {
        this.checkState();
        super.setCharacterEncoding(enc);
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletRequestWrapper#getContentLength()
     */
    @Override
    public int getContentLength() {
        this.checkState();
        return super.getContentLength();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletRequestWrapper#getContentType()
     */
    @Override
    public String getContentType() {
        this.checkState();
        return super.getContentType();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletRequestWrapper#getInputStream()
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        this.checkState();
        return super.getInputStream();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletRequestWrapper#getParameter(java.lang.String)
     */
    @Override
    public String getParameter(String name) {
        this.checkState();
        return super.getParameter(name);
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletRequestWrapper#getParameterMap()
     */
    @Override
    public Map getParameterMap() {
        this.checkState();
        return super.getParameterMap();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletRequestWrapper#getParameterNames()
     */
    @Override
    public Enumeration getParameterNames() {
        this.checkState();
        return super.getParameterNames();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletRequestWrapper#getParameterValues(java.lang.String)
     */
    @Override
    public String[] getParameterValues(String name) {
        this.checkState();
        return super.getParameterValues(name);
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletRequestWrapper#getProtocol()
     */
    @Override
    public String getProtocol() {
        this.checkState();
        return super.getProtocol();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletRequestWrapper#getScheme()
     */
    @Override
    public String getScheme() {
        this.checkState();
        return super.getScheme();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletRequestWrapper#getServerName()
     */
    @Override
    public String getServerName() {
        this.checkState();
        return super.getServerName();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletRequestWrapper#getServerPort()
     */
    @Override
    public int getServerPort() {
        this.checkState();
        return super.getServerPort();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletRequestWrapper#getReader()
     */
    @Override
    public BufferedReader getReader() throws IOException {
        this.checkState();
        return super.getReader();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletRequestWrapper#getRemoteAddr()
     */
    @Override
    public String getRemoteAddr() {
        this.checkState();
        return super.getRemoteAddr();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletRequestWrapper#getRemoteHost()
     */
    @Override
    public String getRemoteHost() {
        this.checkState();
        return super.getRemoteHost();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletRequestWrapper#setAttribute(java.lang.String, java.lang.Object)
     */
    @Override
    public void setAttribute(String name, Object o) {
        this.checkState();
        super.setAttribute(name, o);
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletRequestWrapper#removeAttribute(java.lang.String)
     */
    @Override
    public void removeAttribute(String name) {
        this.checkState();
        super.removeAttribute(name);
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletRequestWrapper#getLocale()
     */
    @Override
    public Locale getLocale() {
        this.checkState();
        return super.getLocale();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletRequestWrapper#getLocales()
     */
    @Override
    public Enumeration getLocales() {
        this.checkState();
        return super.getLocales();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletRequestWrapper#isSecure()
     */
    @Override
    public boolean isSecure() {
        this.checkState();
        return super.isSecure();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletRequestWrapper#getRequestDispatcher(java.lang.String)
     */
    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        this.checkState();
        return super.getRequestDispatcher(path);
    }



    /* (non-Javadoc)
     * @see jakarta.servlet.ServletRequestWrapper#getRemotePort()
     */
    @Override
    public int getRemotePort() {
        this.checkState();
        return super.getRemotePort();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletRequestWrapper#getLocalName()
     */
    @Override
    public String getLocalName() {
        this.checkState();
        return super.getLocalName();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletRequestWrapper#getLocalAddr()
     */
    @Override
    public String getLocalAddr() {
        this.checkState();
        return super.getLocalAddr();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletRequestWrapper#getLocalPort()
     */
    @Override
    public int getLocalPort() {
        this.checkState();
        return super.getLocalPort();
    }
}
