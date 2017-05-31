package me.jesonlee.jjfsserver;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Log4JLoggerFactory;
import me.jesonlee.jjfsserver.fileserver.FileServer;
import me.jesonlee.jjfsserver.httpserver.HttpStaticFileServer;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;

/**
 * Created by JesonLee
 * on 2017/5/28.
 */
public class Run {
    private static String config = "/classes/jjfs.xml";//相对于baseDir
    private static String logsPath = "/logs";
    private static String baseDir;


    public static void main(String[] args) {

        initBaseDir();
        System.setProperty("logDir", baseDir + logsPath);

        InternalLoggerFactory.setDefaultFactory(new Log4JLoggerFactory());
        initConfig();
        new Thread(() -> FileServer.main(null)).start();
        new Thread(() -> {
            try {
                HttpStaticFileServer.main(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    private static void initBaseDir() {
        String classPath = Run.class.getResource("/").getPath();
        File file = new File(classPath);
        baseDir = file.getParent();
    }

    private static void initConfig() {

        String configPath = baseDir + config;
        System.out.println(configPath);
        File config = new File(configPath);

        Document document = null;
        SAXReader reader = new SAXReader();
        try {
            document = reader.read(config);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element root = document.getRootElement();
        Element contentRoot = root.element("contentRoot");
        Element filePort = root.element("filePort");
        Element httpPort = root.element("httpPort");

        System.setProperty("contextRoot", contentRoot.getText());
        System.setProperty("filePort", filePort.getText());
        System.setProperty("httpPort", httpPort.getText());
    }
}
