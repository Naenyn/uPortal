package org.apereo.portal.services;

import org.apereo.portal.groups.IEntityGroup;
import org.apereo.portal.groups.IGroupMember;
import org.apereo.portal.security.IPerson;

public class GroupService {
    public enum SearchMethod {
        DISCRETE
    }
    
    public static IEntityGroup findGroup(String role) {
        return null;
    }
    
    public static IEntityGroup[] searchForGroups(String role, SearchMethod method, Class<IPerson> clazz) {
        return new IEntityGroup[0];
    }
    
    public static IGroupMember getGroupMember(IEntityGroup group) {
        return null;
    }
    
    public static IGroupMember getGroupMember(String entityId) {
        return null;
    }
}