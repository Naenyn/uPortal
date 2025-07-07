package org.apereo.portal.url;

import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Utilities for working with portal requests
 */
public interface IPortalRequestUtils {
    /**
     * Get the original portal request from the specified request
     * 
     * @param request The current request
     * @return The original portal request
     */
    HttpServletRequest getOriginalPortalRequest(HttpServletRequest request);
    
    /**
     * Get the original portal request from the specified request
     * 
     * @param request The current request
     * @return The original portal request
     */
    HttpServletRequest getOriginalPortalRequest(Object request);
    
    /**
     * Get the HttpServletRequest for a portlet request
     * 
     * @param portletRequest The portlet request
     * @return The HttpServletRequest
     */
    HttpServletRequest getPortletHttpRequest(Object portletRequest);
    
    /**
     * Get the original portal response from the specified request
     * 
     * @param request The current request
     * @return The original portal response
     */
    HttpServletResponse getOriginalPortalResponse(HttpServletRequest request);
    
    /**
     * Get the original portal response from the specified request
     * 
     * @param portletRequest The portlet request
     * @return The original portal response
     */
    HttpServletResponse getOriginalPortalResponse(Object portletRequest);
    
    /**
     * Get the original portal response from the specified response
     * 
     * @param response The current response
     * @return The original portal response
     */
    HttpServletResponse getOriginalPortalResponse(HttpServletResponse response);
    
    /**
     * Get the URL for the current request
     * 
     * @param request The current request
     * @return The URL for the current request
     */
    String getRequestURL(HttpServletRequest request);
    
    /**
     * Get the URL for the current request with the specified parameters
     * 
     * @param request The current request
     * @param parameters The parameters to add to the URL
     * @return The URL for the current request with the specified parameters
     */
    String getRequestURL(HttpServletRequest request, Map<String, String[]> parameters);
    
    /**
     * Get the current portal request from the RequestContextHolder
     * 
     * @return The current portal request
     */
    HttpServletRequest getCurrentPortalRequest();
}