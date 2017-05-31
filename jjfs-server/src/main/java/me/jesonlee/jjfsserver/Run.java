package me.jesonlee.jjfsserver;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Log4JLoggerFactory;
import me.jesonlee.jjfsserver.fileserver.FileServer;
import me.jesonlee.jjfsserver.httpserver.HttpStaticFileServer;

import java.io.File;

/**
 * Created by JesonLee
 * on 2017/5/28.
 */
public class Run {


    public static void main(String[] args) {
        System.setProperty("logDir", "J:\\Java\\projects\\fileserver\\jjfs-server\\src\\main\\logs");
        InternalLoggerFactory.setDefaultFactory(new Log4JLoggerFactory());
        initConfig();

        new Thread(() -> FileServer.main(new String[]{"1912"})).start();
        new Thread(() -> {
            try {
                HttpStaticFileServer.main(new String[]{"8080"});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    static void initConfig() {
        String classPath = Run.class.getResource("/").getPath();
        File file = new File(classPath);
        String configPath = file.getParent() + "/conf/jjfs.xml";
        System.out.println(configPath);

        String contextRoot;
        String filePort;
        String httpPort;
    }
}
