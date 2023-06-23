package com.demo.rpc.client;

import com.demo.rpc.constant.Constant;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * RPCClientChannelHandler
 *
 * @author gnl
 * @since 2023/5/26
 */
public class RPCClientChannelHandler extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext ctx;

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("RPCClientChannelHandler#channelActive");
        this.ctx = ctx;
        String method = "str#";
        // RPCService#str#
        String execTarget = Constant.INVOKE_PREFIX + method;
        String param = "111";
        ctx.writeAndFlush(execTarget + param);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(msg.toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
