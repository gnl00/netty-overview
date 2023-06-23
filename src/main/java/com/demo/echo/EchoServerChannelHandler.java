package com.demo.echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

/**
 * EchoServerChannelHandler
 *
 * @author gnl
 * @since 2023/5/21
 */
public class EchoServerChannelHandler extends ChannelInboundHandlerAdapter {

    // channelRead 方法会在收到客户端新消息时被调用
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;

        try {
            System.out.println("[server] received " + byteBuf.toString(CharsetUtil.UTF_8));

            // response
            ctx.write("server echo");
            // ctx.flush();

        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER) // flush 缓存中的所有数据到到远程
                .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close(); // Close
    }
}
