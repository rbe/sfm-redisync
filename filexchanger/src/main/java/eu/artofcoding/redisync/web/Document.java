/*
 * redisync
 *
 * Copyright (C) 2013 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package eu.artofcoding.redisync.web;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Document {

    @Inject
    private FacesHelper facesHelper;

    private SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    private Path path;

    public Document() {
    }

    public Document(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    public File getFile() {
        return path.toFile();
    }

    public Date getLastModified() {
        return new Date(path.toFile().lastModified());
    }

    public String getLastModifiedAsGerman() {
        return df.format(getLastModified());
    }

    public String getContentType() throws IOException {
        return Files.probeContentType(path);
    }

    public void download() throws IOException {
        facesHelper.download(
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                path.getFileName().toString(),
                Files.readAllBytes(path)
        );
    }

}
