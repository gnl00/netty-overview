package com.demo.rpc.codec;

import com.demo.rpc.request.RPCRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * RPCRequestDecoder
 *
 * @author gnl
 * @since 2023/5/26
 */
public class RPCRequestDecoder extends ReplayingDecoder<RPCRequest> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

    }
}
