package server1;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by JesonLee
 * on 2017/5/24.
 */
public class RequestReader implements Reader {
    private int position;
    private byte[] bytes;

    public RequestReader(int length) {
        this.bytes = new byte[length];
    }

    public byte readByte() {
        return bytes[position++];
    }

    public int readInt() {
        return (bytes[position++] & 0xFF) << 24 //
                | (bytes[position++] & 0xFF) << 16 //
                | (bytes[position++] & 0xFF) << 8 //
                | bytes[position++] & 0xFF;
    }

    public RequestReader(ByteBuf buf, int length) {
        bytes = new byte[length];
        buf.readBytes(bytes);
    }

    public void readBytes(byte[] dst) {
        System.arraycopy(bytes, position, dst, 0, dst.length);
        position += dst.length;
    }

    public void readBytes(OutputStream outputStream, int length) throws IOException {
        outputStream.write(bytes, position, length);
        position += length;
    }

    @Override
    public int length() {
        return bytes.length;
    }
}
