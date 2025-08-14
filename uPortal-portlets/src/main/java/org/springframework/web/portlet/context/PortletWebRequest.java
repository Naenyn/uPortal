package org.springframework.web.portlet.context;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import org.springframework.web.context.request.WebRequest;

/**
 * Compatibility class for Spring portlet PortletWebRequest
 */
public class PortletWebRequest implements WebRequest {
    private final PortletRequest request;
    private final PortletResponse response;
    
    public PortletWebRequest(PortletRequest request) {
        this.request = request;
        this.response = null;
    }
    
    public PortletWebRequest(PortletRequest request, PortletResponse response) {
        this.request = request;
        this.response = response;
    }
    
    // Minimal WebRequest implementation
    @Override
    public String getHeader(String headerName) {
        return null;
    }
    
    @Override
    public String[] getHeaderValues(String headerName) {
        return new String[0];
    }
    
    @Override
    public java.util.Iterator<String> getHeaderNames() {
        return java.util.Collections.emptyIterator();
    }
    
    @Override
    public String getParameter(String paramName) {
        return request.getParameter(paramName);
    }
    
    @Override
    public String[] getParameterValues(String paramName) {
        return request.getParameterValues(paramName);
    }
    
    @Override
    public java.util.Iterator<String> getParameterNames() {
        return java.util.Collections.list(request.getParameterNames()).iterator();
    }
    
    @Override
    public java.util.Map<String, String[]> getParameterMap() {
        return request.getParameterMap();
    }
    
    @Override
    public java.util.Locale getLocale() {
        return request.getLocale();
    }
    
    @Override
    public String getContextPath() {
        return request.getContextPath();
    }
    
    @Override
    public String getRemoteUser() {
        return request.getRemoteUser();
    }
    
    @Override
    public java.security.Principal getUserPrincipal() {
        return request.getUserPrincipal();
    }
    
    @Override
    public boolean isUserInRole(String role) {
        return request.isUserInRole(role);
    }
    
    @Override
    public boolean isSecure() {
        return request.isSecure();
    }
    
    @Override
    public boolean checkNotModified(long lastModifiedTimestamp) {
        return false;
    }
    
    @Override
    public boolean checkNotModified(String etag) {
        return false;
    }
    
    @Override
    public boolean checkNotModified(String etag, long lastModifiedTimestamp) {
        return false;
    }
    
    @Override
    public String getDescription(boolean includeClientInfo) {
        return "PortletWebRequest";
    }
    
    @Override
    public Object getAttribute(String name, int scope) {
        return request.getAttribute(name);
    }
    
    @Override
    public void setAttribute(String name, Object value, int scope) {
        request.setAttribute(name, value);
    }
    
    @Override
    public void removeAttribute(String name, int scope) {
        request.removeAttribute(name);
    }
    
    @Override
    public String[] getAttributeNames(int scope) {
        return java.util.Collections.list(request.getAttributeNames()).toArray(new String[0]);
    }
    
    @Override
    public void registerDestructionCallback(String name, Runnable callback, int scope) {
        // No-op for compatibility
    }
    
    @Override
    public Object resolveReference(String key) {
        return null;
    }
    
    @Override
    public String getSessionId() {
        return request.getPortletSession().getId();
    }
    
    @Override
    public Object getSessionMutex() {
        return request.getPortletSession();
    }
}