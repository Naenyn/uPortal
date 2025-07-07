package org.apereo.portal.utils.cache;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Listens for session destruction events and purges any cache entries that are tagged with the
 * destroyed session's id
 */
public class SessionIdTaggedCacheEntryPurger
        implements HttpSessionListener, ApplicationListener<ContextRefreshedEvent>, DisposableBean {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private final ConcurrentMap<String, CacheManager> cacheManagers = new ConcurrentHashMap<String, CacheManager>();
    
    @Autowired
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManagers.put(cacheManager.getName(), cacheManager);
    }
    
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // Nothing to do
    }
    
    @Override
    public void destroy() throws Exception {
        this.cacheManagers.clear();
    }
    
    @Override
    public void sessionCreated(HttpSessionEvent event) {
        // Nothing to do
    }
    
    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        final HttpSession session = event.getSession();
        final String sessionId = session.getId();
        
        for (final CacheManager cacheManager : this.cacheManagers.values()) {
            final String[] cacheNames = cacheManager.getCacheNames();
            for (final String cacheName : cacheNames) {
                final Ehcache cache = cacheManager.getEhcache(cacheName);
                if (cache == null) {
                    continue;
                }
                
                final java.util.List<?> keysList = cache.getKeys();
                final Set<?> keys = new java.util.HashSet<>(keysList);
                for (final Object key : keys) {
                    final Element element = cache.get(key);
                    if (element == null) {
                        continue;
                    }
                    
                    final Object value = element.getObjectValue();
                    if (value instanceof SessionIdTaggedCacheEntry) {
                        final SessionIdTaggedCacheEntry<?> taggedCacheEntry = (SessionIdTaggedCacheEntry<?>)value;
                        final String entrySessionId = taggedCacheEntry.getSessionId();
                        if (sessionId.equals(entrySessionId)) {
                            if (this.logger.isDebugEnabled()) {
                                this.logger.debug("Removing cache entry for key " + key + " from cache " + cacheName + " due to destruction of session " + sessionId);
                            }
                            
                            cache.remove(key);
                        }
                    }
                }
            }
        }
    }
}