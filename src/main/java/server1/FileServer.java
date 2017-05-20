package server1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by JesonLee
 * on 2017/5/7.
 */
public class FileServer {
    public final static int SEGMENT_SIZE = 1024;
    public final static String rootPath = "J:/Java/projects/fileserver/store";

    static {
        File file = new File(rootPath);
        file.mkdirs();
    }
    public static void main(String[] args) {
        EventLoopGroup waiter = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup(1);
        ServerBootstrap bootstrap = new ServerBootstrap();
        final int port = 8080;
        bootstrap.group(waiter, worker)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler())
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new HelloHandler());
                    }
                });

        ChannelFuture future = bootstrap.bind(port);
        future.addListener((ChannelFutureListener) future1 -> System.out.println("服务器绑定到" + port + "端口"));
    }
}


class HelloHandler extends ChannelInboundHandlerAdapter {


    private boolean start = true;
    private int index = 0;
    byte[] bytes = new byte[FileServer.SEGMENT_SIZE];
    //MappedByteBuffer mappedByteBuffer;TODO：大文件使用
    private FileOutputStream outputStream;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        if (start) {
            byte b = buf.readByte();
            assert b == 100;
            System.out.println(buf.readerIndex());

            int fileLength = buf.readInt();

            System.out.println("文件长度为" + fileLength);

            int length = buf.readInt();
            byte[] bytes = new byte[length];
            buf.readBytes(bytes);
            String storePath = FileServer.rootPath + new String(bytes);
            mkDirectory(storePath);
            File file = new File(storePath);
            file.createNewFile();
            outputStream = new FileOutputStream(file);
            start = false;
        }

        buf.readBytes(outputStream, buf.readableBytes());
    }

    private void mkDirectory(String storePath) {
        int index = storePath.lastIndexOf("/");
        if (index > FileServer.rootPath.length()) {

            String directory = storePath.substring(0, index);
            File file = new File(directory);
            file.mkdirs();
        }
    }
}
