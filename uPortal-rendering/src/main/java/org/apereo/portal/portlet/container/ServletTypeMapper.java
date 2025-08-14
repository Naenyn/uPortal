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

/**
 * Utility class to map between javax.servlet and jakarta.servlet types
 * for Portlet 2.0 compatibility while using Pluto 3.0 with Jakarta EE
 */
public class ServletTypeMapper {
    
    /**
     * Convert jakarta HttpServletRequest to javax HttpServletRequest
     */
    public static javax.servlet.http.HttpServletRequest toJavax(jakarta.servlet.http.HttpServletRequest jakartaRequest) {
        if (jakartaRequest == null) return null;
        return new JavaxRequestAdapter(jakartaRequest);
    }
    
    /**
     * Convert jakarta HttpServletResponse to javax HttpServletResponse  
     */
    public static javax.servlet.http.HttpServletResponse toJavax(jakarta.servlet.http.HttpServletResponse jakartaResponse) {
        if (jakartaResponse == null) return null;
        return new JavaxResponseAdapter(jakartaResponse);
    }
    
    /**
     * Convert jakarta Cookie to javax Cookie
     */
    public static javax.servlet.http.Cookie toJavax(jakarta.servlet.http.Cookie jakartaCookie) {
        if (jakartaCookie == null) return null;
        javax.servlet.http.Cookie javaxCookie = new javax.servlet.http.Cookie(jakartaCookie.getName(), jakartaCookie.getValue());
        if (jakartaCookie.getDomain() != null) {
            javaxCookie.setDomain(jakartaCookie.getDomain());
        }
        javaxCookie.setMaxAge(jakartaCookie.getMaxAge());
        if (jakartaCookie.getPath() != null) {
            javaxCookie.setPath(jakartaCookie.getPath());
        }
        javaxCookie.setSecure(jakartaCookie.getSecure());
        return javaxCookie;
    }
    
    /**
     * Convert jakarta Cookie array to javax Cookie array
     */
    public static javax.servlet.http.Cookie[] toJavax(jakarta.servlet.http.Cookie[] jakartaCookies) {
        if (jakartaCookies == null) return null;
        javax.servlet.http.Cookie[] javaxCookies = new javax.servlet.http.Cookie[jakartaCookies.length];
        for (int i = 0; i < jakartaCookies.length; i++) {
            javaxCookies[i] = toJavax(jakartaCookies[i]);
        }
        return javaxCookies;
    }
    
    /**
     * Convert javax HttpServletRequest to jakarta HttpServletRequest
     */
    public static jakarta.servlet.http.HttpServletRequest toJakarta(javax.servlet.http.HttpServletRequest javaxRequest) {
        if (javaxRequest == null) return null;
        return new JakartaRequestAdapter(javaxRequest);
    }
    
    /**
     * Convert javax HttpServletResponse to jakarta HttpServletResponse
     */
    public static jakarta.servlet.http.HttpServletResponse toJakarta(javax.servlet.http.HttpServletResponse javaxResponse) {
        if (javaxResponse == null) return null;
        return new JakartaResponseAdapter(javaxResponse);
    }
    
    /**
     * Convert javax HttpSession to jakarta HttpSession
     */
    public static jakarta.servlet.http.HttpSession toJakarta(javax.servlet.http.HttpSession javaxSession) {
        if (javaxSession == null) return null;
        return new JakartaSessionAdapter(javaxSession);
    }
    
    /**
     * Convert jakarta ServletContext to javax ServletContext
     */
    public static javax.servlet.ServletContext toJavax(jakarta.servlet.ServletContext jakartaContext) {
        if (jakartaContext == null) return null;
        return new JavaxServletContextAdapter(jakartaContext);
    }
    
    /**
     * Convert javax ServletContext to jakarta ServletContext
     */
    public static jakarta.servlet.ServletContext toJakarta(javax.servlet.ServletContext javaxContext) {
        if (javaxContext == null) return null;
        return new JakartaServletContextAdapter(javaxContext);
    }
    
    /**
     * Convert internal HeaderData to javax portlet HeaderData for interface compliance
     */
    public static Object toJavaxHeaderData(Object internalHeaderData) {
        // Return null for Portlet 2.0 compatibility - HeaderData not used
        return null;
    }
    
    /**
     * Convert internal PortletAsyncManager for interface compliance
     */
    public static org.apereo.portal.portlet.container.stubs.PortletAsyncManager toJavaxPortletAsyncManager(Object internalAsyncManager) {
        // Return null for Portlet 2.0 compatibility - async not supported
        return org.apereo.portal.portlet.container.stubs.PortletAsyncManager.createNull();
    }
    
