package org.springframework.webflow.mvc.portlet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import org.springframework.webflow.execution.FlowExecutionOutcome;

/**
 * Compatibility class for Spring WebFlow portlet AbstractFlowHandler
 * Maintains Portlet 2.0 functionality while using Spring 6
 */
public abstract class AbstractFlowHandler {
    
    public abstract String getFlowId();
    
    public boolean handleExecutionOutcome(
            FlowExecutionOutcome outcome, ActionRequest request, ActionResponse response) {
        return false;
    }
}