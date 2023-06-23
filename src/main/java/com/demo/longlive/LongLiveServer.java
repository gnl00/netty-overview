package com.demo.longlive;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * LongLiveServer 支持长连接的服务器
 *
 * @author gnl
 * @since 2023/5/23
 */
public class LongLiveServer {

    private final int port;

    public LongLiveServer(int port) {
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
                            ch.pipeline()
                                    .addLast(new HttpServerCodec()) // 基于 http 协议实现的长连接，需要添加对应编解码器
                                    .addLast(new ChunkedWriteHandler()) // 以块的方式进行写
                                    // http 在传输的过程中是分段进行传输的，
                                    // HttpObjectAggregator 可以将分段的 http 数据聚合起来处理
                                    .addLast(new HttpObjectAggregator(8192))
                                    // 处理 WebSocket 协议
                                    // 访问 host:port/ws 时，将 http 协议升级成 ws 协议，保持长连接
                                    .addLast(new WebSocketServerProtocolHandler("/ws"))
                                    .addLast(new WebSocketFrameChannelHandler());
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
        new LongLiveServer(5000).start();
    }
}
