/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package me.jesonlee.jjfsserver.httpserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.apache.log4j.Logger;

public final class HttpStaticFileServer {

    static final boolean SSL = System.getProperty("ssl") != null;
    static int port;
    static String fileRoot = "J:\\Java\\projects\\fileserver\\store";
    private static Logger logger = Logger.getLogger(HttpStaticFileServer.class);

    public static void main(String[] args) throws Exception {

        port = Integer.parseInt(System.getProperty("httpPort"));
        if (port < 1000 || port > 65535) {
            logger.error("port can not less than 1000 or beyond 65535");
            System.exit(1);
        }
        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContext.newServerContext(SslProvider.JDK, ssc.certificate(), ssc.privateKey());
        } else {
            sslCtx = null;
        }

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new HttpStaticFileServerInitializer(sslCtx));

            Channel ch = b.bind(port).sync().channel();
            System.out.println("http文件服务器运行在" + port);
            logger.info("http文件服务器运行" + port);

            System.out.println("Open your web browser and navigate to " +
                    (SSL? "https" : "http") + "://127.0.0.1:" + port + '/');

            ch.closeFuture().sync();

        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
