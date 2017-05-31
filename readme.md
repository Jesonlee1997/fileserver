基于Netty的高性能文件服务器-JJFS  

目前只支持单机部署

使用精简的自定义文件传输协议
支持上传文件或目录，上传多个文件时使用多个通道同时传输加速


使用方法：
分为Java客户端和Java服务端。  
在服务端运行：
1. 获得压缩包 目录下的jjfs-server-1.0.tar.gz
2. 解压压缩包，根据需要修改配置文件  
contextRoot是文件存储和http访问的根目录，所有的文件操作和http文件的访问都是以此为根目录  
filePort是文件操作的端口，默认为1912
httpPort表示提供http文件服务的端口，默认为8081
3. 启动文件服务器， 进入bin目录`./run.sh start`
4. 停止服务， `./run.sh stop`
5. logs中记录了所有日志，jjfs.log是运行中产生的日志

    


Java客户端使用
将JJFSClient的jar包放入classpath下
api使用：
新建一个JJFSClient，指定文件服务器的地址，

    JJFSClient client = new JJFSClient("127.0.0.1", 1912);
    
指定本地文件的路径和将要上传到的远程文件的路径
如果本地文件是目录，则对应的远程文件也是目录，可直接指定路径，若路径不存在会直接创建
    
    String localPath = "D:/java/test.dat";
    String remotePath = "/test1.dat";
    client.uploadFile(localPath, remotePath);
    
注意：不要在junit的test中运行，可能会出现数据还没传输完线程就结束。

删除文件  
使用*来匹配所有文件
删除单个文件或目录

    client.deleteFile("/*");
    client.deleteFile("/data")