    // Minimal adapter for HttpServletRequest
    private static class JavaxRequestAdapter implements javax.servlet.http.HttpServletRequest {
        private final jakarta.servlet.http.HttpServletRequest jakartaRequest;
        
        public JavaxRequestAdapter(jakarta.servlet.http.HttpServletRequest jakartaRequest) {
            this.jakartaRequest = jakartaRequest;
        }
        
        @Override public String getMethod() { return jakartaRequest.getMethod(); }
        @Override public String getRequestURI() { return jakartaRequest.getRequestURI(); }
        @Override public String getQueryString() { return jakartaRequest.getQueryString(); }
        @Override public String getContextPath() { return jakartaRequest.getContextPath(); }
        @Override public String getServletPath() { return jakartaRequest.getServletPath(); }
        @Override public String getPathInfo() { return jakartaRequest.getPathInfo(); }
        @Override public String getRemoteAddr() { return jakartaRequest.getRemoteAddr(); }
        @Override public String getRemoteHost() { return jakartaRequest.getRemoteHost(); }
        @Override public int getRemotePort() { return jakartaRequest.getRemotePort(); }
        @Override public String getLocalAddr() { return jakartaRequest.getLocalAddr(); }
        @Override public String getLocalName() { return jakartaRequest.getLocalName(); }
        @Override public int getLocalPort() { return jakartaRequest.getLocalPort(); }
        @Override public String getServerName() { return jakartaRequest.getServerName(); }
        @Override public int getServerPort() { return jakartaRequest.getServerPort(); }
        @Override public String getScheme() { return jakartaRequest.getScheme(); }
        @Override public String getProtocol() { return jakartaRequest.getProtocol(); }
        @Override public boolean isSecure() { return jakartaRequest.isSecure(); }
        @Override public String getCharacterEncoding() { return jakartaRequest.getCharacterEncoding(); }
        @Override public void setCharacterEncoding(String env) throws java.io.UnsupportedEncodingException { 
            try {
                jakartaRequest.setCharacterEncoding(env); 
            } catch (java.io.UnsupportedEncodingException e) {
                throw new java.io.UnsupportedEncodingException(e.getMessage());
            }
        }
        @Override public int getContentLength() { return jakartaRequest.getContentLength(); }
        @Override public long getContentLengthLong() { return jakartaRequest.getContentLengthLong(); }
        @Override public String getContentType() { return jakartaRequest.getContentType(); }
        @Override public Object getAttribute(String name) { return jakartaRequest.getAttribute(name); }
        @Override public void setAttribute(String name, Object o) { jakartaRequest.setAttribute(name, o); }
        @Override public void removeAttribute(String name) { jakartaRequest.removeAttribute(name); }
        @Override public java.util.Enumeration<String> getAttributeNames() { return jakartaRequest.getAttributeNames(); }
        @Override public String getParameter(String name) { return jakartaRequest.getParameter(name); }
        @Override public java.util.Map<String, String[]> getParameterMap() { return jakartaRequest.getParameterMap(); }
        @Override public java.util.Enumeration<String> getParameterNames() { return jakartaRequest.getParameterNames(); }
        @Override public String[] getParameterValues(String name) { return jakartaRequest.getParameterValues(name); }
        @Override public String getHeader(String name) { return jakartaRequest.getHeader(name); }
        @Override public java.util.Enumeration<String> getHeaders(String name) { return jakartaRequest.getHeaders(name); }
        @Override public java.util.Enumeration<String> getHeaderNames() { return jakartaRequest.getHeaderNames(); }
        @Override public int getIntHeader(String name) { return jakartaRequest.getIntHeader(name); }
        @Override public long getDateHeader(String name) { return jakartaRequest.getDateHeader(name); }
        @Override public String getRequestedSessionId() { return jakartaRequest.getRequestedSessionId(); }
        @Override public boolean isRequestedSessionIdValid() { return jakartaRequest.isRequestedSessionIdValid(); }
        @Override public boolean isRequestedSessionIdFromCookie() { return jakartaRequest.isRequestedSessionIdFromCookie(); }
        @Override public boolean isRequestedSessionIdFromURL() { return jakartaRequest.isRequestedSessionIdFromURL(); }
        @Override public boolean isRequestedSessionIdFromUrl() { return jakartaRequest.isRequestedSessionIdFromURL(); }
        @Override public java.util.Locale getLocale() { return jakartaRequest.getLocale(); }
        @Override public java.util.Enumeration<java.util.Locale> getLocales() { return jakartaRequest.getLocales(); }
        @Override public String getAuthType() { return jakartaRequest.getAuthType(); }
        @Override public java.security.Principal getUserPrincipal() { return jakartaRequest.getUserPrincipal(); }
        @Override public boolean isUserInRole(String role) { return jakartaRequest.isUserInRole(role); }
        @Override public String getRemoteUser() { return jakartaRequest.getRemoteUser(); }
        @Override public StringBuffer getRequestURL() { return jakartaRequest.getRequestURL(); }
        @Override public String getPathTranslated() { return jakartaRequest.getPathTranslated(); }
        
