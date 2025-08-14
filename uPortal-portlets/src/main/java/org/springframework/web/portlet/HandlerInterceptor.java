package org.springframework.web.portlet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

/**
 * Compatibility interface for Spring portlet HandlerInterceptor
 */
public interface HandlerInterceptor {
    
    default boolean preHandleAction(ActionRequest request, ActionResponse response, Object handler) throws Exception {
        return true;
    }
    
    default void afterActionCompletion(ActionRequest request, ActionResponse response, Object handler, Exception ex) throws Exception {
    }
    
    default boolean preHandleRender(RenderRequest request, RenderResponse response, Object handler) throws Exception {
        return true;
    }
    
    default void postHandleRender(RenderRequest request, RenderResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }
    
    default void afterRenderCompletion(RenderRequest request, RenderResponse response, Object handler, Exception ex) throws Exception {
    }
    
    default boolean preHandleResource(ResourceRequest request, ResourceResponse response, Object handler) throws Exception {
        return true;
    }
    
    default void postHandleResource(ResourceRequest request, ResourceResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }
    
    default void afterResourceCompletion(ResourceRequest request, ResourceResponse response, Object handler, Exception ex) throws Exception {
    }
    
    default boolean preHandleEvent(EventRequest request, EventResponse response, Object handler) throws Exception {
        return true;
    }
    
    default void afterEventCompletion(EventRequest request, EventResponse response, Object handler, Exception ex) throws Exception {
    }
}