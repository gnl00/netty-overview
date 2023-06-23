package com.demo.sticky_split.sticky;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * StickyClientHandler
 *
 * @author gnl
 * @since 2023/5/23
 */
public class StickyClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private int count;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0; i < 10; i++) {
            ByteBuf buffer = Unpooled.copiedBuffer("hello, this is client\n", CharsetUtil.UTF_8);
            ctx.writeAndFlush(buffer);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        byte[] buffer = new byte[msg.readableBytes()];

        msg.readBytes(buffer);
        String str = new String(buffer, CharsetUtil.UTF_8);
        System.out.println("[client] received: " + str);
        System.out.println(this.count++);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        cause.printStackTrace();
    }
}
