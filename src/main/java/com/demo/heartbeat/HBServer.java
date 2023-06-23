package com.demo.heartbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * HeartBeat 检测服务
 *
 * @author gnl
 * @since 2023/5/23
 */
public class HBServer {

    private final int port;

    public HBServer(int port) {
        this.port = port;
    }

    public void start() {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap()
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // IdleStateHandler 是 netty 提供的服务器空闲状态管理
                            // 当 IdleStateEvent 触发后 , 就会传递给管道的下一个 handler 处理
                            // 触发下一个handler 的 ChannelHandler#userEventTiggered 方法
                            ch.pipeline()
                                    .addLast(new IdleStateHandler(3, 5, 10, TimeUnit.SECONDS))
                                    .addLast(new HBServerChannelHandler());
                        }
                    });

            ChannelFuture future = b.bind(port).sync();
            System.out.println("[server] Started, port: " + port);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new HBServer(5566).start();
    }
}
