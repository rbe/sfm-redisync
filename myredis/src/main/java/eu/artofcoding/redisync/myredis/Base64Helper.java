/*
 * redisync
 *
 * Copyright (C) 2013 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package eu.artofcoding.redisync.myredis;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Base64Helper {

    private Base64Helper() {
    }

    public static String fileToBase64(Path path) throws IOException {
        final byte[] data = Files.readAllBytes(path);
        return DatatypeConverter.printBase64Binary(data);
    }

    public static byte[] base64ToByteArray(String base64) {
        return DatatypeConverter.parseBase64Binary(base64);
    }

}
