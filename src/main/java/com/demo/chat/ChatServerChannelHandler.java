package com.demo.chat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ChatServerChannelHandler
 *
 * @author gnl
 * @since 2023/5/23
 */
public class ChatServerChannelHandler extends SimpleChannelInboundHandler<String> {

    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // 新连接加入
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channels.writeAndFlush("[server] 新连接加入 " + channel.remoteAddress() + sdf.format(new Date()));
        channels.add(channel);
        System.out.println("Size: " + channels.size());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + " active");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel channel = ctx.channel();
        System.out.println("[received] " + channel.remoteAddress() + ": " + msg);
        for (Channel ch : channels) {
            if (ch == channel) {
                ch.writeAndFlush("[you] " + msg + "\n");
            } else {
                ch.writeAndFlush("[" + channel.remoteAddress() + "] " + msg + "\n");
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + " inActive");
    }

    // 连接退出
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channels.remove(channel);
        channels.writeAndFlush("[server] 连接离开 " + channel.remoteAddress() + " " + sdf.format(new Date()));
        System.out.println("Size: " + channels.size());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        cause.printStackTrace();
    }
}