        // Portlet 2.0 compatibility stubs - return null/empty for unsupported operations
        @Override public javax.servlet.http.HttpSession getSession() { return null; }
        @Override public javax.servlet.http.HttpSession getSession(boolean create) { return null; }
        @Override public javax.servlet.ServletInputStream getInputStream() { return null; }
        @Override public java.io.BufferedReader getReader() { return null; }
        @Override public javax.servlet.RequestDispatcher getRequestDispatcher(String path) { return null; }
        @Override public javax.servlet.ServletContext getServletContext() { return null; }
        @Override public javax.servlet.AsyncContext startAsync() { return null; }
        @Override public javax.servlet.AsyncContext startAsync(javax.servlet.ServletRequest servletRequest, javax.servlet.ServletResponse servletResponse) { return null; }
        @Override public boolean isAsyncStarted() { return false; }
        @Override public boolean isAsyncSupported() { return false; }
        @Override public javax.servlet.AsyncContext getAsyncContext() { return null; }
        @Override public javax.servlet.DispatcherType getDispatcherType() { return javax.servlet.DispatcherType.REQUEST; }
        @Override public javax.servlet.http.Cookie[] getCookies() { return new javax.servlet.http.Cookie[0]; }
        @Override public javax.servlet.http.HttpUpgradeHandler upgrade(Class handlerClass) { return null; }
        @Override public boolean authenticate(javax.servlet.http.HttpServletResponse response) { return false; }
        @Override public void login(String username, String password) {}
        @Override public void logout() {}
        @Override public java.util.Collection<javax.servlet.http.Part> getParts() { return null; }
        @Override public javax.servlet.http.Part getPart(String name) { return null; }
        @Override public String changeSessionId() { return null; }
        @Override public String getRealPath(String path) { return null; }
    }
    
    // Minimal adapter for HttpServletResponse
    private static class JavaxResponseAdapter implements javax.servlet.http.HttpServletResponse {
        private final jakarta.servlet.http.HttpServletResponse jakartaResponse;
        
        public JavaxResponseAdapter(jakarta.servlet.http.HttpServletResponse jakartaResponse) {
            this.jakartaResponse = jakartaResponse;
        }
        
        @Override public boolean containsHeader(String name) { return jakartaResponse.containsHeader(name); }
        @Override public String encodeURL(String url) { return jakartaResponse.encodeURL(url); }
        @Override public String encodeRedirectURL(String url) { return jakartaResponse.encodeRedirectURL(url); }
        @Override public String encodeUrl(String url) { return jakartaResponse.encodeURL(url); }
        @Override public String encodeRedirectUrl(String url) { return jakartaResponse.encodeRedirectURL(url); }
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
        @Override public void setCharacterEncoding(String charset) { jakartaResponse.setCharacterEncoding(charset); }
        @Override public void setContentLength(int len) { jakartaResponse.setContentLength(len); }
        @Override public void setContentLengthLong(long len) { jakartaResponse.setContentLengthLong(len); }
        @Override public void setContentType(String type) { jakartaResponse.setContentType(type); }
        @Override public void setBufferSize(int size) { jakartaResponse.setBufferSize(size); }
        @Override public int getBufferSize() { return jakartaResponse.getBufferSize(); }
        @Override public void resetBuffer() { jakartaResponse.resetBuffer(); }
        @Override public boolean isCommitted() { return jakartaResponse.isCommitted(); }
        @Override public void reset() { jakartaResponse.reset(); }
        @Override public void setLocale(java.util.Locale loc) { jakartaResponse.setLocale(loc); }
        @Override public java.util.Locale getLocale() { return jakartaResponse.getLocale(); }
        
        // Portlet 2.0 compatibility stubs - no-op for unsupported operations
        @Override public void addCookie(javax.servlet.http.Cookie cookie) {}
        @Override public void sendError(int sc, String msg) {}
        @Override public void sendError(int sc) {}
        @Override public void sendRedirect(String location) {}
        @Override public javax.servlet.ServletOutputStream getOutputStream() { return null; }
        @Override public java.io.PrintWriter getWriter() { return null; }
        @Override public void flushBuffer() {}
    }
    
    // Minimal adapter for reverse conversion (javax to jakarta)
    private static class JakartaRequestAdapter implements jakarta.servlet.http.HttpServletRequest {
        private final javax.servlet.http.HttpServletRequest javaxRequest;
        
        public JakartaRequestAdapter(javax.servlet.http.HttpServletRequest javaxRequest) {
            this.javaxRequest = javaxRequest;
        }
        
