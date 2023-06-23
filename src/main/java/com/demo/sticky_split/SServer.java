package com.demo.sticky_split;

import com.demo.sticky_split.codec.MessageProtocolDecoder;
import com.demo.sticky_split.codec.MessageProtocolEncoder;
import com.demo.sticky_split.split.SplitServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * TCP 粘包现象测试
 *
 * @author gnl
 * @since 2023/5/23
 */
public class SServer {
    private final int port;

    public SServer(int port) {
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
                                    //.addLast(new StickyServerHandler());
                                    .addLast(new MessageProtocolEncoder())
                                    .addLast(new MessageProtocolDecoder())
                                    .addLast(new SplitServerHandler());
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
        new SServer(6666).start();
    }
}
