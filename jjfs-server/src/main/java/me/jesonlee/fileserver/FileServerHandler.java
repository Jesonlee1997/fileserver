package me.jesonlee.fileserver;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by JesonLee
 * on 2017/5/20.
 */
class FileServerHandler extends ChannelInboundHandlerAdapter {


    private int fileRemain;

    //MappedByteBuffer mappedByteBuffer; TODO：大文件使用
    private FileOutputStream outputStream;
    private RequestReaderBuffer requestReaderBuffer;
    private LengthBuffer lengthBuffer;
    private Channel channel;
    private String fileName;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channel = ctx.channel();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        //System.out.println(buf.readableBytes());
        //buffer中还有未处理的数据
        int length;

        //如果lengthBuffer中有数据的话 buffer中就一定没有
        if (lengthBuffer != null) {
            length = lengthBuffer.mergeLength(buf);
            RequestReader reader = new RequestReader(buf, length);
            handle(reader);
            lengthBuffer = null;
        } else if (requestReaderBuffer != null) {//buffer中有上次没处理完的数据
            requestReaderBuffer.add(buf);
            if (!requestReaderBuffer.complete()) {
                return;
            }
            handle(requestReaderBuffer);
            requestReaderBuffer = null;
        }

        //判断buf中还是否有其他请求
        while (buf.readableBytes() > 3) {
            length = buf.readInt();

            //有一个完整的请求
            if (length <= buf.readableBytes()) {
                RequestReader reader = new RequestReader(buf, length);
                handle(reader);
            } else {
                //有一个不完整的请求
                requestReaderBuffer = new RequestReaderBuffer(buf, length);
                return;
            }
        }

        //说明代表长度的int被拆了包
        if (buf.readableBytes() > 0) {
            lengthBuffer = new LengthBuffer(buf);
        }
        buf.release();

    }

    /**
     * 处理单个请求
     * @param reader
     * @throws IOException
     */
    private void handle(Reader reader) throws IOException {
        byte start = reader.readByte();

        if (start == Constants.REQUEST_START) {
            String storePath = getStorePath(reader);
            byte opt = reader.readByte();

            if (opt == Constants.OPT_DELETE) {
                doDelete(storePath);
                return;
            }

            File file = new File(storePath);
            if (opt == Constants.OPT_NEW) {
                mkDirectory(storePath);
                file.createNewFile();
                outputStream = new FileOutputStream(file);
                fileRemain = reader.readInt();//长度
                return;
            }

        }

        if (start == Constants.BODY_START) {
            fileRemain -= reader.length() - 1;
            System.out.println("fileRemain" + fileRemain);
            reader.readBytes(outputStream, reader.length() - 1);
            if (fileRemain == 0) {
                outputStream.close();
            }
        }
    }

    void sendSuccessResponse(String fileName) {

    }

    /**
     * @param storePath "/test/1.txt
     *                  /test/*\/
     *                  /*
     * @return
     */
    private boolean doDelete(String storePath) {
        int index = storePath.lastIndexOf("*");
        if (index == -1) {
            File file = new File(storePath);
            if (file.isDirectory()) {
                doDeleteDir(file);
            }
            return file.delete();
        }

        if (index > FileServer.rootPath.length()) {
            String directory = storePath.substring(0, index - 1);
            File file = new File(directory);
            for (File child : file.listFiles()) {
                if (child.isDirectory()) {
                    doDeleteDir(child);
                } else {
                    child.delete();
                }
            }
        }
        return true;
    }

    /**
     * 根据路径创建目录
     * @param storePath
     */
    private void mkDirectory(String storePath) {
        int index = storePath.lastIndexOf("/");
        if (index > FileServer.rootPath.length()) {
            String directory = storePath.substring(0, index);
            File file = new File(directory);
            file.mkdirs();
        }
    }

    /**
     * 获取文件将要在文件服务器上存储的路径
     * @param reader
     * @return
     */
    private String getStorePath(Reader reader) {
        int length = reader.readInt();
        byte[] bytes = new byte[length];
        reader.readBytes(bytes);
        fileName = new String(bytes);
        String storePath = FileServer.rootPath + fileName;
        return storePath;
    }

    /**
     * 删除目录
     * @param dir 将要删除的目录路径
     */
    private void doDeleteDir(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                doDeleteDir(file);
            }
            file.delete();
        }
        dir.delete();
    }
}
