package server1;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 用来存放未读取完的数据
 * Created by JesonLee
 * on 2017/5/22.
 */
public class RequestReaderBuffer implements Reader {
    private byte[] bytes = new byte[1024];

    private int totalLength;
    private int currentLength;

    public RequestReaderBuffer(ByteBuf buf, int length) {
        totalLength = length;

        currentLength = buf.readableBytes();
        buf.readBytes(bytes, 0, currentLength);

    }

    private int position;


    public int readInt() {
        required(4);
        return (bytes[position++] & 0xFF) << 24 //
                | (bytes[position++] & 0xFF) << 16 //
                | (bytes[position++] & 0xFF) << 8 //
                | bytes[position++] & 0xFF;
    }

    @Override
    public void readBytes(byte[] dst) {
        required(dst.length);
        System.arraycopy(bytes, position, dst, 0, dst.length);
        position += dst.length;
    }

    @Override
    public void readBytes(OutputStream outputStream, int length) throws IOException {
        required(length);
        outputStream.write(bytes, position, length);
        position += length;
    }

    public byte readByte() {
        required(1);
        return bytes[position++];
    }


    private void required(int num) {
        if ((position + num) > totalLength) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    //在第二段报文到达后被调用
    public void add(ByteBuf buf) {
        //可能会出现需要的字节数比buf中的更多的情况
        int readNum = Math.min(buf.readableBytes(), totalLength - currentLength);
        buf.readBytes(bytes, currentLength, readNum);
        currentLength += readNum;
    }

    @Override
    public int length() {
        return currentLength;
    }

    @Override
    public boolean complete() {
        return currentLength == totalLength;
    }

}
