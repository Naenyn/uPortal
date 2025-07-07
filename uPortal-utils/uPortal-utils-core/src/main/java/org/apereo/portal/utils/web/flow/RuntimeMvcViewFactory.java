package org.apereo.portal.utils.web.flow;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ViewFactory;
import org.springframework.webflow.execution.Event;

/**
 * Simple ViewFactory implementation that creates Spring MVC Views
 */
public class RuntimeMvcViewFactory implements ViewFactory {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private final ViewResolver viewResolver;
    private final String viewResolverBeanName;
    private final ApplicationContext applicationContext;
    
    @Autowired
    public RuntimeMvcViewFactory(ViewResolver viewResolver, String viewResolverBeanName, ApplicationContext applicationContext) {
        this.viewResolver = viewResolver;
        this.viewResolverBeanName = viewResolverBeanName;
        this.applicationContext = applicationContext;
    }

    @Override
    public org.springframework.webflow.execution.View getView(RequestContext context) {
        final String viewName = context.getCurrentState().getId();
        
        try {
            final View view = resolveView(viewName, null);
            
            return new org.springframework.webflow.execution.View() {
                public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
                    final Map<String, Object> newModel = new HashMap<String, Object>(model);
                    view.render(newModel, request, response);
                }
                
                @Override
                public void render() {
                    // Default implementation - no-op
                }
                
                @Override
                public boolean userEventQueued() {
                    return false;
                }
                
                @Override
                public void processUserEvent() {
                    // No-op
                }
                
                @Override
                public Serializable getUserEventState() {
                    return null;
                }
                
                @Override
                public boolean hasFlowEvent() {
                    return false;
                }
                
                @Override
                public Event getFlowEvent() {
                    return null;
                }
                
                @Override
                public void saveState() {
                    // No-op
                }
            };
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to create view: " + viewName, e);
        }
    }
    
    protected View resolveView(String viewName, HttpServletRequest nativeRequest) throws Exception {
        try {
            return this.viewResolver.resolveViewName(viewName, null);
        }
        catch (Exception e) {
            this.logger.warn("Failed to resolve view: " + viewName + " using ViewResolver: " + this.viewResolverBeanName, e);
            return new InternalResourceView(viewName);
        }
    }
}