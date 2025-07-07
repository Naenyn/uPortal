package org.apereo.portal.utils.web;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.util.WebUtils;

/**
 * Utility constants for portal web functionality
 */
public final class PortalWebUtils {
    
    /**
     * Request attribute key for the request mutex
     */
    public static final String REQUEST_MUTEX_ATTRIBUTE = "org.apereo.portal.utils.web.REQUEST_MUTEX";
    
    private PortalWebUtils() {
        // Utility class
    }
    
    /**
     * Get a mutex object for synchronizing access to request attributes
     */
    public static Object getRequestAttributeMutex(HttpServletRequest request) {
        Object mutex = request.getAttribute(REQUEST_MUTEX_ATTRIBUTE);
        if (mutex == null) {
            mutex = new Object();
            request.setAttribute(REQUEST_MUTEX_ATTRIBUTE, mutex);
        }
        return mutex;
    }
    
    /**
     * Get a map from request attributes, creating it if it doesn't exist
     */
    @SuppressWarnings("unchecked")
    public static <K, V> ConcurrentMap<K, V> getMapRequestAttribute(HttpServletRequest request, String attributeName) {
        ConcurrentMap<K, V> map = (ConcurrentMap<K, V>) request.getAttribute(attributeName);
        if (map == null) {
            map = new ConcurrentHashMap<K, V>();
            request.setAttribute(attributeName, map);
        }
        return map;
    }
    
    /**
     * Get a map from session attributes, creating it if it doesn't exist
     */
    @SuppressWarnings("unchecked")
    public static <K, V> ConcurrentMap<K, V> getMapSessionAttribute(HttpSession session, String attributeName) {
        final Object mutex = WebUtils.getSessionMutex(session);
        synchronized (mutex) {
            ConcurrentMap<K, V> map = (ConcurrentMap<K, V>) session.getAttribute(attributeName);
            if (map == null) {
                map = new ConcurrentHashMap<K, V>();
                session.setAttribute(attributeName, map);
            }
            return map;
        }
    }
}