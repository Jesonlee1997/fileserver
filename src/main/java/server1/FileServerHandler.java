package server1;

import io.netty.buffer.ByteBuf;
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

    private Buffer buffer;
    private LengthBuffer lengthBuffer;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        //buffer中还有未处理的数据
        int length;

        //如果lengthBuffer中有数据的话 buffer中就一定没有
        if (lengthBuffer != null) {
            lengthBuffer.mergeLength(buf);
            length = lengthBuffer.getLength();
            handle(buf, length);
        } else if (buffer != null) {//buffer中有上次没处理完的数据
            buffer.add(buf);
            handle(buffer);
        }

        //判断buf中还是否有其他请求
        while (buf.readableBytes() > 3) {
            length = buf.readInt();

            //有一个完整的请求
            if (length < buf.readableBytes()) {
                handle(buf, length);
            } else {
                //有一个不完整的请求
                buffer = new Buffer(buf, length);
                return;
            }
        }

        //说明int类型的长度被拆了包
        if (buf.readableBytes() > 0) {
            lengthBuffer = new LengthBuffer(buf);
        }

    }

    private void mkDirectory(String storePath) {
        int index = storePath.lastIndexOf("/");
        if (index > FileServer.rootPath.length()) {
            String directory = storePath.substring(0, index);
            File file = new File(directory);
            file.mkdirs();
        }
    }

    private String getStorePath(ByteBuf buf) {
        int length = buf.readInt();
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        String storePath = FileServer.rootPath + new String(bytes);
        return storePath;
    }

    public void handle(Buffer buffer) {

    }

    /**
     * 处理一个完整的请求
     * @param buf
     * @param length 这个请求在buf中的长度
     * @throws IOException
     */
    public void handle(ByteBuf buf, int length) throws IOException {
        byte start = buf.readByte();

        if (start == Constants.REQUEST_START) {

            String storePath = getStorePath(buf);

            byte opt = buf.readByte();
            if (opt == Constants.OPT_DELETE) {
                doDelete(storePath);
                return;
            }

            File file = new File(storePath);
            if (opt == Constants.OPT_NEW) {
                mkDirectory(storePath);
                file.createNewFile();


                outputStream = new FileOutputStream(file);
                fileRemain = buf.readInt();//长度
                return;
            }

        }
        if (start == Constants.BODY_START) {
            fileRemain -= length - 1;
            buf.readBytes(outputStream, length - 1);
            if (fileRemain == 0) {
                outputStream.close();
            }
        }
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
