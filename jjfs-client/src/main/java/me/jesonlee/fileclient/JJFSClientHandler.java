package me.jesonlee.fileclient;

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
class JJFSClientHandler extends ChannelInboundHandlerAdapter {
    private Channel channel;

    private byte[] bytes = new byte[Constants.SEGMENT_LENGTH];

    List<File> files = new ArrayList<>();


    public void deleteFile(String storePath) {
        ByteBuf buf = Unpooled.buffer();

        int length = 1 + 4 + storePath.length() + 1;
        buf.writeInt(length);
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


        sendUploadRequestHead(file, storePath);

        sendUploadRequestBody(file);

    }


    public void sendUploadRequestHead(File file, String storePath) {
        //报文长度
        int length = 1 + 4 + storePath.length() + 1 + 4;
        ByteBuf buf = Unpooled.buffer(length + 4);
        buf.writeInt(length);//写入请求头的长度
        buf.writeByte(Constants.REQUEST_START);
        buf.writeInt(storePath.length());
        buf.writeBytes(storePath.getBytes());
        buf.writeByte(Constants.OPT_NEW);
        buf.writeInt((int) file.length());
        channel.writeAndFlush(buf);
    }

    public void sendUploadRequestBody(File file) {
        try {
            int fileLength = (int) file.length();
            FileInputStream fileInputStream = new FileInputStream(file);
            MappedByteBuffer buffer = fileInputStream.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);


            //将消息体发送到服务端，如果消息体大于1020就分几次发送
            for (int i = 0; i < fileLength / Constants.SEGMENT_LENGTH; i++) {
                ByteBuf buf = Unpooled.buffer(Constants.SEGMENT_LENGTH);
                buffer.get(bytes);
                buf.writeInt(Constants.SEGMENT_LENGTH + 1);
                buf.writeByte(Constants.BODY_START);
                buf.writeBytes(bytes);
                channel.writeAndFlush(buf);
            }


            //将剩余的字节发生给服务器
            int remain = buffer.remaining();
            ByteBuf buf = Unpooled.buffer(buffer.remaining() + 1 + 4);
            buf.writeInt(remain + 1);
            buf.writeByte(Constants.BODY_START);
            buffer.get(bytes, 0, remain);
            buf.writeBytes(bytes, 0, remain);
            channel.writeAndFlush(buf);

            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channel = ctx.channel();
        System.out.println("成功连接到服务器");
    }
}

