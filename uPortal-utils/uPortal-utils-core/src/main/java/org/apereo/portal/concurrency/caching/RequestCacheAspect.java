package org.apereo.portal.concurrency.caching;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Aspect that provides request level caching for methods annotated with {@link RequestCache}
 */
@Aspect
public class RequestCacheAspect implements InitializingBean, DisposableBean {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private ApplicationContext applicationContext;
    private MBeanExporter mBeanExporter;
    private final ConcurrentMap<String, CacheKeyGenerator<?, ?>> cacheKeyGeneratorCache = new ConcurrentHashMap<String, CacheKeyGenerator<?, ?>>();
    private final ConcurrentMap<String, Object> mbeanNames = new ConcurrentHashMap<String, Object>();
    
    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
    @Autowired(required = false)
    public void setMBeanExporter(MBeanExporter mBeanExporter) {
        this.mBeanExporter = mBeanExporter;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        // Nothing to do
    }
    
    @Override
    public void destroy() throws Exception {
        // Nothing to do
    }
    
    @Around("execution(* *(..)) && @annotation(org.apereo.portal.concurrency.caching.RequestCache)")
    public Object cacheAroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        final MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        final Method method = methodSignature.getMethod();
        final RequestCache requestCache = AnnotationUtils.findAnnotation(method, RequestCache.class);
        
        // If no request cache annotation, just proceed
        if (requestCache == null) {
            return joinPoint.proceed();
        }
        
        // Get the request attributes
        final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        
        // If no request attributes, just proceed
        if (requestAttributes == null) {
            return joinPoint.proceed();
        }
        
        // Get the cache key
        final String cacheName = this.getCacheName(requestCache, method);
        final CacheKey cacheKey = this.getCacheKey(requestCache, method, joinPoint.getArgs());
        
        // Register the cache as an MBean if needed
        if (this.mBeanExporter != null && !this.mbeanNames.containsKey(cacheName)) {
            final Object mbeanName = new Object();
            if (this.mbeanNames.putIfAbsent(cacheName, mbeanName) == null) {
                try {
                    final RequestAttributeCache requestAttributeCache = new RequestAttributeCache(cacheName);
                    final String nameString = "org.apereo.portal.concurrency.caching:type=RequestAttributeCache,name=" + cacheName;
                    
                    this.registerMbean(requestAttributeCache, nameString);
                }
                catch (Exception e) {
                    this.logger.warn("Failed to register cache MBean for: " + cacheName, e);
                }
            }
        }
        
        // Get the cache map from the request
        final Map<CacheKey, Object> cacheMap = this.getRequestCacheMap(requestAttributes, cacheName);
        
        // Check if the value is already cached
        if (cacheMap.containsKey(cacheKey)) {
            return cacheMap.get(cacheKey);
        }
        
        // Execute the method
        final Object result = joinPoint.proceed();
        
        // Cache the result
        cacheMap.put(cacheKey, result);
        
