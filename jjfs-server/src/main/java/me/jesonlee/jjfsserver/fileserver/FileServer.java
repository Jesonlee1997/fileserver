package me.jesonlee.jjfsserver.fileserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * Created by JesonLee
 * on 2017/5/7.
 */
public class FileServer {
    private static Logger logger = Logger.getLogger(FileServer.class);

    static String rootPath = "J:/Java/projects/fileserver/store";

    public static void setRootPath(String rootPath) {
        FileServer.rootPath = rootPath;
    }

    static {
        File file = new File(rootPath);
        file.mkdirs();
    }

    public void startServer(int port) {
        EventLoopGroup waiter = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup(6);
        try {

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(waiter, worker)
                    .option(ChannelOption.SO_BACKLOG, 4096)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new FileServerHandler());
                        }
                    });

            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.println("服务器绑定到" + port+ "端口");
            logger.info("服务器绑定到" + port+ "端口");
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            waiter.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        if (port < 1000 || port > 65535) {
            logger.error("port can not less than 1000 or beyond 65535");
            System.exit(1);
        }
        new FileServer().startServer(port);
    }
}


