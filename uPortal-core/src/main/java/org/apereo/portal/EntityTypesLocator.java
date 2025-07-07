package org.apereo.portal;

public final class EntityTypesLocator {
    private static final EntityTypes ENTITY_TYPES = new EntityTypes();
    
    private EntityTypesLocator() {}
    
    public static EntityTypes getEntityTypes() {
        return ENTITY_TYPES;
    }
}