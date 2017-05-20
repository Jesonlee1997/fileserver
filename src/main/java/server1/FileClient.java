package server1;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by JesonLee
 * on 2017/5/7.
 */
public class FileClient {
    public static void main(String[] args) throws InterruptedException, IOException {

        File file = new File("J:\\Java\\projects\\fileserver\\src\\main\\java\\server1\\test.txt");
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        FileClientHandler clientHandler = new FileClientHandler();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(clientHandler);
                    }
                });
        String host = "127.0.0.1";
        int port = 8080;
        bootstrap.connect(host, port).sync();
        clientHandler.sendFile(file, "/test/java");
    }
}

/**
 * 传输客户端
 */
class FileClientHandler extends ChannelInboundHandlerAdapter {
    private Channel channel;
    private static final int SEGMENT_LENGTH = 1024;
    private byte[] bytes = new byte[SEGMENT_LENGTH];
    private static final byte REQUEST_START = 100;


    public void sendFile(File file, String storePath) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getPath());

        }
        if (file.length() > Integer.MAX_VALUE) {
            throw new IOException(file.getName() + " file is too large");
        }

        FileInputStream fileInputStream = new FileInputStream(file);

        int fileLength = (int) file.length();
        MappedByteBuffer buffer = fileInputStream.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);

        clear(bytes);
        ByteBuf buf = Unpooled.buffer();

        //将请求头发送到服务端，请求头编号，文件长度，文件存储路径
        buf.writeByte(REQUEST_START);
        buf.writeInt(fileLength);
        buf.writeInt(storePath.length());
        buf.writeBytes(storePath.getBytes());

        //将消息体发送到服务端
        for (int i = 0; i < fileLength/SEGMENT_LENGTH; i++) {
            buffer.get(bytes);
            buf.writeBytes(bytes);
        }


        int remain = buffer.remaining();
        buffer.get(bytes, 0 , remain);
        buf.writeBytes(bytes, 0, remain);

        channel.writeAndFlush(buf);
    }

    void clear(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = 0;
        }
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channel = ctx.channel();
        System.out.println("成功连接到服务器");
    }

}
