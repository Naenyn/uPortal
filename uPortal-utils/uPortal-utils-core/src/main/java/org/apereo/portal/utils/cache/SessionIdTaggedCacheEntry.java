package org.apereo.portal.utils.cache;

import java.io.Serializable;

/**
 * Cache entry tagged with session ID
 */
public class SessionIdTaggedCacheEntry<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final T value;
    private final String sessionId;
    
    public SessionIdTaggedCacheEntry(T value, String sessionId) {
        this.value = value;
        this.sessionId = sessionId;
    }
    
    public T getValue() {
        return value;
    }
    
    public String getSessionId() {
        return sessionId;
    }
}