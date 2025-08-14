package org.springframework.web.portlet.util;

import javax.portlet.PortletSession;

/**
 * Compatibility class for Spring portlet PortletUtils
 * Maintains Portlet 2.0 functionality while using Spring 6
 */
public class PortletUtils {
    
    public static Object getSessionMutex(PortletSession session) {
        // Return session itself as mutex for synchronization
        return session;
    }
}