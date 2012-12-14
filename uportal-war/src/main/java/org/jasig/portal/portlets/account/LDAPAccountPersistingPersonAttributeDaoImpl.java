package org.jasig.portal.portlets.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component("ldapAccountPersistingPersonAttributeDaoImpl")
public class LDAPAccountPersistingPersonAttributeDaoImpl implements
		IPersistingPersonAttributeDAO {

	protected final Log log = LogFactory.getLog(getClass());

	private LdapTemplate ldapTemplate = null;;
	private ContextSource contextSource = null;
	private String ldapUrl;
	private String baseDn;
	private String searchBase = "";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jasig.portal.portlets.account.IPersistingPersonAttributeDAO#
	 * updateAttributes(java.lang.String, java.util.Map)
	 */
	@Override
	public void updateAttributes(String username,
			Map<String, List<Object>> attributes) {

		Name dn = buildDn(username);
		DirContextOperations context = ldapTemplate.lookupContext(dn);
		if (context != null) {
			Map<String, List<Object>> currentAttributes = getLdapUserAttributes(username);
			currentAttributes.putAll(attributes);
			updateAttributes(username, currentAttributes);
		} else {
			create(username, attributes);
		}
	}

	/**
	 * Creates a new user in the ldap datastore for the given username and user
	 * attributes
	 * 
	 * @param username
	 *            the user to create the entry for
	 * @param attributes
	 *            the user's attributes to set
	 */
	public void create(String username, Map<String, List<Object>> attributes) {
		DirContextAdapter context = new DirContextAdapter();
		mapToContext(username, attributes, context);
		ldapTemplate.bind(buildDn(username), context, null);
		if (log.isDebugEnabled()) {
			log.debug("New account has been created for " + username);
		}
	}

	/**
	 * For a given user id, get a map of user attributes from ldap
	 * 
	 * @param userId
	 *            the user id of the user to retrieve
	 * @return a map of attribute ids to attribute lists
	 */
	@SuppressWarnings("unchecked")
	public Map<String, List<Object>> getLdapUserAttributes(String userId) {
		AndFilter filter = new AndFilter();
		filter.and(new EqualsFilter("objectclass", "ncportalPerson"));
		filter.and(new EqualsFilter("uid", userId));

		return (Map<String, List<Object>>) ldapTemplate.search(searchBase,
				filter.encode(), new UserAttributesMapper());
	}

	/**
	 * Build the distinguished name for a given userId.
	 * 
	 * @param userId
	 * @return
	 */
	protected Name buildDn(String userId) {
		DistinguishedName dn = new DistinguishedName(baseDn);
		dn.add("uid", userId);
		return dn;
	}

	public void setLdapUrl(String ldapUrl) {
		this.ldapUrl = ldapUrl;
	}

	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
	}

	public void setSearchBase(String searchBase) {
		this.searchBase = searchBase;
	}

	public void setLdapTemplate(LdapTemplate ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
	}

	/**
	 * Given a username, map of user attributes, and ldap context, map the user
	 * attributes onto the context.
	 * 
	 * @param username
	 *            the username of the user to map to the context
	 * @param attributes
	 *            a map of user attribute ids to user attribute lists
	 * @param context
	 *            the ldap context upon which to map
	 */
	protected void mapToContext(String username,
			Map<String, List<Object>> attributes, DirContextOperations context) {
		context.setAttributeValues("objectclass", new String[] { "top",
				"ncportalPerson" });
		for (String attributeId : attributes.keySet()) {
			List<Object> attribute = attributes.get(attributeId);
			context.setAttributeValues(attributeId,
					attribute.toArray((new Object[attribute.size()])));
		}
	}
	
    /**
     * @param contextSource The ContextSource to get DirContext objects for queries from.
     */
    public void setContextSource(ContextSource contextSource) {
        Assert.notNull(contextSource, "contextSource can not be null");
        this.contextSource = contextSource;
        this.ldapTemplate = new LdapTemplate(this.contextSource);
    }


	/**
	 * This class handles mapping from a ldap context onto a map of user
	 * attributes
	 * 
	 * @author Bill Smith (wsmith@unicon.net)
	 * 
	 */
	private static class UserAttributesMapper extends AbstractContextMapper {

		protected final Log log = LogFactory.getLog(getClass());

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.springframework.ldap.core.support.AbstractContextMapper#
		 * doMapFromContext(org.springframework.ldap.core.DirContextOperations)
		 */
		@Override
		public Object doMapFromContext(DirContextOperations context) {
			Attributes attributes = context.getAttributes();
			NamingEnumeration<String> ids = attributes.getIDs();
			Map<String, List<Object>> attributesMap = new HashMap<String, List<Object>>();
			try {
				while (ids.hasMore()) {
					String id = ids.next();
					ArrayList<Object> attributeList = new ArrayList<Object>();
					Attribute attribute = attributes.get(id);
					NamingEnumeration<?> attributeEnumeration = attribute
							.getAll();
					while (attributeEnumeration.hasMore()) {
						Object attributeObject = attributeEnumeration.next();
						attributeList.add(attributeObject);
					}
					attributesMap.put(id, attributeList);
				}
			} catch (NamingException e) {
				log.error(e);
			}
			return attributesMap;
		}
	}
}
