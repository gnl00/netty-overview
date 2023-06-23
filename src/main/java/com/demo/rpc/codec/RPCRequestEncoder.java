package com.demo.rpc.codec;

import com.demo.rpc.request.RPCRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * RPCRequestEncoder
 *
 * @author gnl
 * @since 2023/5/26
 */
public class RPCRequestEncoder extends MessageToByteEncoder<RPCRequest> {
    @Override
    protected void encode(ChannelHandlerContext ctx, RPCRequest msg, ByteBuf out) throws Exception {

    }
}
