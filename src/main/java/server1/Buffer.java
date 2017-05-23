package server1;

import io.netty.buffer.ByteBuf;

/**
 * 用来存放未读取完的数据
 * Created by JesonLee
 * on 2017/5/22.
 */
public class Buffer {
    private byte[] bytes = new byte[1024];

    private int totalLength;
    private int currentLength;

    public Buffer(ByteBuf buf, int length) {
        totalLength = length;

        currentLength = buf.readableBytes();
        buf.readBytes(bytes, 0, currentLength);

    }

    private int position;

    public int position() {
        return position;
    }

    public int readInt() {
        required(4);
        return (bytes[position++] & 0xFF) << 24 //
                | (bytes[position++] & 0xFF) << 16 //
                | (bytes[position++] & 0xFF) << 8 //
                | bytes[position++] & 0xFF;
    }

    public int readByte() {
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
        buf.readBytes(bytes, currentLength, totalLength - currentLength);
        currentLength = totalLength;
    }

    public boolean isReady() {
        return currentLength == totalLength;
    }
}
