/*
 * redisync
 *
 * Copyright (C) 2013 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package eu.artofcoding.redisync.web;

import javax.annotation.PostConstruct;
import javax.annotation.security.DeclareRoles;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

@DeclareRoles({"uploader"})
@Named
public class DownloadBean implements Serializable {

    @Inject
    private FacesHelper facesHelper;

    private String[] exts;

    @PostConstruct
    private void postConstruct() {
        // EXTENSIONS
        String extParameter = String.format("%s.EXTENSIONS", this.getClass().getName());
        String extensions = facesHelper.getInitParameter(extParameter);
        exts = extensions.split(",");
    }

    public class F implements FileVisitor<Path> {

        private String[] exts;

        private List<Document> list;

        public F(List<Document> list, String[] exts) {
            this.list = list;
            this.exts = exts;
        }

        private boolean fileTypeAcceptable(Path file) {
            final String filename = file.getFileName().toString();
            for (String s : exts) {
                if (filename.startsWith("~")) {
                    return false;
                }
                if (filename.endsWith(s)) {
                    return true;
                }
            }
            return false;
        }

        private boolean fileAttributesAcceptable(BasicFileAttributes attrs) {
            return attrs.isRegularFile() && attrs.size() > 0;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (fileTypeAcceptable(file) && fileAttributesAcceptable(attrs)) {
                list.add(new Document(file));
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            return FileVisitResult.TERMINATE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }

    }

    public List<Document> getFiles() throws IOException {
        List<Document> list = new ArrayList<>();
        ManagedDirectory managedDirectory = new FacesHelper().getSelectedManagedDirectory();
        if (null != managedDirectory) {
            Files.walkFileTree(managedDirectory.getPath(), new TreeSet<FileVisitOption>(), 1, new F(list, exts));
        }
        return list;
    }

    public void download(Document document) throws IOException {
        Path path = document.getPath();
        String filename = path.getFileName().toString();
        byte[] data = Files.readAllBytes(path);
        facesHelper.download(Document.CONTENT_TYPE_DOCX, filename, data);
    }

}