        @Override public String getMethod() { return javaxRequest.getMethod(); }
        @Override public String getRequestURI() { return javaxRequest.getRequestURI(); }
        @Override public String getQueryString() { return javaxRequest.getQueryString(); }
        @Override public String getContextPath() { return javaxRequest.getContextPath(); }
        @Override public String getServletPath() { return javaxRequest.getServletPath(); }
        @Override public String getPathInfo() { return javaxRequest.getPathInfo(); }
        @Override public String getRemoteAddr() { return javaxRequest.getRemoteAddr(); }
        @Override public String getRemoteHost() { return javaxRequest.getRemoteHost(); }
        @Override public int getRemotePort() { return javaxRequest.getRemotePort(); }
        @Override public String getLocalAddr() { return javaxRequest.getLocalAddr(); }
        @Override public String getLocalName() { return javaxRequest.getLocalName(); }
        @Override public int getLocalPort() { return javaxRequest.getLocalPort(); }
        @Override public String getServerName() { return javaxRequest.getServerName(); }
        @Override public int getServerPort() { return javaxRequest.getServerPort(); }
        @Override public String getScheme() { return javaxRequest.getScheme(); }
        @Override public String getProtocol() { return javaxRequest.getProtocol(); }
        @Override public boolean isSecure() { return javaxRequest.isSecure(); }
        @Override public String getCharacterEncoding() { return javaxRequest.getCharacterEncoding(); }
        @Override public void setCharacterEncoding(String env) throws java.io.UnsupportedEncodingException { 
            try {
                javaxRequest.setCharacterEncoding(env); 
            } catch (java.io.UnsupportedEncodingException e) {
                throw new java.io.UnsupportedEncodingException(e.getMessage());
            }
        }
        @Override public int getContentLength() { return javaxRequest.getContentLength(); }
        @Override public long getContentLengthLong() { return javaxRequest.getContentLengthLong(); }
        @Override public String getContentType() { return javaxRequest.getContentType(); }
        @Override public Object getAttribute(String name) { return javaxRequest.getAttribute(name); }
        @Override public void setAttribute(String name, Object o) { javaxRequest.setAttribute(name, o); }
        @Override public void removeAttribute(String name) { javaxRequest.removeAttribute(name); }
        @Override public java.util.Enumeration<String> getAttributeNames() { return javaxRequest.getAttributeNames(); }
        @Override public String getParameter(String name) { return javaxRequest.getParameter(name); }
        @Override public java.util.Map<String, String[]> getParameterMap() { return javaxRequest.getParameterMap(); }
        @Override public java.util.Enumeration<String> getParameterNames() { return javaxRequest.getParameterNames(); }
        @Override public String[] getParameterValues(String name) { return javaxRequest.getParameterValues(name); }
        @Override public String getHeader(String name) { return javaxRequest.getHeader(name); }
        @Override public java.util.Enumeration<String> getHeaders(String name) { return javaxRequest.getHeaders(name); }
        @Override public java.util.Enumeration<String> getHeaderNames() { return javaxRequest.getHeaderNames(); }
        @Override public int getIntHeader(String name) { return javaxRequest.getIntHeader(name); }
        @Override public long getDateHeader(String name) { return javaxRequest.getDateHeader(name); }
        @Override public String getRequestedSessionId() { return javaxRequest.getRequestedSessionId(); }
        @Override public boolean isRequestedSessionIdValid() { return javaxRequest.isRequestedSessionIdValid(); }
        @Override public boolean isRequestedSessionIdFromCookie() { return javaxRequest.isRequestedSessionIdFromCookie(); }
        @Override public boolean isRequestedSessionIdFromURL() { return javaxRequest.isRequestedSessionIdFromURL(); }
        @Override public java.util.Locale getLocale() { return javaxRequest.getLocale(); }
        @Override public java.util.Enumeration<java.util.Locale> getLocales() { return javaxRequest.getLocales(); }
        @Override public String getAuthType() { return javaxRequest.getAuthType(); }
        @Override public java.security.Principal getUserPrincipal() { return javaxRequest.getUserPrincipal(); }
        @Override public boolean isUserInRole(String role) { return javaxRequest.isUserInRole(role); }
        @Override public String getRemoteUser() { return javaxRequest.getRemoteUser(); }
        @Override public StringBuffer getRequestURL() { return javaxRequest.getRequestURL(); }
        @Override public String getPathTranslated() { return javaxRequest.getPathTranslated(); }
        
