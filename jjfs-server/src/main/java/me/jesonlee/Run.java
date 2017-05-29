package me.jesonlee;

import me.jesonlee.fileserver.FileServer;
import me.jesonlee.httpserver.HttpStaticFileServer;

/**
 * Created by JesonLee
 * on 2017/5/28.
 */
public class Run {
    public static void main(String[] args) throws Exception {
        new Thread(() -> {
            FileServer.main(null);
        }).start();
        new Thread(() -> {
            try {
                HttpStaticFileServer.main(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }
}
