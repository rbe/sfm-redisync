package eu.artofcoding.redisync.myredis;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

public class RedisyncTest {

    private Redisync redisync;

    @Before
    public void setUp() {
        redisync = new Redisync();
    }

    @Test
    public void testPublishFile() throws Exception {
        final Path path = Paths.get("/Users", "rbe", "leksah.lkshs");
        final Path path2 = Paths.get("/Users", "rbe", "leksah2.lkshs");
        new Thread() {
            public void run() {
                try {
                    //redisync.setFile("data", path);
                    redisync.publishFile("data2", path);
                    System.out.printf("%s: published %s%n", new Date(), path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        Thread.sleep(2 * 1000);
        new Thread() {
            public void run() {
                try {
                    //redisync.getFile("data", path2);
                    redisync.waitForFile("data2", path2);
                    System.out.printf("%s: saved %s%n", new Date(), path2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Test
    public void testWaitForFile() throws Exception {
    }

}
