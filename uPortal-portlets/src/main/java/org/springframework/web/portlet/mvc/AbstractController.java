package org.springframework.web.portlet.mvc;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import org.springframework.web.portlet.ModelAndView;

/**
 * Compatibility class for Spring portlet AbstractController
 */
public abstract class AbstractController {
    
    protected abstract ModelAndView handleRenderRequestInternal(
            RenderRequest request, RenderResponse response) throws Exception;
}