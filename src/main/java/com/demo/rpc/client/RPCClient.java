package com.demo.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * RPCClient
 *
 * @author gnl
 * @since 2023/5/26
 */
public class RPCClient {

    private final String host;
    private final int port;
    private RPCClientChannelHandler clientHandler;

    public RPCClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        clientHandler = new RPCClientChannelHandler();
        Bootstrap b = new Bootstrap()
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new StringDecoder())
                                .addLast(new StringEncoder())
                                .addLast(clientHandler);
                    }
                });

        try {
            ChannelFuture future = b.connect(host, port).sync();
            future.channel().closeFuture().sync();
            System.out.println("Client started");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public void send() {
        clientHandler.getCtx().writeAndFlush("send outer");
    }


    public Object getService(final Class<?> srvClass,final String invokeMethod) {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{srvClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (null == clientHandler) {
                    start();
                }
                // clientHandler.setArgs(invokeMethod + args[0]);
                // do something
                return null;
            }
        });
    }
}
