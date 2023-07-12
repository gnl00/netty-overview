package com.demo.proxy;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


public class ProxyClientChannelHandler extends ChannelInboundHandlerAdapter {

    private final Channel proxyServerInBoundChannel;

    public ProxyClientChannelHandler(Channel inBoundChannel) {
        this.proxyServerInBoundChannel = inBoundChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ProxyClientChannelHandler#channelActive");
        ctx.read();
    }

    // 收到来自【目标服务器】的消息，使用【代理服务器】转发到【被代理客户端】
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ChannelFuture f = proxyServerInBoundChannel.writeAndFlush(msg); // 转发到【被代理客户端】
        f.addListener(future -> {
            if (future.isSuccess()) {
                // ctx.channel() = targetChannel
                ctx.channel().read();
            } else {
                ctx.close();
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ProxyServerChannelHandler.cleanup(proxyServerInBoundChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ProxyServerChannelHandler.cleanup(ctx.channel());
    }
}
