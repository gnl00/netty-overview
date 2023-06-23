package com.demo.proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class TestClient {

    private String host;
    private int port;

    public TestClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap()
                .group(worker)
                .channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress(host, port))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new TestClientChannelHandler());
                    }
                });

        try {
            ChannelFuture f = b.connect().sync();
            System.out.println("[test-client] started");
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            worker.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        TestClient client = new TestClient("127.0.0.1", 6380);
        client.start();
    }
}
