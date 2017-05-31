package me.jesonlee.jjfstest;

import me.jesonlee.fileclient.JJFSClient;

/**
 * Created by JesonLee
 * on 2017/5/31.
 */
public class TestClient {
    public static void main(String[] args) {
        JJFSClient client = new JJFSClient("192.168.56.200", 1912);
        client.uploadFile("J:/Github/BoBo", "/bobo");
    }
}
