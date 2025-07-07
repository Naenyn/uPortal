package org.apereo.portal.concurrency.caching;

import java.io.Serializable;

/**
 * Interface for generating cache keys
 */
public interface CacheKeyGenerator<T, K extends Serializable> {
    K generateKey(T... args);
}