package org.apereo.portal.security.mvc;

import jakarta.servlet.http.HttpServletRequest;

public interface ILoginRedirect {

    /*
     * Return redirect URL or null to bypass redirect.
     */
    String redirectTarget(HttpServletRequest request);
}
