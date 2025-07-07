package org.apereo.portal.concurrency;

import java.util.Date;

public interface IEntityLock {
    String getKey();
    Class<?> getType();
    Date getExpirationTime();
    String getOwner();
    int getLockType();
    
    // Additional methods needed by implementations
    String getEntityKey();
    Class<?> getEntityType();
    String getLockOwner();
    
    void convert(int newType);
    void convert(int newType, int newDuration);
    boolean isValid();
    void release();
    void renew();
    void renew(int duration);
}