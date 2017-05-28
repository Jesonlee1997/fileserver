package me.jesonlee.fileserver;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by JesonLee
 * on 2017/5/24.
 */
public interface Reader {

    byte readByte();

    int readInt();

    void readBytes(byte[] dst);

    void readBytes(OutputStream outputStream, int length) throws IOException;

    //表示Reader中的数据长度
    int length();

    boolean complete();
}
