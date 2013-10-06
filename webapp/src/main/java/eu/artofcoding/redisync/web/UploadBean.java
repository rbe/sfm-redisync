/*
 * redisync
 *
 * Copyright (C) 2013 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package eu.artofcoding.redisync.web;

import javax.annotation.security.DeclareRoles;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@DeclareRoles({"uploader"})
@ManagedBean
public class UploadBean {

    private static final int MAX_FILE_SIZE_IN_BYTES = 10 * 1024 * 1024;

    private Part part;
    @Inject
    private FacesHelper facesHelper;

    private String uploadNewName;

    public void validateFile(FacesContext facesContext, UIComponent uiComponent, Object value) {
        List<FacesMessage> msgs = new ArrayList<>();
        Part file = (Part) value;
        final long sizeInBytes = file.getSize();
        if (sizeInBytes > MAX_FILE_SIZE_IN_BYTES) {
            msgs.add(new FacesMessage("part too big"));
        }
        /*
        System.out.printf("Content-Type: %s%n", part.getContentType());
        if (!"text/plain".equals(part.getContentType())) {
            msgs.add(new FacesMessage("not a text part"));
        }
        */
        if (!msgs.isEmpty()) {
            throw new ValidatorException(msgs);
        }
    }

    public void uploadNew() {
        String basePathParameter = String.format("%s.BASE_PATH", this.getClass().getName());
        String basePath = FacesHelper.getInitParameter(basePathParameter);
        String filename = FacesHelper.getFilename(part);
        Path destination = Paths.get(basePath, filename);
        try (InputStream stream = part.getInputStream()) {
            Files.copy(stream, destination, StandardCopyOption.REPLACE_EXISTING);
            uploadNewName = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadExisting() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        Map<String, String> params = externalContext.getRequestParameterMap();
        String filename = params.get("filename");
        String basePathParameter = String.format("%s.BASE_PATH", this.getClass().getName());
        String basePath = FacesHelper.getInitParameter(basePathParameter);
        Path destination = Paths.get(basePath, filename);
        try (InputStream stream = part.getInputStream()) {
            Files.copy(stream, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }

    public void setUploadNewName(String uploadNewName) {
        this.uploadNewName = uploadNewName;
    }

    public String getUploadNewName() {
        return uploadNewName;
    }

}
