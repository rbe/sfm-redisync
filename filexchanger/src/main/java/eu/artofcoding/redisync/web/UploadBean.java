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
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;
import java.io.File;
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
@Named
@RequestScoped
public class UploadBean {

    private static final int MAX_FILE_SIZE_IN_BYTES = 10 * 1024 * 1024;

    @Inject
    private FacesHelper facesHelper;
    
    private Part part;

    public void validateFile(FacesContext facesContext, UIComponent uiComponent, Object value) {
        List<FacesMessage> msgs = new ArrayList<>();
        Part file = (Part) value;
        long sizeInBytes = file.getSize();
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
        // Check part
        if (part.getSize() == 0) {
            return;
        }
        String filename = facesHelper.getFilename(part);
        ManagedDirectory managedDirectory = new FacesHelper().getSelectedManagedDirectory();
        Path destination = Paths.get(managedDirectory.getPath().toString(), filename);
        try (InputStream stream = part.getInputStream()) {
            File parentFile = destination.toFile().getParentFile();
            if (!parentFile.isDirectory()) {
                /*boolean madeDirectories = */
                parentFile.mkdirs();
            }
            if (parentFile.isDirectory()) {
                boolean isNewFile = !destination.toFile().isDirectory() && !destination.toFile().isFile();
                if (isNewFile) {
                    Files.copy(stream, destination, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadExisting() {
        // Check part
        if (part.getSize() == 0) {
            return;
        }
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        Map<String, String> params = externalContext.getRequestParameterMap();
        String filename = params.get("filename");
        ManagedDirectory managedDirectory = new FacesHelper().getSelectedManagedDirectory();
        Path destination = Paths.get(managedDirectory.getPath().toString(), filename);
        File parentFile = destination.toFile().getParentFile();
        if (parentFile.isDirectory()) {
            try (InputStream stream = part.getInputStream()) {
                boolean canProceed = destination.toFile().isFile() && destination.toFile().canWrite();
                if (canProceed) {
                    Files.copy(stream, destination, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }

}
