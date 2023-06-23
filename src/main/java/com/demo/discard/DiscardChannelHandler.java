package com.demo.discard;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * DiscardChannelHandler 忽略所有收到的数据
 *
 * @author gnl
 * @since 2023/5/21
 */
public class DiscardChannelHandler extends ChannelInboundHandlerAdapter {

    // channelRead 方法会在收到客户端新消息时被调用
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        byteBuf.release(); // Discard msg
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close(); // Close
    }
}
