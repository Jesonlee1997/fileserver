package server1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;

import java.io.File;

/**
 * Created by JesonLee
 * on 2017/5/7.
 */
public class FileServer {
    final static String rootPath = "J:/Java/projects/fileserver/store";
    static {
        File file = new File(rootPath);
        file.mkdirs();
    }

    public void startServer(int port) {
        EventLoopGroup waiter = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(waiter, worker)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler())
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new FileServerHandler());
                    }
                });

        ChannelFuture future = bootstrap.bind(port);
        future.addListener((ChannelFutureListener) future1 -> System.out.println("服务器绑定到" + port + "端口"));
    }

    public static void main(String[] args) {
        new FileServer().startServer(1912);
    }
}


