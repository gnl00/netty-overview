package com.demo.greenis;

import com.demo.greenis.handler.GreenisChannelInboundHandler;
import com.demo.greenis.handler.GreenisChannelOutboundHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class GreenisClient {
    private String host;
    private int port;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public GreenisClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap()
                .group(worker)
                .channel(NioSocketChannel.class)
                .remoteAddress(host, port)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new GreenisChannelOutboundHandler())
                                .addLast(new GreenisChannelInboundHandler())
                        ;
                    }
                });
        try {
            ChannelFuture future = b.connect().sync();
            System.out.println("connected to host: " + host + ", port: " + port);

            // String cmdStr = "*2\r\n$3\r\nGET\r\n$3\r\nfoo\r\n";
            // String cmdStr = "*2\r\n$3\r\nget\r\n$4\r\nkey1\r\n";

            String[] cmds = execute("get", "foo");
            // ByteBuf buffer = Unpooled.buffer(cmdStr.length());
            // buffer.writeBytes(cmdStr.getBytes(StandardCharsets.US_ASCII));
            // future.channel().writeAndFlush(buffer);

            if (cmds.length > 0) {
                future.channel().writeAndFlush(cmds);
            }

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            worker.shutdownGracefully();
        }
    }

    private String[] execute(String...commands) {
        return commands;
    }

    public static void main(String[] args) {
        new GreenisClient("localhost", 6379).start();
    }
}
