package com.demo.rpc.server;

import com.demo.rpc.constant.Constant;
import com.demo.rpc.provider.RPCServiceImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * RPCServerChannelHandler
 *
 * @author gnl
 * @since 2023/5/26
 */
public class RPCServerChannelHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("RPCServerChannelHandler#handlerAdded");
        System.out.println(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 服务端处理远程调用逻辑
        String received = msg.toString();
        System.out.println("msg => " + received);


        // 定义一个调用协议，比如要求以 xxx 开头
        if (received.contains(Constant.INVOKE_PREFIX)) {
            String serviceName = received.substring(received.indexOf(Constant.INVOKE_PREFIX) + 1);
            Object result = findService(serviceName);
            ctx.writeAndFlush(result);
        }
    }

    private Object findService(String serviceName) {
        return new RPCServiceImpl().invoke(serviceName);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        cause.printStackTrace();
    }
}
