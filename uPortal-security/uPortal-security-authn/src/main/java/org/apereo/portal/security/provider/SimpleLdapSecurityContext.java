/**
 * Licensed to Apereo under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright ownership. Apereo
 * licenses this file to you under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the License at the
 * following location:
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apereo.portal.security.provider;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import java.util.List;
import org.apereo.portal.ldap.ILdapServer;
import org.apereo.portal.ldap.LdapServices;
import org.apereo.portal.security.PortalSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is an implementation of a SecurityContext that checks a user's credentials against an LDAP
 * directory. It expects to be able to bind to the LDAP directory as the user so that it can
 * authenticate the user.
 *
 * <p>The default LDAP connection returned by {@link org.apereo.portal.ldap.LdapServices} is used.
 */
public class SimpleLdapSecurityContext extends ChainingSecurityContext {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // Attributes that we're interested in.
    public static final int ATTR_UID = 0;
    public static final int ATTR_FIRSTNAME = ATTR_UID + 1;
    public static final int ATTR_LASTNAME = ATTR_FIRSTNAME + 1;
    private final int SIMPLE_LDAP_SECURITYAUTHTYPE = 0xFF04;
    private static final String[] attributes = {
        "uid", // user ID
        "givenName", // first name
        "sn" // last name
    };

    public static final String LDAP_PROPERTIES_CONNECTION_NAME = "connection";

    /* package-private */ SimpleLdapSecurityContext() {}

    /**
     * Returns the type of authentication this class provides.
     *
     * @return authorization type
     */
    @Override
    public int getAuthType() {
        /*
         * What is this for?  No one would know what to do with the
         * value returned.  Subclasses might know but our getAuthType()
         * doesn't return anything easily useful.
         */
        return this.SIMPLE_LDAP_SECURITYAUTHTYPE;
    }

    /** Authenticates the user. */
    @Override
    public synchronized void authenticate() throws PortalSecurityException {
        this.isauth = false;
        ILdapServer ldapConn;

        ldapConn = LdapServices.getDefaultLdapServer();

        String creds = new String(this.myOpaqueCredentials.credentialstring);
        if (this.myPrincipal.UID != null
                && !this.myPrincipal.UID.trim().equals("")
                && this.myOpaqueCredentials.credentialstring != null
                && !creds.trim().equals("")) {
            
            String userFilter = "(" + ldapConn.getUidAttribute() + "=" + escapeLdapFilter(this.myPrincipal.UID) + ")";
            log.debug("SimpleLdapSecurityContext: Looking for {}", userFilter);

            try {
                // Use existing LDAP context source from configuration
                org.springframework.ldap.core.ContextSource contextSource = 
                    ((org.apereo.portal.ldap.ContextSourceLdapServerImpl) ldapConn).getContextSource();
                
                LdapTemplate ldapTemplate = new LdapTemplate(contextSource);
                
                // Search for user and extract attributes
                List<UserInfo> users = ldapTemplate.search(
                    "", userFilter,
                    (AttributesMapper<UserInfo>) attrs -> {
                        UserInfo user = new UserInfo();
                        user.firstName = getAttributeValue(attrs, ATTR_FIRSTNAME);
                        user.lastName = getAttributeValue(attrs, ATTR_LASTNAME);
                        user.dn = attrs.get("distinguishedName") != null ? 
                            attrs.get("distinguishedName").get().toString() : null;
                        return user;
                    }
                );
                
                if (users.isEmpty()) {
                    log.error("SimpleLdapSecurityContext: user not found: {}", this.myPrincipal.UID);
                } else {
                    UserInfo userInfo = users.get(0);
                    
                    // Authenticate by binding as the user using Spring LDAP
                    try {
                        String userDn = userInfo.dn != null ? userInfo.dn : 
                            ldapConn.getUidAttribute() + "=" + this.myPrincipal.UID + "," + ldapConn.getBaseDN();
                        
                        // Test authentication by getting context with user credentials
                        contextSource.getContext(userDn, new String(this.myOpaqueCredentials.credentialstring));
                        
                        this.isauth = true;
                        this.myPrincipal.FullName = userInfo.firstName + " " + userInfo.lastName;
                        log.debug("SimpleLdapSecurityContext: User {} ({}) is authenticated",
                                this.myPrincipal.UID, this.myPrincipal.FullName);
                        
                        // Since LDAP is case-insensitive with respect to uid, force
                        // user name to lower case for use by the portal
                        this.myPrincipal.UID = this.myPrincipal.UID.toLowerCase();
                        
                    } catch (Exception ae) {
                        log.info("SimpleLdapSecurityContext: Password invalid for user: " + this.myPrincipal.UID);
                    }
                }
                
            } catch (Exception e) {
                log.error("SimpleLdapSecurityContext: LDAP Error with user: " + this.myPrincipal.UID + "; ", e);
                throw new PortalSecurityException("SimpleLdapSecurityContext: LDAP Error" + e + " with user: " + this.myPrincipal.UID);
            }
        } else {
            // If the principal and/or credential are missing, the context authentication
            // simply fails. It should not be construed that this is an error. It happens for guest
            // access.
            log.info("Principal or OpaqueCredentials not initialized prior to authenticate");
        }
        // Ok...we are now ready to authenticate all of our subcontexts.
        super.authenticate();
        return;
    }

    /*--------------------- Helper methods ---------------------*/

    /**
     * Return a single value of an attribute from possibly multiple values, grossly ignoring
     * anything else. If there are no values, then return an empty string.
     *
     * @param attrs LDAP query results
     * @param attribute LDAP attribute we are interested in
     * @return a single value of the attribute
     */
    private String getAttributeValue(Attributes attrs, int attribute) {
        String aValue = "";
        if (!isAttribute(attribute)) return aValue;
        try {
            javax.naming.directory.Attribute attrib = attrs.get(attributes[attribute]);
            if (attrib != null && attrib.get() != null) {
                aValue = attrib.get().toString();
            }
        } catch (Exception e) {
            log.debug("Error getting attribute value for {}: {}", attributes[attribute], e.getMessage());
        }
        return aValue;
    }
    
    /**
     * Helper class to hold user information from LDAP search
     */
    private static class UserInfo {
        String firstName = "";
        String lastName = "";
        String dn;
    }

    /**
     * Is this a value attribute that's been requested?
     *
     * @param attribute in question
     */
    private boolean isAttribute(int attribute) {
        if (attribute < ATTR_UID || attribute > ATTR_LASTNAME) {
            return false;
        }
        return true;
    }

    /**
     * Escape special LDAP characters to prevent injection attacks
     */
    private String escapeLdapFilter(String input) {
        if (input == null) return null;
        return input.replace("\\", "\\\\")
                    .replace("*", "\\*")
                    .replace("(", "\\(")
                    .replace(")", "\\)")
                    .replace("\0", "\\00");
    }
}
