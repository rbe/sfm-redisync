/*
 * redisync
 *
 * Copyright (C) 2013 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package eu.artofcoding.redisync.myredis;

import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Redisync {

    private final Jedis jedis;

    public Redisync(String hostname) {
        jedis = new Jedis(hostname);
    }

    public Redisync() {
        this("localhost");
    }

    public void set(String key, String value) {
        jedis.set(key, value);
    }

    public String get(String key) {
        return jedis.get(key);
    }

    public List<String> waitFor(int timeout, String key) {
        return jedis.blpop(timeout, key);
    }

    public void setFile(String key, Path path) throws IOException {
        final String base64 = Base64Helper.fileToBase64(path);
        jedis.set(key, base64);
    }

    public Path getFile(String key, Path path) throws IOException {
        final String base64 = get(key);
        final byte[] b = Base64Helper.base64ToByteArray(base64);
        final Path path2 = Files.write(path, b);
        return path2;
    }

    public void publishFile(String key, Path path) throws IOException {
        String base64 = Base64Helper.fileToBase64(path);
        jedis.rpush(key, base64);
    }

    public Path waitForFile(String key, Path path) throws IOException {
        List<String> w = waitFor(1, key);
        final String base64 = w.get(1);
        final byte[] b = Base64Helper.base64ToByteArray(base64);
        final Path path2 = Files.write(path, b);
        return path2;
    }

}
