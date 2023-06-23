package com.demo.sticky_split.codec;

import com.demo.sticky_split.split.MessageProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * MessageProtocolDecoder
 *
 * @author gnl
 * @since 2023/5/23
 */
public class MessageProtocolDecoder extends ReplayingDecoder<MessageProtocol> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // ByteBuf to MessageProtocol
        System.out.println("decode MessageProtocol");
        int len = in.readInt();
        byte[] buf = new byte[len];

        in.readBytes(buf); // 将 len 长度的数据读入 buf
        // 封装成 MessageProtocol 传递给下一个 handler 处理
        MessageProtocol messageProtocol = new MessageProtocol();
        messageProtocol.setLen(len);
        messageProtocol.setContent(buf);
        out.add(messageProtocol);
    }
}
