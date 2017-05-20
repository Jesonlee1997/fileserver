package server1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by JesonLee
 * on 2017/5/7.
 */
public class FileClient {

    private String host;
    private int port;
    private Bootstrap bootstrap;

    public FileClient(String host, int port) {
        this.host = host;
        this.port = port;
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new FileClientHandler());
                        channelList.add(ch);
                    }
                });
    }


    private int channelNum = 5;
    private List<Channel> channelList = new ArrayList<>(channelNum);

    private AtomicInteger round = new AtomicInteger(0);


    private Channel initChannel() {
        if (channelList.size() < channelNum) {
            try {
                ChannelFuture future = bootstrap.connect(host, port).sync();
                return future.channel();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return channelList.get(round.getAndIncrement() % channelNum);
    }

    /**
     * 删除远程文件服务器的文件
     *
     * @param remotePath 文件在文件服务器的相对路径
     */
    public void deleteFile(String remotePath) {
        Channel channel = initChannel();
        FileClientHandler handler = channel.pipeline().get(FileClientHandler.class);
        handler.deleteFile(remotePath);
    }

    public void uploadFile(String localPath, String remotePath) {
        Channel channel = initChannel();
        FileClientHandler handler = channel.pipeline().get(FileClientHandler.class);
        try {
            handler.uploadFile(new File(localPath), remotePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadFile(File file, String remotePath) {
        Channel channel = initChannel();
        FileClientHandler handler = channel.pipeline().get(FileClientHandler.class);
        try {
            handler.uploadFile(file, remotePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadDir(String localPath, String remotePath) throws IOException {
        uploadDir(new File(localPath), remotePath);
    }

    /**
     * @param dir        目录
     * @param remotePath 目录的远程路径
     */
    public void uploadDir(File dir, String remotePath) throws IOException {

        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                uploadDir(file, remotePath + "/" + file.getName());
            } else {
                uploadFile(file, remotePath + "/" + file.getName());
            }
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        FileClient client = new FileClient("127.0.0.1", 1912);
        String localPath = "J:\\Java\\test";
        String remotePath = "/test";

        //client.uploadFile(localPath, remotePath);
        //client.deleteFile(remotePath);
        client.uploadDir(localPath, remotePath);
    }
}

