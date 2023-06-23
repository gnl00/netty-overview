package com.demo.greenis;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.redis.RedisArrayAggregator;
import io.netty.handler.codec.redis.RedisBulkStringAggregator;
import io.netty.handler.codec.redis.RedisDecoder;
import io.netty.handler.codec.redis.RedisEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * A redis client based on netty
 */
public class GreenisClient {

    private String host;
    private int port;

    public GreenisClient(String host, int port) {
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
                        ch.pipeline()
                                .addLast(new RedisDecoder())
                                .addLast(new RedisEncoder())
                                .addLast(new RedisBulkStringAggregator())
                                .addLast(new RedisArrayAggregator())
                                .addLast(new GreenisChannelHandler());
                    }
                });

        try {
            ChannelFuture f = b.connect().sync();
            Channel ch = f.channel();
            System.out.println("connected to host: " + host + ", port: " + port);

            ChannelFuture currentChannelFuture = null;
            Scanner sc = new Scanner(System.in);
            System.out.print(">");
            while (sc.hasNextLine()) {
                String s = sc.nextLine();
                if (s.equalsIgnoreCase("q")) {
                    break;
                }

                currentChannelFuture = ch.writeAndFlush(s);
                currentChannelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
                    @Override
                    public void operationComplete(Future<? super Void> future) throws Exception {
                        if (!future.isSuccess()) {
                            future.cause().printStackTrace();
                        }
                    }
                });
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            worker.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        GreenisClient client = new GreenisClient("127.0.0.1", 6379);
        client.start();
    }
}
