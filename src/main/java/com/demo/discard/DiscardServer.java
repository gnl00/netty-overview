package com.demo.discard;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Objects;

/**
 * DiscardServer
 *
 * @author gnl
 * @since 2023/5/21
 */
public class DiscardServer {

    private final int port;

    public DiscardServer(int port) {
        this.port = port;
    }

    public void start() {
        // NioEventLoopGroup 用来处理 I/O 操作的多线程事件循环器
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(); // 接收连接
        // boss 接收到信息后注册到 worker 上，由 worker 处理
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(); // 处理连接
        try {
            // ServerBootstrap 启动 NIO 服务的辅助启动类
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup) // Set the EventLoopGroup for the parent (acceptor) and the child (client)
                    .channel(NioServerSocketChannel.class) // The Class which is used to create Channel instances
                    .childHandler(new ChannelInitializer<SocketChannel>() { // Set the ChannelHandler which is used to serve the request for the Channel's
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new DiscardChannelHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128) // 设置线程队列得到连接个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // 设置保持活动连接状态
            ChannelFuture future = bootstrap.bind(port).sync();

            System.out.println("port: " + port + " is starting");

            future.channel().close(); // 关闭 channel
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }
}
