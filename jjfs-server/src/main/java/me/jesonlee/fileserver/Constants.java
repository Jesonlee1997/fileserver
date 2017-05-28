package me.jesonlee.fileserver;

/**
 * Created by JesonLee
 * on 2017/5/20.
 */
class Constants {
    //传输文件的数据报文的实际长度 还有4字节的length和标识位
    static final byte REQUEST_START = 100;
    static final byte BODY_START = 101;

    static final byte OPT_DELETE = 110;
    static final byte OPT_NEW = 111;
}
