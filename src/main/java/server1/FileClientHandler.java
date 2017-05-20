package server1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * 传输客户端
 */
class FileClientHandler extends ChannelInboundHandlerAdapter {
    private Channel channel;

    private byte[] bytes = new byte[Constants.SEGMENT_LENGTH];

    List<File> files = new ArrayList<>();


    public void deleteFile(String storePath) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(Constants.REQUEST_START);

        buf.writeInt(storePath.length());
        buf.writeBytes(storePath.getBytes());
        buf.writeByte(Constants.OPT_DELETE);
        channel.writeAndFlush(buf);
    }


    public synchronized void uploadFile(File file, String storePath) throws IOException {
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
        buf.writeByte(Constants.REQUEST_START);
        buf.writeInt(storePath.length());
        buf.writeBytes(storePath.getBytes());
        buf.writeByte(Constants.OPT_NEW);
        buf.writeInt(fileLength);
        channel.writeAndFlush(buf);

        buf = Unpooled.buffer();
        //将消息体发送到服务端
        for (int i = 0; i < fileLength / Constants.SEGMENT_LENGTH; i++) {
            buffer.get(bytes);
            buf.writeBytes(bytes);
        }

        int remain = buffer.remaining();
        buffer.get(bytes, 0, remain);
        buf.writeBytes(bytes, 0, remain);

        channel.writeAndFlush(buf);
        fileInputStream.close();
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
