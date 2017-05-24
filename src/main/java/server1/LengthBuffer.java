package server1;

import io.netty.buffer.ByteBuf;

/**
 * 存放被拆包的int类型
 * Created by JesonLee
 * on 2017/5/23.
 */
public class LengthBuffer {
    private byte[] bytes;
    private int width;
    private int data;
    public int getLength() {
        return data;
    }


    public LengthBuffer(ByteBuf buf) {
        //buf.readableBytes() < 3
        width = buf.readableBytes();
        bytes = new byte[width];
        buf.readBytes(bytes, 0, width);
    }

    public boolean hasRemain() {
        return width != 0;
    }

    public int mergeLength(ByteBuf buf) {
        buf.readBytes(bytes, width, 4 - width);
        data = (bytes[0] & 0xFF) << 24 //
                | (bytes[1] & 0xFF) << 16 //
                | (bytes[2] & 0xFF) << 8 //
                | bytes[3] & 0xFF;
        return data;
    }
}
