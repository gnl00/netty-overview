package com.demo.sticky_split;

import com.demo.sticky_split.codec.MessageProtocolDecoder;
import com.demo.sticky_split.codec.MessageProtocolEncoder;
import com.demo.sticky_split.split.SplitClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * StickyClient
 *
 * @author gnl
 * @since 2023/5/23
 */
public class SClient {
    private final String host;
    private final int port;

    public SClient(String host, int port) {
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
                                    // .addLast(new StickyClientHandler());
                                    .addLast(new MessageProtocolEncoder())
                                    .addLast(new MessageProtocolDecoder())
                                    .addLast(new SplitClientHandler());
                        }
                    });

            ChannelFuture f = b.connect().sync();
            System.out.println("[client] connected to server " + f.channel().remoteAddress());
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            worker.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new SClient("localhost", 6666).start();
    }
}
