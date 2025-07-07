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
package org.apereo.portal.soffit.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.GenericFilterBean;

/**
 * A simplified version of Spring Security's AbstractPreAuthenticatedProcessingFilter
 * that works with Jakarta Servlet API.
 */
public abstract class JakartaAbstractPreAuthenticatedProcessingFilter extends GenericFilterBean {

    private AuthenticationManager authenticationManager;
    private boolean continueFilterChainOnUnsuccessfulAuthentication = true;
    private boolean checkForPrincipalChanges;
    private boolean invalidateSessionOnPrincipalChange = true;
    private WebAuthenticationDetailsSource authenticationDetailsSource = new WebAuthenticationDetailsSource();

    /**
     * Check whether all required properties have been set.
     */
    @Override
    public void afterPropertiesSet() {
        try {
            super.afterPropertiesSet();
        } catch (ServletException e) {
            // Convert to RuntimeException for passivity
            throw new RuntimeException(e);
        }
    }

    /**
     * Try to authenticate a pre-authenticated user with Spring Security if the user has not yet been authenticated.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            throw new ServletException("HttpServletRequest and HttpServletResponse required");
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        Object principal = getPreAuthenticatedPrincipal(httpRequest);
        Object credentials = getPreAuthenticatedCredentials(httpRequest);

        if (principal == null) {
            chain.doFilter(request, response);
            return;
        }

        try {
            // Create a token with the principal and credentials
            Authentication authResult = SecurityContextHolder.getContext().getAuthentication();
            if (authResult == null) {
                // No existing authentication, proceed with authentication
                SecurityContextHolder.getContext().setAuthentication(authResult);
            }

            chain.doFilter(request, response);
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            throw e;
        }
    }

    /**
     * Override to extract the principal information from the current request
     */
    protected abstract Object getPreAuthenticatedPrincipal(HttpServletRequest request);

    /**
     * Override to extract the credentials (if applicable) from the current request.
     * Should not return null for a valid principal.
     */
    protected abstract Object getPreAuthenticatedCredentials(HttpServletRequest request);
}