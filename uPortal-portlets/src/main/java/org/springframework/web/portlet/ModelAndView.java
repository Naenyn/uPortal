package org.springframework.web.portlet;

import java.util.Map;

/**
 * Compatibility class for Spring portlet ModelAndView
 * Maintains Portlet 2.0 functionality while using Spring 6
 */
public class ModelAndView {
    private String viewName;
    private Map<String, Object> model;
    
    public ModelAndView() {}
    
    public ModelAndView(String viewName) {
        this.viewName = viewName;
    }
    
    public ModelAndView(String viewName, Map<String, Object> model) {
        this.viewName = viewName;
        this.model = model;
    }
    
    public ModelAndView(String viewName, String modelName, Object modelObject) {
        this.viewName = viewName;
        addObject(modelName, modelObject);
    }
    
    public void setViewName(String viewName) {
        this.viewName = viewName;
    }
    
    public String getViewName() {
        return this.viewName;
    }
    
    public void setModel(Map<String, Object> model) {
        this.model = model;
    }
    
    public Map<String, Object> getModel() {
        return this.model;
    }
    
    public ModelAndView addObject(String attributeName, Object attributeValue) {
        if (this.model == null) {
            this.model = new java.util.HashMap<>();
        }
        this.model.put(attributeName, attributeValue);
        return this;
    }
    
    public ModelAndView addAllObjects(Map<String, ?> modelMap) {
        if (this.model == null) {
            this.model = new java.util.HashMap<>();
        }
        if (modelMap != null) {
            this.model.putAll(modelMap);
        }
        return this;
    }
}