/*
 * redisync
 *
 * Copyright (C) 2013 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package eu.artofcoding.redisync.myredis;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.Hashtable;

/**
 * Following are the different error codes and their meanings:
 * 52e: invalid credentials; user name or password is incorrect.
 * 525: User not found.
 * 530: User is not permitted to logon at this time.
 * 532: Password has expired.
 * 533: User account is disabled.
 * 701: User account has expired.
 * 773: User must reset password.
 */
public class WindowsAuth implements AutoCloseable {

    private static final String LDAP_CTX_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";

    private static final String USERFILTER = "(sAMAccountName=%s)";

    private DirContext ctx;

    private final String ldapURL;

    public WindowsAuth(String ldapURL) {
        this.ldapURL = ldapURL;
    }

    public DirContext login(String user, String passwd) throws NamingException {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, LDAP_CTX_FACTORY);
        env.put(Context.PROVIDER_URL, ldapURL);
        env.put(Context.SECURITY_AUTHENTICATION, "simple"); // "DIGEST-MD5"
        env.put(Context.SECURITY_PRINCIPAL, user);
        env.put(Context.SECURITY_CREDENTIALS, passwd);
        ctx = new InitialDirContext(env);
        return ctx;
    }

    public SearchResult findUser(DirContext ctx, String base, String username) throws NamingException {
        SearchControls searchCtls = new SearchControls();
        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String[] returnAttributes = {"givenName", "cn", "mail", "memberOf"};
        searchCtls.setReturningAttributes(returnAttributes);
        String filter = String.format(USERFILTER, username);
        NamingEnumeration<SearchResult> result = ctx.search(base, filter, searchCtls);
        // Return first result
        if (result.hasMoreElements()) {
            SearchResult r = result.next();
            return r;
        } else {
            return null;
        }
    }

    public boolean findUserWithRole(DirContext ctx, String base, String username, String groupDn, String role) throws NamingException {
        SearchResult result = findUser(ctx, base, username);
        Attribute attr = result.getAttributes().get("memberOf");
        NamingEnumeration<?> e = attr.getAll();
        while (e.hasMoreElements()) {
            String n = (String) e.next();
            if (n.matches(String.format(groupDn, role))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void close() throws NamingException {
        ctx.close();
    }

}
