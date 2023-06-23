package com.demo.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * 利用 netty 实现 Http 服务
 *
 * @author gnl
 * @since 2023/5/22
 */
public class HttpServer {
    private final int port;

    public HttpServer(int port) {
        this.port = port;
    }

    public void start() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // HttpServerCodec 是 netty 提供的处理 http 的编/解码器
                            ch.pipeline().addLast(new HttpServerCodec()).addLast(new HttpServerChannelHandler());
                        }
                    });
            ChannelFuture future = bootstrap.bind(port).sync();

            System.out.println("server started on port " + port);

            future.channel().closeFuture().sync(); // 监听 close Channel 直到关闭
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new HttpServer(8999).start();
    }
}
