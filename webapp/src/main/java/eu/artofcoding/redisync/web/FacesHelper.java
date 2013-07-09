/*
 * redisync
 *
 * Copyright (C) 2013 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package eu.artofcoding.redisync.web;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;
import java.io.IOException;

public class FacesHelper {

    public static String getInitParameter(String name) {
        final FacesContext facesContext = FacesContext.getCurrentInstance();
        final ExternalContext externalContext = facesContext.getExternalContext();
        return externalContext.getInitParameter(name);
    }

    public static String getFilename(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                String filename = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
                return filename.substring(filename.lastIndexOf('/') + 1).substring(filename.lastIndexOf('\\') + 1); // MSIE fix.  
            }
        }
        return null;
    }

    public static void download(String contentType, String filename, byte[] data) {
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