        // Portlet 2.0 compatibility stubs - return null/empty for unsupported operations
        @Override public jakarta.servlet.http.HttpSession getSession() { return null; }
        @Override public jakarta.servlet.http.HttpSession getSession(boolean create) { return null; }
        @Override public jakarta.servlet.ServletInputStream getInputStream() { return null; }
        @Override public java.io.BufferedReader getReader() { return null; }
        @Override public jakarta.servlet.RequestDispatcher getRequestDispatcher(String path) { return null; }
        @Override public jakarta.servlet.ServletContext getServletContext() { return null; }
        @Override public jakarta.servlet.AsyncContext startAsync() { return null; }
        @Override public jakarta.servlet.AsyncContext startAsync(jakarta.servlet.ServletRequest servletRequest, jakarta.servlet.ServletResponse servletResponse) { return null; }
        @Override public boolean isAsyncStarted() { return false; }
        @Override public boolean isAsyncSupported() { return false; }
        @Override public jakarta.servlet.AsyncContext getAsyncContext() { return null; }
        @Override public jakarta.servlet.DispatcherType getDispatcherType() { return jakarta.servlet.DispatcherType.REQUEST; }
        @Override public jakarta.servlet.http.Cookie[] getCookies() { return new jakarta.servlet.http.Cookie[0]; }
        @Override public jakarta.servlet.http.HttpUpgradeHandler upgrade(Class handlerClass) { return null; }
        @Override public boolean authenticate(jakarta.servlet.http.HttpServletResponse response) { return false; }
        @Override public void login(String username, String password) {}
        @Override public void logout() {}
        @Override public java.util.Collection<jakarta.servlet.http.Part> getParts() { return null; }
        @Override public jakarta.servlet.http.Part getPart(String name) { return null; }
        @Override public String changeSessionId() { return null; }
        public String getRealPath(String path) { return null; }
        @Override public jakarta.servlet.ServletConnection getServletConnection() { return null; }
        @Override public String getProtocolRequestId() { return null; }
        @Override public String getRequestId() { return null; }
    }
    
    // Minimal adapter for HttpServletResponse (javax to jakarta)
    private static class JakartaResponseAdapter implements jakarta.servlet.http.HttpServletResponse {
        private final javax.servlet.http.HttpServletResponse javaxResponse;
        
        public JakartaResponseAdapter(javax.servlet.http.HttpServletResponse javaxResponse) {
            this.javaxResponse = javaxResponse;
        }
        
        @Override public boolean containsHeader(String name) { return javaxResponse.containsHeader(name); }
        @Override public String encodeURL(String url) { return javaxResponse.encodeURL(url); }
        @Override public String encodeRedirectURL(String url) { return javaxResponse.encodeRedirectURL(url); }
        @Override public void setDateHeader(String name, long date) { javaxResponse.setDateHeader(name, date); }
        @Override public void addDateHeader(String name, long date) { javaxResponse.addDateHeader(name, date); }
        @Override public void setHeader(String name, String value) { javaxResponse.setHeader(name, value); }
        @Override public void addHeader(String name, String value) { javaxResponse.addHeader(name, value); }
        @Override public void setIntHeader(String name, int value) { javaxResponse.setIntHeader(name, value); }
        @Override public void addIntHeader(String name, int value) { javaxResponse.addIntHeader(name, value); }
        @Override public void setStatus(int sc) { javaxResponse.setStatus(sc); }
        @Override public int getStatus() { return javaxResponse.getStatus(); }
        @Override public String getHeader(String name) { return javaxResponse.getHeader(name); }
        @Override public java.util.Collection<String> getHeaders(String name) { return javaxResponse.getHeaders(name); }
        @Override public java.util.Collection<String> getHeaderNames() { return javaxResponse.getHeaderNames(); }
        @Override public String getCharacterEncoding() { return javaxResponse.getCharacterEncoding(); }
        @Override public String getContentType() { return javaxResponse.getContentType(); }
        @Override public void setCharacterEncoding(String charset) { javaxResponse.setCharacterEncoding(charset); }
        @Override public void setContentLength(int len) { javaxResponse.setContentLength(len); }
        @Override public void setContentLengthLong(long len) { javaxResponse.setContentLengthLong(len); }
        @Override public void setContentType(String type) { javaxResponse.setContentType(type); }
        @Override public void setBufferSize(int size) { javaxResponse.setBufferSize(size); }
        @Override public int getBufferSize() { return javaxResponse.getBufferSize(); }
        @Override public void resetBuffer() { javaxResponse.resetBuffer(); }
        @Override public boolean isCommitted() { return javaxResponse.isCommitted(); }
        @Override public void reset() { javaxResponse.reset(); }
        @Override public void setLocale(java.util.Locale loc) { javaxResponse.setLocale(loc); }
        @Override public java.util.Locale getLocale() { return javaxResponse.getLocale(); }
        
