package com.demo.sticky_split.codec;

import com.demo.sticky_split.split.MessageProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * MessageProtocolEncoder
 *
 * @author gnl
 * @since 2023/5/23
 */
public class MessageProtocolEncoder extends MessageToByteEncoder<MessageProtocol> {
    @Override
    protected void encode(ChannelHandlerContext ctx, MessageProtocol msg, ByteBuf out) throws Exception {
        // MessageProtocol to ByteBuf
        System.out.println("encode MessageProtocol");
        out.writeInt(msg.getLen());
        out.writeBytes(msg.getContent());
    }
}
