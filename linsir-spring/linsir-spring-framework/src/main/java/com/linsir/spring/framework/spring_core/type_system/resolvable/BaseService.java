package com.linsir.spring.framework.spring_core.type_system.resolvable;

import java.util.List;

/**
 * Base service for demonstrating generic type resolution
 */
public abstract class BaseService<T, ID> {

    public abstract T findById(ID id);

    public abstract List<T> findAll();

    public abstract void save(T entity);

    public abstract void deleteById(ID id);

    /**
     * Get generic type T at runtime
     */
    public Class<T> getEntityClass() {
        return null;
    }

    /**
     * Get ID type at runtime
     */
    public Class<ID> getIdClass() {
        return null;
    }
}
