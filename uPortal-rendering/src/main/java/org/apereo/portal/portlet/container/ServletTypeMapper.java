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
        @Override public void setCharacterEncoding(String env) { jakartaRequest.setCharacterEncoding(env); }
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
}