package me.jesonlee.fileclient;

import org.junit.Test;

/**
 * Created by JesonLee
 * on 2017/5/28.
 */
public class FileClientTest {
    private FileClient client = new FileClient("127.0.0.1", 1912);

    @Test
    public void deleteFile() throws Exception {
        client.deleteFile("/*");
    }

    @Test
    public void uploadFile() throws Exception {
        String localPath = "J:\\Github\\bobo";
        String remotePath = "/bobo";

        client.uploadFile(localPath, remotePath);
    }

    @Test
    public void uploadDir() throws Exception {
        String localPath = "J:\\Github\\bobo";
        String remotePath = "/bobo";

        //client.uploadDir(localPath, remotePath);
    }

}