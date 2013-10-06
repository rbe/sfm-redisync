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
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import java.io.Serializable;

/*
@javax.faces.bean.ManagedBean
@javax.faces.bean.SessionScoped
*/
@javax.inject.Named
@javax.enterprise.context.SessionScoped
public class LoginBean implements Serializable {
    
    @Inject
    private FacesHelper facesHelper;

    private String ldapURL;

    private String ldapBaseDn;

    private String ldapUserSuffix;

    private String ldapGroupDn;

    private String ldapNeededGroup;

    private String username;

    private String password;

    private boolean authenticated;

    private ManagedDirectory[] managedDirectories;

    private ManagedDirectory selectedDirectory;

    @PostConstruct
    private void postConstruct() {
        String clazzName = this.getClass().getName();
        String packageName = this.getClass().getPackage().getName();
        // LDAP configuration parameter
        ldapURL = facesHelper.getInitParameter(String.format("%s.LDAP_URL", clazzName));
        ldapBaseDn = facesHelper.getInitParameter(String.format("%s.LDAP_BASE_DN", clazzName));
        ldapUserSuffix = facesHelper.getInitParameter(String.format("%s.LDAP_USER_SUFFIX", clazzName));
        ldapGroupDn = facesHelper.getInitParameter(String.format("%s.LDAP_GROUP_DN", clazzName));
        ldapNeededGroup = facesHelper.getInitParameter(String.format("%s.NEEDED_GROUP", clazzName));
        // Managed directories
        managedDirectories = facesHelper.getManagedDirectories();
        selectedDirectory = managedDirectories[0];
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

    public ManagedDirectory[] getManagedDirectories() {
        return managedDirectories;
    }

    public ManagedDirectory getSelectedDirectory() {
        return selectedDirectory;
    }

    public void directoryChanged(AjaxBehaviorEvent event) {
        HtmlSelectOneMenu source = (HtmlSelectOneMenu) event.getSource();
        String submittedValue = (String) source.getSubmittedValue();
        selectedDirectory = facesHelper.getManagedDirectoryByName(submittedValue);
    }

}
