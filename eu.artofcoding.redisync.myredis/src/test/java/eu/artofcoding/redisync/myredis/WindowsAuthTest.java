/*
 * redisync
 *
 * Copyright (C) 2013 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package eu.artofcoding.redisync.myredis;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import javax.naming.directory.DirContext;

@Ignore
public class WindowsAuthTest {

    private static final String LDAP_URL = "ldap://10.31.21.1:389";

    private static final String LDAP_BASE_DN = "dc=RZ-FACT,dc=local";

    private static final String LDAP_GROUP_DN = "CN=%s,OU=Zugriff,OU=Benutzergruppen,OU=.Root,DC=RZ-FACT,DC=local";

    @Test
    public void testFindUserWithRole() throws Exception {
        WindowsAuth windowsAuth = new WindowsAuth(LDAP_URL);
        String user = "zzz_test1@rz-fact.local";
        String passwd = "kennwort";
        DirContext ctx = windowsAuth.login(user, passwd);
        boolean b;
        b = windowsAuth.findUserWithRole(ctx, LDAP_BASE_DN, "zzz_test1", LDAP_GROUP_DN, "Z_Curator_Vorlagen_RW");
        Assert.assertTrue(b);
        b = windowsAuth.findUserWithRole(ctx, "dc=RZ-FACT,dc=local", "zzz_test1", LDAP_GROUP_DN, "Z_Curator_Vorlagen");
        Assert.assertFalse(b);
        ctx.close();
    }

}
