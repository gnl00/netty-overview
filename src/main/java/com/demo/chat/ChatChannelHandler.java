package com.demo.chat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * ChatChannelHandler
 *
 * @author gnl
 * @since 2023/5/23
 */
public class ChatChannelHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        // output client msg
        System.out.println(msg.trim());
    }
}
