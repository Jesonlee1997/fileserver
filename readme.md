基于Netty的高性能文件服务器-JJFS  
使用自定义文件传输协议
支持上传文件或目录，上传多个文件时使用多个通道同时传输加速


使用方法：
分为Java客户端和Java服务端。  
Java服务端运行的程序：  
TODO：在linux上部署



Java客户端使用
将JJFSClient的jar包放入classpath下
api使用：
新建一个JJFSClient，指定文件服务器的地址，

    JJFSClient client = new JJFSClient("127.0.0.1", 1912);
    
指定本地文件的路径和将要上传到的远程文件的路径
如果本地文件是目录，则对应的远程文件也是目录
    
    String localPath = "D:/java/test.dat";
    String remotePath = "/test1.dat";
    client.uploadFile(localPath, remotePath);
    
注意：不要在junit test中运行，可能会出现数据还没传输完线程就结束。
