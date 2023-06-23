package com.demo.echo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * EchoClient
 *
 * @author gnl
 * @since 2023/5/21
 */
public class EchoClient {

    private final String host;
    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap()
                    .group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                     // .addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(ClassLoader.getSystemClassLoader())))
                                     // .addLast(new ObjectEncoder())
                                    .addLast(new EchoClientChannelHandler());
                        }
                    });
            ChannelFuture future = b.connect().sync();
            System.out.println("echo client started");

            future.channel().closeFuture().sync(); // 阻塞直到 future 关闭
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            workerGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) {
        new EchoClient("localhost", 8080).start();
    }
}
