package org.apereo.portal.url;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Interface for customizing URLs in the portal.
 */
public interface IAuthUrlCustomizer {
    /**
     * Determines if this customizer supports the specified URL.
     * 
     * @param request The current request
     * @param url The URL to check
     * @return true if this customizer supports the URL, false otherwise
     */
    boolean supports(HttpServletRequest request, String url);
    
    /**
     * Customizes the specified URL.
     * 
     * @param request The current request
     * @param url The URL to customize
     * @return The customized URL
     */
    String customizeUrl(HttpServletRequest request, String url);
}