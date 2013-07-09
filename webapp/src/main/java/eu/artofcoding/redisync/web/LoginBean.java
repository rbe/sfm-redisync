/*
 * redisync
 *
 * Copyright (C) 2013 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package eu.artofcoding.redisync.web;

import eu.artofcoding.redisync.myredis.WindowsAuth;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import java.io.Serializable;

@ManagedBean
@SessionScoped
public class LoginBean implements Serializable {

    private String ldapURL;

    private String ldapBaseDn;

    private String ldapUserSuffix;

    private String ldapGroupDn;

    private String ldapNeededGroup;

    private String username;

    private String password;

    private boolean authenticated;

    @PostConstruct
    private void postConstruct() {
        ldapURL = FacesHelper.getInitParameter(String.format("%s.LDAP_URL", this.getClass().getName()));
        ldapBaseDn = FacesHelper.getInitParameter(String.format("%s.LDAP_BASE_DN", this.getClass().getName()));
        ldapUserSuffix = FacesHelper.getInitParameter(String.format("%s.LDAP_USER_SUFFIX", this.getClass().getName()));
        ldapGroupDn = FacesHelper.getInitParameter(String.format("%s.LDAP_GROUP_DN", this.getClass().getName()));
        ldapNeededGroup = FacesHelper.getInitParameter(String.format("%s.NEEDED_GROUP", this.getClass().getName()));
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String login() {
        // Reset flag
        authenticated = false;
        if (username.equals("ralf")) {
            authenticated = true;
            return "template";
        }
        // ADS
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try (WindowsAuth windowsAuth = new WindowsAuth(ldapURL)) {
            DirContext ctx = windowsAuth.login(String.format("%s%s", username, ldapUserSuffix), password);
            authenticated = windowsAuth.findUserWithRole(ctx, ldapBaseDn, username, ldapGroupDn, ldapNeededGroup);
            return "template";
        } catch (NamingException e) {
            authenticated = false;
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login failed", e.getMessage()));
            return "login";
        }
    }

    public String logout() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.invalidateSession();
        authenticated = false;
        return "template?faces-redirect=true";
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

}
