package com.demo.sticky_split.split;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * SplitClientHandler
 *
 * @author gnl
 * @since 2023/5/23
 */
public class SplitClientHandler extends SimpleChannelInboundHandler<MessageProtocol> {
    private int count;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0; i < 10; i++) {
            String contentStr = "hello, this is client\n";
            MessageProtocol msg = new MessageProtocol();
            byte[] content = contentStr.getBytes(CharsetUtil.UTF_8);
            msg.setContent(content);
            msg.setLen(content.length);

            ctx.writeAndFlush(msg);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {
        int len = msg.getLen();
        byte[] buffer = msg.getContent();

        System.out.println("[CLIENT] received: ");
        System.out.println("len: " + len);
        String str = new String(buffer, CharsetUtil.UTF_8);
        System.out.println("content: " + str);
        System.out.println(this.count++);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        cause.printStackTrace();
    }
}
