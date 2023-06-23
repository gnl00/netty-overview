package com.demo.proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;

import java.net.InetSocketAddress;
import java.util.Objects;

public class ProxyServerChannelHandler extends ChannelInboundHandlerAdapter {

    private Channel serverInBoundCh;
    private Channel clientOutBoundCh;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ProxyServerChannelHandler#channelActive");
        serverInBoundCh = ctx.channel();

        // channelActive 的时候创建 proxy-client 连接到目标服务器
        initProxyClient();
    }

    private void initProxyClient() {
        ProxyConfig config = new ProxyConfig("127.0.0.1", 6379, 6380);
        System.out.println("Proxying port: " + config.getMappingPort() + " to: " + config.getPort());
        initClientBootstrap(config.getHost(), config.getPort());
    }

    private void initClientBootstrap(String host, int port) {
        Bootstrap b = new Bootstrap()
                .group(serverInBoundCh.eventLoop()) // inBound 和 outBound 使用一个 eventLoop
                .channel(serverInBoundCh.getClass())
                .remoteAddress(new InetSocketAddress(host, port))
                .option(ChannelOption.AUTO_READ, false)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ProxyClientChannelHandler(serverInBoundCh));
                    }
                });

        ChannelFuture f = b.connect();
        clientOutBoundCh = Objects.isNull(clientOutBoundCh) ? f.channel() : clientOutBoundCh;
        f.addListener(future -> {
            if (future.isSuccess()) {
                // triggers an ChannelInboundHandler.channelRead
                serverInBoundCh.read(); // clientOutBoundCh 连接成功，读取 serverInBoundCh 中的数据
            } else {
                serverInBoundCh.close();
            }
        });
    }

    // 收到来自【被代理客户端】的消息，使用【代理客户端】转发到【目标服务器】
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 收到消息后使用 proxy-client 发送到目标服务
        if (clientOutBoundCh.isActive()) {
            ChannelFuture f = clientOutBoundCh.writeAndFlush(msg);
            f.addListener(future -> {
                if (future.isSuccess()) {
                    ctx.channel().read(); // 读取下一条消息
                } else {
                    f.channel().close();
                }
            });
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (Objects.nonNull(clientOutBoundCh)) {
            cleanup(clientOutBoundCh);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        cleanup(ctx.channel());
    }

    public static void cleanup(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }

    }
}
