package me.jesonlee.fileclient;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by JesonLee
 * on 2017/5/28.
 */
public class JJFSClientTest {
    private JJFSClient client = new JJFSClient("192.168.56.200", 1912);

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
    public void uploadFile2() throws Exception {
        String localPath = "test.dat";
        File file = new File(localPath);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        byte[] bytes = new byte[1024];
        for (int i = 0; i < 1024 * 128; i++) {
            for (int j = 0; j < bytes.length; j++) {
                bytes[j] = (byte) (j % 128);
            }
            fileOutputStream.write(bytes);
        }

        String remotePath = "/test.dat";
        client.uploadFile(localPath, remotePath);
        Thread.sleep(3000);
    }

}