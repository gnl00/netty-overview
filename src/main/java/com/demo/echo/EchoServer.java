package com.demo.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;

/**
 * EchoServer
 *
 * @author gnl
 * @since 2023/5/21
 */
public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public void start() {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap server = new ServerBootstrap()
                    .group(boss, worker)
                    .handler(new LoggingHandler())
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ch.pipeline()
                                     // .addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(ClassLoader.getSystemClassLoader())))
                                     // .addLast(new ObjectEncoder())
                                    .addLast(new EchoServerChannelHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // start
            ChannelFuture future = server.bind(port).sync();
            System.out.println("server started, listen on port " + port);

            // 对关闭 Channel 进行监听
            future.channel().closeFuture().sync(); // 阻塞直到 future 关闭

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }

    public static void main(String[] args) {
        new EchoServer(8080).start();
    }
}
