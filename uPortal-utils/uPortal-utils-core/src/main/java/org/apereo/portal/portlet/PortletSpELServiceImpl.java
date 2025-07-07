package org.apereo.portal.portlet;

import java.lang.reflect.Constructor;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.MapAccessor;
import org.springframework.core.convert.ConversionService;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

/**
 * Implementation of {@link IPortletSpELService} that uses Spring Expression Language to parse
 * expressions
 */
@Service
public class PortletSpELServiceImpl implements IPortletSpELService {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private final ExpressionParser expressionParser = new SpelExpressionParser();
    private WebApplicationContext applicationContext;
    private ConversionService conversionService;
    private Class<?> portletWebRequestClass;
    
    @Autowired
    public void setApplicationContext(WebApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
    @Autowired
    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }
    
    public PortletSpELServiceImpl() {
        try {
            this.portletWebRequestClass = Class.forName("org.springframework.web.portlet.context.PortletWebRequest");
        }
        catch (ClassNotFoundException e) {
            this.logger.debug("PortletWebRequest class not found, portlet request support will be disabled", e);
        }
    }

    public boolean hasExpression(String expressionString) {
        if (expressionString == null) {
            return false;
        }
        
        return expressionString.contains("${") && expressionString.contains("}");
    }

    @Override
    public String parseString(String expressionString, javax.portlet.PortletRequest request) {
        if (!this.hasExpression(expressionString)) {
            return expressionString;
        }
        
        final Expression expression = this.expressionParser.parseExpression(expressionString, ParserContext.TEMPLATE_EXPRESSION);
        final EvaluationContext evaluationContext = this.getEvaluationContext(request);
        
        return expression.getValue(evaluationContext, String.class);
    }

    @Override
    public <T> T getValue(String expressionString, javax.portlet.PortletRequest request, Class<T> desiredResultType) {
        final Expression expression = this.expressionParser.parseExpression(expressionString, ParserContext.TEMPLATE_EXPRESSION);
        final EvaluationContext evaluationContext = this.getEvaluationContext(request);
        
        return expression.getValue(evaluationContext, desiredResultType);
    }
    
    @Override
    public Expression parseExpression(String expressionString) {
        return this.expressionParser.parseExpression(expressionString, ParserContext.TEMPLATE_EXPRESSION);
    }

    /**
     * Create an evaluation context for the specified request
     * 
     * @param request The request to create an evaluation context for
     * @return The evaluation context
     */
    @SuppressWarnings("unchecked")
    protected EvaluationContext getEvaluationContext(Object request) {
        final StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        evaluationContext.addPropertyAccessor(new MapAccessor());
        evaluationContext.setBeanResolver(new BeanFactoryResolver(this.applicationContext));
        if (this.conversionService != null) {
            try {
                evaluationContext.setTypeConverter(new org.springframework.expression.TypeConverter() {
                    @Override
                    public boolean canConvert(org.springframework.core.convert.TypeDescriptor sourceType, org.springframework.core.convert.TypeDescriptor targetType) {
                        return conversionService.canConvert(sourceType, targetType);
                    }
                    
                    @Override
                    public Object convertValue(Object value, org.springframework.core.convert.TypeDescriptor sourceType, org.springframework.core.convert.TypeDescriptor targetType) {
                        return conversionService.convert(value, sourceType, targetType);
                    }
                });
            } catch (Exception e) {
                logger.debug("Failed to set conversion service", e);
            }
        }
        
        // Add user info attributes if available
        Map<String, String> userInfo = null;
        try {
            if (request != null) {
                userInfo = (Map<String, String>) request.getClass().getMethod("getAttribute", String.class).invoke(request, "USER_INFO");
            }
        }
        catch (Exception e) {
            this.logger.debug("Failed to get USER_INFO from request: " + request, e);
        }
        
        if (userInfo != null) {
            evaluationContext.setVariable("userInfo", userInfo);
        }
        
        // Create a PortletWebRequest if possible
        if (this.portletWebRequestClass != null && request != null) {
            try {
                Constructor<?> constructor = portletWebRequestClass.getConstructor(Object.class);
                Object portletWebRequest = constructor.newInstance(request);
                evaluationContext.setVariable("portletRequest", portletWebRequest);
            }
            catch (Exception e) {
                this.logger.debug("Failed to create PortletWebRequest for request: " + request, e);
            }
        }
        
        return evaluationContext;
    }
}