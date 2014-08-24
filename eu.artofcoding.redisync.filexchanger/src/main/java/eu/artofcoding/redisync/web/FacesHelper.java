/*
 * redisync
 *
 * Copyright (C) 2013 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package eu.artofcoding.redisync.web;

import javax.el.ELContext;
import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Named
public class FacesHelper implements Serializable {

    public String getInitParameter(String name) {
        final FacesContext facesContext = FacesContext.getCurrentInstance();
        final ExternalContext externalContext = facesContext.getExternalContext();
        return externalContext.getInitParameter(name);
    }

    /**
     * Get managed bean by name.
     * @param beanName Name of bean.
     * @return Managed bean instance, can be null.
     */
    public <T> T getManagedBean(final String beanName) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        T bean;
        try {
            ELContext elContext = facesContext.getELContext();
            bean = (T) elContext.getELResolver().getValue(elContext, null, beanName);
        } catch (RuntimeException e) {
            throw new FacesException(e.getMessage(), e);
        }
        return bean;
    }

    public ManagedDirectory[] getManagedDirectories() {
        String parameterDirectories = getInitParameter(String.format("%s.BASE_PATHS", FacesHelper.class.getPackage().getName()));
        String[] split = parameterDirectories.split("[,]");
        List<ManagedDirectory> directories = new ArrayList<>();
        for (String s : split) {
            String[] split2 = s.split("=");
            String name = split2[0];
            Path path = Paths.get(split2[1]);
            directories.add(new ManagedDirectory(name, path));
        }
        ManagedDirectory[] managedDirectories = directories.toArray(new ManagedDirectory[directories.size()]);
        return managedDirectories;
    }

    public ManagedDirectory getManagedDirectoryByName(String name) {
        ManagedDirectory managedDirectory = null;
        ManagedDirectory[] managedDirectories = getManagedDirectories();
        for (ManagedDirectory m : managedDirectories) {
            if (/*null != m &&*/ m.getName().equals(name)) {
                managedDirectory = m;
                break;
            }
        }
        return managedDirectory;
    }

    public ManagedDirectory getSelectedManagedDirectory() {
        LoginBean loginBean = getManagedBean("loginBean");
        ManagedDirectory managedDirectory = loginBean.getSelectedDirectory();
        return managedDirectory;
    }

    public String getFilename(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                String filename = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
                return filename.substring(filename.lastIndexOf('/') + 1).substring(filename.lastIndexOf('\\') + 1); // MSIE fix.  
            }
        }
        return null;
    }

    public void download(String contentType, String filename, byte[] data) {
        final FacesContext facesContext = FacesContext.getCurrentInstance();
        final ExternalContext externalContext = facesContext.getExternalContext();
        // Check content type
        if (null == contentType) {
            contentType = "application/x-octet-stream";
        }
        externalContext.setResponseHeader("Content-Type", contentType);
        externalContext.setResponseHeader("Content-Length", String.valueOf(data.length));
        if (null == filename) {
            String[] split = contentType.split("/");
            String s;
            if (split.length >= 2) {
                s = String.format("file.%s", split[1]);
            } else {
                s = "file";
            }
            filename = String.format("This_is_a_%s", s);
        }
        externalContext.setResponseHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", filename));
        try {
            externalContext.getResponseOutputStream().write(data);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        facesContext.responseComplete();
    }

}
