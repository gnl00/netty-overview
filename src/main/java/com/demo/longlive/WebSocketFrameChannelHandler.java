package com.demo.longlive;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.CharsetUtil;

import java.time.LocalDateTime;

/**
 * WebSocketFrameChannelHandler
 * WebSocket 是以帧为单位进行数据传输的
 *
 * @author gnl
 * @since 2023/5/23
 */
public class WebSocketFrameChannelHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerAdded");
        Channel channel = ctx.channel();
        System.out.println("asLongText " + channel.id().asLongText());
        System.out.println("asShortText " + channel.id().asShortText());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        String text = msg.text();
        System.out.println("[received] " + text);
        ctx.channel().writeAndFlush(new TextWebSocketFrame(LocalDateTime.now().toString()));
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerRemoved");
        Channel channel = ctx.channel();
        System.out.println("asLongText " + channel.id().asLongText());
        System.out.println("asShortText " + channel.id().asShortText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        cause.printStackTrace();
    }
}
