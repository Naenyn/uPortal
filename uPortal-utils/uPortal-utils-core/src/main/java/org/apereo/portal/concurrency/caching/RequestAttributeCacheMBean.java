package org.apereo.portal.concurrency.caching;

import java.util.Map;

/**
 * MBean interface for request attribute cache monitoring
 */
public interface RequestAttributeCacheMBean {
    String getCacheName();
    int getCacheSize();
    Map<String, Object> getCacheEntries();
}