        // Portlet 2.0 compatibility stubs - no-op for unsupported operations
        @Override public void addCookie(jakarta.servlet.http.Cookie cookie) {}
        @Override public void sendError(int sc, String msg) {}
        @Override public void sendError(int sc) {}
        @Override public void sendRedirect(String location) {}
        @Override public jakarta.servlet.ServletOutputStream getOutputStream() { return null; }
        @Override public java.io.PrintWriter getWriter() { return null; }
        @Override public void flushBuffer() {}
    }
    
    // Minimal adapter for HttpSession
    private static class JakartaSessionAdapter implements jakarta.servlet.http.HttpSession {
        private final javax.servlet.http.HttpSession javaxSession;
        
        public JakartaSessionAdapter(javax.servlet.http.HttpSession javaxSession) {
            this.javaxSession = javaxSession;
        }
        
        @Override public long getCreationTime() { return javaxSession.getCreationTime(); }
        @Override public String getId() { return javaxSession.getId(); }
        @Override public long getLastAccessedTime() { return javaxSession.getLastAccessedTime(); }
        @Override public jakarta.servlet.ServletContext getServletContext() { return null; }
        @Override public void setMaxInactiveInterval(int interval) { javaxSession.setMaxInactiveInterval(interval); }
        @Override public int getMaxInactiveInterval() { return javaxSession.getMaxInactiveInterval(); }
        @Override public Object getAttribute(String name) { return javaxSession.getAttribute(name); }
        @Override public java.util.Enumeration<String> getAttributeNames() { return javaxSession.getAttributeNames(); }
        @Override public void setAttribute(String name, Object value) { javaxSession.setAttribute(name, value); }
        @Override public void removeAttribute(String name) { javaxSession.removeAttribute(name); }
        @Override public void invalidate() { javaxSession.invalidate(); }
        @Override public boolean isNew() { return javaxSession.isNew(); }
    }
    
    // Minimal adapter for ServletContext (jakarta to javax)
    private static class JavaxServletContextAdapter implements javax.servlet.ServletContext {
        private final jakarta.servlet.ServletContext jakartaContext;
        
        public JavaxServletContextAdapter(jakarta.servlet.ServletContext jakartaContext) {
            this.jakartaContext = jakartaContext;
        }
        
