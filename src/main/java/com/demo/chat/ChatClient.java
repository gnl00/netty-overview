package com.demo.chat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * ChatClient
 *
 * @author gnl
 * @since 2023/5/23
 */
public class ChatClient {

    private final String host;

    private final int port;

    public ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap()
                    .group(worker)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new StringDecoder())
                                    .addLast(new StringEncoder())
                                    .addLast(new ChatChannelHandler());
                        }
                    });

            ChannelFuture future = b.connect().sync();
            Channel channel = future.channel();
            System.out.println("==> " + channel.localAddress().toString() + " ==> Started");
            Scanner sc = new Scanner(System.in);
            while (sc.hasNextLine()) {
                String msg = sc.nextLine();
                channel.writeAndFlush(msg + "\n");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            worker.shutdownGracefully();
        }

    }

    public static void main(String[] args) {
        new ChatClient("localhost", 6666).start();
    }
}
