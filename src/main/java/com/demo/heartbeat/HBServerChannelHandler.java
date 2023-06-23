package com.demo.heartbeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * TODO
 *
 * @author gnl
 * @since 2023/5/23
 */
public class HBServerChannelHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent state = (IdleStateEvent) evt;
            String idleType = null;
            switch (state.state()) {
                case READER_IDLE:
                    idleType = "READER_IDLE";
                    break;
                case WRITER_IDLE:
                    idleType = "WRITER_IDLE";
                    break;
                case ALL_IDLE:
                    idleType = "ALL_IDLE";
                    break;
                default:
                    break;
            }

            System.out.println(ctx.channel().remoteAddress() + " ==> timeout ==> " + idleType);

            // 空闲则关闭通道
            // ctx.channel().close();
        }
    }
}