        @Override public String getContextPath() { return jakartaContext.getContextPath(); }
        @Override public javax.servlet.ServletContext getContext(String uripath) { return null; }
        @Override public int getMajorVersion() { return jakartaContext.getMajorVersion(); }
        @Override public int getMinorVersion() { return jakartaContext.getMinorVersion(); }
        @Override public int getEffectiveMajorVersion() { return jakartaContext.getEffectiveMajorVersion(); }
        @Override public int getEffectiveMinorVersion() { return jakartaContext.getEffectiveMinorVersion(); }
        @Override public String getMimeType(String file) { return jakartaContext.getMimeType(file); }
        @Override public java.util.Set<String> getResourcePaths(String path) { return jakartaContext.getResourcePaths(path); }
        @Override public java.net.URL getResource(String path) throws java.net.MalformedURLException { return jakartaContext.getResource(path); }
        @Override public java.io.InputStream getResourceAsStream(String path) { return jakartaContext.getResourceAsStream(path); }
        @Override public javax.servlet.RequestDispatcher getRequestDispatcher(String path) { return null; }
        @Override public javax.servlet.RequestDispatcher getNamedDispatcher(String name) { return null; }
        @Override public void log(String msg) { jakartaContext.log(msg); }
        @Override public void log(Exception exception, String msg) { jakartaContext.log(msg, exception); }
        @Override public void log(String message, Throwable throwable) { jakartaContext.log(message, throwable); }
        @Override public String getRealPath(String path) { return jakartaContext.getRealPath(path); }
        @Override public String getServerInfo() { return jakartaContext.getServerInfo(); }
        @Override public String getInitParameter(String name) { return jakartaContext.getInitParameter(name); }
        @Override public java.util.Enumeration<String> getInitParameterNames() { return jakartaContext.getInitParameterNames(); }
        @Override public boolean setInitParameter(String name, String value) { return jakartaContext.setInitParameter(name, value); }
        @Override public Object getAttribute(String name) { return jakartaContext.getAttribute(name); }
        @Override public java.util.Enumeration<String> getAttributeNames() { return jakartaContext.getAttributeNames(); }
        @Override public void setAttribute(String name, Object object) { jakartaContext.setAttribute(name, object); }
        @Override public void removeAttribute(String name) { jakartaContext.removeAttribute(name); }
        @Override public String getServletContextName() { return jakartaContext.getServletContextName(); }
        @Override public java.util.Enumeration<String> getServletNames() { return java.util.Collections.emptyEnumeration(); }
        @Override public java.util.Enumeration<javax.servlet.Servlet> getServlets() { return java.util.Collections.emptyEnumeration(); }
        @Override public javax.servlet.Servlet getServlet(String name) { return null; }
        @Override public javax.servlet.ServletRegistration.Dynamic addServlet(String servletName, String className) { return null; }
        @Override public javax.servlet.ServletRegistration.Dynamic addServlet(String servletName, javax.servlet.Servlet servlet) { return null; }
        @Override public javax.servlet.ServletRegistration.Dynamic addServlet(String servletName, Class<? extends javax.servlet.Servlet> servletClass) { return null; }
        @Override public javax.servlet.ServletRegistration.Dynamic addJspFile(String servletName, String jspFile) { return null; }
        @Override public <T extends javax.servlet.Servlet> T createServlet(Class<T> clazz) { return null; }
        @Override public javax.servlet.ServletRegistration getServletRegistration(String servletName) { return null; }
        @Override public java.util.Map<String, ? extends javax.servlet.ServletRegistration> getServletRegistrations() { return null; }
        @Override public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, String className) { return null; }
        @Override public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, javax.servlet.Filter filter) { return null; }
        @Override public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, Class<? extends javax.servlet.Filter> filterClass) { return null; }
        @Override public <T extends javax.servlet.Filter> T createFilter(Class<T> clazz) { return null; }
        @Override public javax.servlet.FilterRegistration getFilterRegistration(String filterName) { return null; }
        @Override public java.util.Map<String, ? extends javax.servlet.FilterRegistration> getFilterRegistrations() { return null; }
        @Override public javax.servlet.SessionCookieConfig getSessionCookieConfig() { return null; }
        @Override public void setSessionTrackingModes(java.util.Set<javax.servlet.SessionTrackingMode> sessionTrackingModes) {}
        @Override public java.util.Set<javax.servlet.SessionTrackingMode> getDefaultSessionTrackingModes() { return null; }
        @Override public java.util.Set<javax.servlet.SessionTrackingMode> getEffectiveSessionTrackingModes() { return null; }
        @Override public void addListener(String className) {}
        @Override public <T extends java.util.EventListener> void addListener(T t) {}
        @Override public void addListener(Class<? extends java.util.EventListener> listenerClass) {}
        @Override public <T extends java.util.EventListener> T createListener(Class<T> clazz) { return null; }
        @Override public javax.servlet.descriptor.JspConfigDescriptor getJspConfigDescriptor() { return null; }
        @Override public ClassLoader getClassLoader() { return jakartaContext.getClassLoader(); }
        @Override public void declareRoles(String... roleNames) {}
        @Override public String getVirtualServerName() { return jakartaContext.getVirtualServerName(); }
        @Override public int getSessionTimeout() { return jakartaContext.getSessionTimeout(); }
        @Override public void setSessionTimeout(int sessionTimeout) { jakartaContext.setSessionTimeout(sessionTimeout); }
        @Override public String getRequestCharacterEncoding() { return jakartaContext.getRequestCharacterEncoding(); }
        @Override public void setRequestCharacterEncoding(String encoding) { jakartaContext.setRequestCharacterEncoding(encoding); }
        @Override public String getResponseCharacterEncoding() { return jakartaContext.getResponseCharacterEncoding(); }
        @Override public void setResponseCharacterEncoding(String encoding) { jakartaContext.setResponseCharacterEncoding(encoding); }
    }
    
    // Minimal adapter for ServletContext (javax to jakarta)
    private static class JakartaServletContextAdapter implements jakarta.servlet.ServletContext {
        private final javax.servlet.ServletContext javaxContext;
        
        public JakartaServletContextAdapter(javax.servlet.ServletContext javaxContext) {
            this.javaxContext = javaxContext;
        }
        
        @Override public String getContextPath() { return javaxContext.getContextPath(); }
        @Override public jakarta.servlet.ServletContext getContext(String uripath) { return null; }
        @Override public int getMajorVersion() { return javaxContext.getMajorVersion(); }
        @Override public int getMinorVersion() { return javaxContext.getMinorVersion(); }
        @Override public int getEffectiveMajorVersion() { return javaxContext.getEffectiveMajorVersion(); }
        @Override public int getEffectiveMinorVersion() { return javaxContext.getEffectiveMinorVersion(); }
        @Override public String getMimeType(String file) { return javaxContext.getMimeType(file); }
        @Override public java.util.Set<String> getResourcePaths(String path) { return javaxContext.getResourcePaths(path); }
        @Override public java.net.URL getResource(String path) throws java.net.MalformedURLException { return javaxContext.getResource(path); }
        @Override public java.io.InputStream getResourceAsStream(String path) { return javaxContext.getResourceAsStream(path); }
        @Override public jakarta.servlet.RequestDispatcher getRequestDispatcher(String path) { return null; }
        @Override public jakarta.servlet.RequestDispatcher getNamedDispatcher(String name) { return null; }
        @Override public void log(String msg) { javaxContext.log(msg); }
        @Override public void log(String message, Throwable throwable) { javaxContext.log(message, throwable); }
        @Override public String getRealPath(String path) { return javaxContext.getRealPath(path); }
        @Override public String getServerInfo() { return javaxContext.getServerInfo(); }
        @Override public String getInitParameter(String name) { return javaxContext.getInitParameter(name); }
        @Override public java.util.Enumeration<String> getInitParameterNames() { return javaxContext.getInitParameterNames(); }
        @Override public boolean setInitParameter(String name, String value) { return javaxContext.setInitParameter(name, value); }
        @Override public Object getAttribute(String name) { return javaxContext.getAttribute(name); }
        @Override public java.util.Enumeration<String> getAttributeNames() { return javaxContext.getAttributeNames(); }
        @Override public void setAttribute(String name, Object object) { javaxContext.setAttribute(name, object); }
        @Override public void removeAttribute(String name) { javaxContext.removeAttribute(name); }
        @Override public String getServletContextName() { return javaxContext.getServletContextName(); }
        @Override public jakarta.servlet.ServletRegistration.Dynamic addServlet(String servletName, String className) { return null; }
        @Override public jakarta.servlet.ServletRegistration.Dynamic addServlet(String servletName, jakarta.servlet.Servlet servlet) { return null; }
        @Override public jakarta.servlet.ServletRegistration.Dynamic addServlet(String servletName, Class<? extends jakarta.servlet.Servlet> servletClass) { return null; }
        @Override public jakarta.servlet.ServletRegistration.Dynamic addJspFile(String servletName, String jspFile) { return null; }
        @Override public <T extends jakarta.servlet.Servlet> T createServlet(Class<T> clazz) { return null; }
        @Override public jakarta.servlet.ServletRegistration getServletRegistration(String servletName) { return null; }
        @Override public java.util.Map<String, ? extends jakarta.servlet.ServletRegistration> getServletRegistrations() { return null; }
        @Override public jakarta.servlet.FilterRegistration.Dynamic addFilter(String filterName, String className) { return null; }
        @Override public jakarta.servlet.FilterRegistration.Dynamic addFilter(String filterName, jakarta.servlet.Filter filter) { return null; }
        @Override public jakarta.servlet.FilterRegistration.Dynamic addFilter(String filterName, Class<? extends jakarta.servlet.Filter> filterClass) { return null; }
        @Override public <T extends jakarta.servlet.Filter> T createFilter(Class<T> clazz) { return null; }
        @Override public jakarta.servlet.FilterRegistration getFilterRegistration(String filterName) { return null; }
        @Override public java.util.Map<String, ? extends jakarta.servlet.FilterRegistration> getFilterRegistrations() { return null; }
        @Override public jakarta.servlet.SessionCookieConfig getSessionCookieConfig() { return null; }
        @Override public void setSessionTrackingModes(java.util.Set<jakarta.servlet.SessionTrackingMode> sessionTrackingModes) {}
        @Override public java.util.Set<jakarta.servlet.SessionTrackingMode> getDefaultSessionTrackingModes() { return null; }
        @Override public java.util.Set<jakarta.servlet.SessionTrackingMode> getEffectiveSessionTrackingModes() { return null; }
        @Override public void addListener(String className) {}
        @Override public <T extends java.util.EventListener> void addListener(T t) {}
        @Override public void addListener(Class<? extends java.util.EventListener> listenerClass) {}
        @Override public <T extends java.util.EventListener> T createListener(Class<T> clazz) { return null; }
        @Override public jakarta.servlet.descriptor.JspConfigDescriptor getJspConfigDescriptor() { return null; }
        @Override public ClassLoader getClassLoader() { return javaxContext.getClassLoader(); }
        @Override public void declareRoles(String... roleNames) {}
        @Override public String getVirtualServerName() { return javaxContext.getVirtualServerName(); }
        @Override public int getSessionTimeout() { return javaxContext.getSessionTimeout(); }
        @Override public void setSessionTimeout(int sessionTimeout) { javaxContext.setSessionTimeout(sessionTimeout); }
        @Override public String getRequestCharacterEncoding() { return javaxContext.getRequestCharacterEncoding(); }
        @Override public void setRequestCharacterEncoding(String encoding) { javaxContext.setRequestCharacterEncoding(encoding); }
        @Override public String getResponseCharacterEncoding() { return javaxContext.getResponseCharacterEncoding(); }
        @Override public void setResponseCharacterEncoding(String encoding) { javaxContext.setResponseCharacterEncoding(encoding); }
    }
}