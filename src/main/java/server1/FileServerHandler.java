package server1;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by JesonLee
 * on 2017/5/20.
 */
class FileServerHandler extends ChannelInboundHandlerAdapter {


    private boolean start = true;
    private int fileRemain;

    //MappedByteBuffer mappedByteBuffer; TODO：大文件使用
    private FileOutputStream outputStream;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf buf = (ByteBuf) msg;

        if (start) {
            byte b = buf.readByte();
            if (b != 100) {
                byte[] bytes = new byte[buf.readableBytes()];
                buf.readBytes(bytes);
                System.out.println(bytes);
            }

            String storePath = getStorePath(buf);


            byte opt = buf.readByte();
            if (opt == Constants.OPT_DELETE) {
                doDelete(storePath);
                start = true;
                return;
            }

            File file = new File(storePath);
            if (opt == Constants.OPT_NEW) {
                fileRemain = buf.readInt();
                mkDirectory(storePath);
                file.createNewFile();
                outputStream = new FileOutputStream(file);
                start = false;
            }
        }


        //读取的字节数
        int readNum = (buf.readableBytes() > fileRemain) ? fileRemain : buf.readableBytes();
        fileRemain -= readNum;

        buf.readBytes(outputStream, readNum);

        if (fileRemain == 0) {
            outputStream.close();
            start = true;
            if (buf.readableBytes() > 0) {
                channelRead(ctx, buf);
            }
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
