package com.demo.sticky_split.split;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.util.UUID;

/**
 * SplitServerHandler
 *
 * @author gnl
 * @since 2023/5/23
 */
public class SplitServerHandler extends SimpleChannelInboundHandler<MessageProtocol> {

    private int count;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {
        // received
        int len = msg.getLen();
        byte[] buffer = msg.getContent();

        System.out.println("[SERVER] received: ");
        System.out.println("len: " + len);
        String str = new String(buffer, CharsetUtil.UTF_8);
        System.out.println("content: " + str);
        System.out.println(this.count++);

        // response
        MessageProtocol response = new MessageProtocol();
        String responseStr = UUID.randomUUID().toString();
        byte[] content = responseStr.getBytes(CharsetUtil.UTF_8);
        response.setContent(content);
        response.setLen(content.length);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        cause.printStackTrace();
    }
}
