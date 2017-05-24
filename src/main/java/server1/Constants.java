package server1;

/**
 * Created by JesonLee
 * on 2017/5/20.
 */
public class Constants {
    //传输文件的数据报文的实际长度 还有4字节的length和标识位
    public static final int SEGMENT_LENGTH = 1019;

    public static final byte REQUEST_START = 100;
    public static final byte BODY_START = 101;

    public static final byte OPT_DELETE = 110;
    public static final byte OPT_NEW = 111;
}
