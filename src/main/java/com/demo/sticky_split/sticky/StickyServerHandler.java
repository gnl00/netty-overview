package com.demo.sticky_split.sticky;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.util.UUID;

/**
 * StickyServerHandler
 *
 * @author gnl
 * @since 2023/5/23
 */
public class StickyServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private int count;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        byte[] buffer = new byte[msg.readableBytes()];

        msg.readBytes(buffer);
        String str = new String(buffer, CharsetUtil.UTF_8);
        System.out.println("[server] received: " + str);
        System.out.println(this.count++);
        ByteBuf response = Unpooled.copiedBuffer(UUID.randomUUID().toString() + "\n", CharsetUtil.UTF_8);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        cause.printStackTrace();
    }
}