        return result;
    }
    
    /**
     * Get the cache name for the specified method
     * 
     * @param requestCache The request cache annotation
     * @param method The method
     * @return The cache name
     */
    protected String getCacheName(RequestCache requestCache, Method method) {
        String cacheName = requestCache.cacheName();
        if (StringUtils.isEmpty(cacheName)) {
            cacheName = method.getDeclaringClass().getName() + "." + method.getName();
        }
        return cacheName;
    }
    
    /**
     * Get the cache key for the specified method and arguments
     * 
     * @param requestCache The request cache annotation
     * @param method The method
     * @param args The arguments
     * @return The cache key
     */
    @SuppressWarnings("unchecked")
    protected CacheKey getCacheKey(RequestCache requestCache, Method method, Object[] args) {
        final String keyGeneratorBeanName = requestCache.keyGeneratorBeanName();
        
        if (StringUtils.isEmpty(keyGeneratorBeanName)) {
            return new CacheKey(method, args);
        }
        
        CacheKeyGenerator<Object, CacheKey> cacheKeyGenerator = (CacheKeyGenerator<Object, CacheKey>)this.cacheKeyGeneratorCache.get(keyGeneratorBeanName);
        if (cacheKeyGenerator == null) {
            cacheKeyGenerator = (CacheKeyGenerator<Object, CacheKey>)this.applicationContext.getBean(keyGeneratorBeanName);
            this.cacheKeyGeneratorCache.put(keyGeneratorBeanName, cacheKeyGenerator);
        }
        
        return cacheKeyGenerator.generateKey(args);
    }
    
    /**
     * Get the cache map for the specified request attributes and cache name
     * 
     * @param requestAttributes The request attributes
     * @param cacheName The cache name
     * @return The cache map
     */
    @SuppressWarnings("unchecked")
    protected Map<CacheKey, Object> getRequestCacheMap(RequestAttributes requestAttributes, String cacheName) {
        final String attributeName = RequestCache.class.getName() + "." + cacheName;
        Map<CacheKey, Object> cacheMap = (Map<CacheKey, Object>)requestAttributes.getAttribute(attributeName, RequestAttributes.SCOPE_REQUEST);
        if (cacheMap == null) {
            cacheMap = new HashMap<CacheKey, Object>();
            requestAttributes.setAttribute(attributeName, cacheMap, RequestAttributes.SCOPE_REQUEST);
        }
        return cacheMap;
    }
    
    /**
     * Register an MBean with the MBean server
     * 
     * @param object The object to register
     * @param name The name to register it under
     * @throws Exception If an error occurs
     */
    protected void registerMbean(Object object, String nameString) throws Exception {
        if (this.mBeanExporter != null) {
            try {
                javax.management.ObjectName objectName = new javax.management.ObjectName(nameString);
                this.mBeanExporter.registerManagedResource(object, objectName);
            }
            catch (Exception e) {
                this.logger.warn("Failed to register MBean: " + nameString, e);
            }
        }
    }
    
    /**
     * Cache key for request cache
     */
    public static class CacheKey implements java.io.Serializable {
        private final Method method;
        private final Object[] args;
        private final int hashCode;
        
        public CacheKey(Method method, Object[] args) {
            this.method = method;
            this.args = args;
            
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode(this.args);
            result = prime * result + ((this.method == null) ? 0 : this.method.hashCode());
            this.hashCode = result;
        }
        
        @Override
        public int hashCode() {
            return this.hashCode;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            CacheKey other = (CacheKey) obj;
            if (!Arrays.equals(args, other.args))
                return false;
            if (method == null) {
                if (other.method != null)
                    return false;
            } else if (!method.equals(other.method))
                return false;
            return true;
        }
        
        @Override
        public String toString() {
            return "CacheKey [method=" + method + ", args=" + Arrays.toString(args) + "]";
        }
    }
    
    /**
     * MBean for monitoring request attribute caches
     */
    public static class RequestAttributeCache implements RequestAttributeCacheMBean {
        private final String cacheName;
        
        public RequestAttributeCache(String cacheName) {
            this.cacheName = cacheName;
        }
        
        @Override
        public String getCacheName() {
            return this.cacheName;
        }
        
        @Override
        public int getCacheSize() {
            final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes == null) {
                return 0;
            }
            
            final String attributeName = RequestCache.class.getName() + "." + this.cacheName;
            @SuppressWarnings("unchecked")
            final Map<CacheKey, Object> cacheMap = (Map<CacheKey, Object>)requestAttributes.getAttribute(attributeName, RequestAttributes.SCOPE_REQUEST);
            if (cacheMap == null) {
                return 0;
            }
            
            return cacheMap.size();
        }
        
        @Override
        public Map<String, Object> getCacheEntries() {
            final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes == null) {
                return Collections.emptyMap();
            }
            
            final String attributeName = RequestCache.class.getName() + "." + this.cacheName;
            @SuppressWarnings("unchecked")
            final Map<CacheKey, Object> cacheMap = (Map<CacheKey, Object>)requestAttributes.getAttribute(attributeName, RequestAttributes.SCOPE_REQUEST);
            if (cacheMap == null) {
                return Collections.emptyMap();
            }
            
            final Map<String, Object> result = new HashMap<String, Object>();
            for (final Map.Entry<CacheKey, Object> entry : cacheMap.entrySet()) {
                result.put(entry.getKey().toString(), entry.getValue());
            }
            
            return result;
        }
    }
}