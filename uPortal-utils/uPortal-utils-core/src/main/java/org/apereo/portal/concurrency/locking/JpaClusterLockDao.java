package org.apereo.portal.concurrency.locking;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/** JPA implementation of the cluster lock DAO */
@Repository
public class JpaClusterLockDao implements IClusterLockDao {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private EntityManager entityManager;

    /** @param entityManager the entityManager to set */
    @PersistenceContext(unitName = "uPortalPersistence")
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ClusterMutex getClusterMutex(String mutexName) {
        final TypedQuery<ClusterMutex> query = createMutexQuery(mutexName);
        final List<ClusterMutex> results = query.getResultList();
        return DataAccessUtils.uniqueResult(results);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ClusterMutex getOrCreateClusterMutex(String mutexName) {
        ClusterMutex mutex = this.getClusterMutex(mutexName);
        if (mutex == null) {
            mutex = new ClusterMutex(mutexName);
            this.entityManager.persist(mutex);
        }

        return mutex;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean getLock(String mutexName, String serverName, long lockTimeout) {
        final Date now = new Date();
        final Date expires = new Date(now.getTime() + lockTimeout);

        final ClusterMutex mutex = this.getOrCreateClusterMutex(mutexName);

        // If the mutex is locked check if it is expired or owned by this server
        if (mutex.isLocked()) {
            if (mutex.isExpired(now)) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug(
                            "Taking over expired lock on mutex "
                                    + mutex.getName()
                                    + " from "
                                    + mutex.getServerId()
                                    + " for "
                                    + serverName);
                }
            } else if (mutex.getServerId().equals(serverName)) {
                // Update the expiration date
                mutex.setLastUpdate(now);
                mutex.setExpirationDate(expires);
                this.entityManager.persist(mutex);

                return true;
            } else {
                return false;
            }
        }

        // Update the mutex
        mutex.setServerId(serverName);
        mutex.setLastUpdate(now);
        mutex.setExpirationDate(expires);
        mutex.setLocked(true);
        this.entityManager.persist(mutex);

        return true;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean updateLock(String mutexName, String serverName, long lockTimeout) {
        final Date now = new Date();
        final Date expires = new Date(now.getTime() + lockTimeout);

        final ClusterMutex mutex = this.getClusterMutex(mutexName);
        if (mutex == null) {
            return false;
        }

        // If the mutex is locked check if it is expired or owned by this server
        if (mutex.isLocked()) {
            if (mutex.getServerId().equals(serverName)) {
                // Update the expiration date
                mutex.setLastUpdate(now);
                mutex.setExpirationDate(expires);
                this.entityManager.persist(mutex);

                return true;
            }
        }

        return false;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void releaseLock(String mutexName) {
        // Implementation for interface method
        this.releaseLock(mutexName, "defaultServer");
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateLock(String mutexName) {
        // Implementation for interface method
        this.updateLock(mutexName, "defaultServer", 300000); // 5 minutes default
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ClusterMutex getLock(String mutexName) {
        // Implementation for interface method
        boolean acquired = this.getLock(mutexName, "defaultServer", 300000);
        return acquired ? this.getClusterMutex(mutexName) : null;
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean releaseLock(String mutexName, String serverName) {
        final ClusterMutex mutex = this.getClusterMutex(mutexName);
        if (mutex == null) {
            return false;
        }

        // If the mutex is locked check if it is expired or owned by this server
        if (mutex.isLocked()) {
            if (mutex.getServerId().equals(serverName)) {
                // Update the expiration date
                mutex.setLocked(false);
                this.entityManager.persist(mutex);

                return true;
            }
        }

        return false;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Map<String, ClusterMutex> getAllLocks() {
        final CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
        final CriteriaQuery<ClusterMutex> criteriaQuery =
                criteriaBuilder.createQuery(ClusterMutex.class);
        final Root<ClusterMutex> root = criteriaQuery.from(ClusterMutex.class);
        criteriaQuery.select(root);

        final TypedQuery<ClusterMutex> query = this.entityManager.createQuery(criteriaQuery);
        final List<ClusterMutex> results = query.getResultList();

        final Map<String, ClusterMutex> mutexes = new LinkedHashMap<String, ClusterMutex>();
        for (final ClusterMutex mutex : results) {
            mutexes.put(mutex.getName(), mutex);
        }

        return mutexes;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean isLockExpired(String mutexName) {
        final ClusterMutex mutex = this.getClusterMutex(mutexName);
        if (mutex == null) {
            return true;
        }

        return mutex.isExpired(new Date());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean isLocked(String mutexName) {
        final ClusterMutex mutex = this.getClusterMutex(mutexName);
        if (mutex == null) {
            return false;
        }

        return mutex.isLocked() && !mutex.isExpired(new Date());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean isLockOwner(String mutexName, String serverName) {
        final ClusterMutex mutex = this.getClusterMutex(mutexName);
        if (mutex == null) {
            return false;
        }

        return mutex.isLocked()
                && !mutex.isExpired(new Date())
                && mutex.getServerId().equals(serverName);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteClusterMutexes(String mutexNameRegex) {
        final Query query =
                this.entityManager.createQuery(
                        "DELETE FROM ClusterMutex mutex WHERE mutex.name REGEXP :mutexNameRegex");
        query.setParameter("mutexNameRegex", mutexNameRegex);
        query.executeUpdate();
    }

    protected TypedQuery<ClusterMutex> createMutexQuery(String mutexName) {
        final CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
        final CriteriaQuery<ClusterMutex> criteriaQuery =
                criteriaBuilder.createQuery(ClusterMutex.class);
        final Root<ClusterMutex> root = criteriaQuery.from(ClusterMutex.class);
        criteriaQuery.select(root);

        final ParameterExpression<String> nameParameter = criteriaBuilder.parameter(String.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("name"), nameParameter));

        final TypedQuery<ClusterMutex> query = this.entityManager.createQuery(criteriaQuery);
        query.setParameter(nameParameter, mutexName);

        return query;
    }
}