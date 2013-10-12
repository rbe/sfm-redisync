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
/*
@javax.faces.bean.ManagedBean
*/
@javax.inject.Named
@javax.enterprise.context.RequestScoped
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
        String filename = facesHelper.getFilename(part);
        ManagedDirectory managedDirectory = new FacesHelper().getSelectedManagedDirectory();
        Path destination = Paths.get(managedDirectory.getPath().toString(), filename);
        try (InputStream stream = part.getInputStream()) {
            boolean madeDirectories = destination.toFile().getParentFile().mkdirs();
            // TODO Must have a filename, will delete directory!!
            // java.nio.file.DirectoryNotEmptyException: /Users/rbe/factit2
            // at sun.nio.fs.UnixFileSystemProvider.implDelete(UnixFileSystemProvider.java:241)
            if (destination.toFile().isFile()) {
                Files.copy(stream, destination, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadExisting() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        Map<String, String> params = externalContext.getRequestParameterMap();
        String filename = params.get("filename");
        ManagedDirectory managedDirectory = new FacesHelper().getSelectedManagedDirectory();
        Path destination = Paths.get(managedDirectory.getPath().toString(), filename);
        try (InputStream stream = part.getInputStream()) {
            if (destination.toFile().isFile()) {
                Files.copy(stream, destination, StandardCopyOption.REPLACE_EXISTING);
            }
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

}
