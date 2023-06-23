package com.demo.greenis;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.redis.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.util.ArrayList;
import java.util.List;

public class GreenisChannelHandler extends ChannelDuplexHandler {

    // 发送 redis 命令
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        String[] cmds = ((String) msg).split("\\s+");
        ArrayList<RedisMessage> messages = new ArrayList<>(cmds.length);
        for (String cmd : cmds) {
            FullBulkStringRedisMessage redisMessage =
                    new FullBulkStringRedisMessage(ByteBufUtil.writeUtf8(ctx.alloc(), cmd));
            messages.add(redisMessage);
        }
        ArrayRedisMessage requestMessages = new ArrayRedisMessage(messages);
        ctx.write(requestMessages, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RedisMessage message = (RedisMessage) msg;
        printResponse(message);
        System.out.print(">");
        ReferenceCountUtil.release(message);
    }

    private void printResponse(RedisMessage message) {
        if (message instanceof SimpleStringRedisMessage) {
            String content = ((SimpleStringRedisMessage) message).content();
            System.out.println(content);
        } else if (message instanceof IntegerRedisMessage) {
            long value = ((IntegerRedisMessage) message).value();
            System.out.println(value);
        } else if (message instanceof FullBulkStringRedisMessage) {
            ByteBuf content = ((FullBulkStringRedisMessage) message).content();
            System.out.println(content.toString(CharsetUtil.UTF_8));
        } else if (message instanceof ArrayRedisMessage) {
            List<RedisMessage> children = ((ArrayRedisMessage) message).children();
            for (RedisMessage child : children) {
                printResponse(child);
            }
        } else if (message instanceof ErrorRedisMessage) {
            String content = ((ErrorRedisMessage) message).content();
            System.out.println(content);
        } else {
            throw new RuntimeException("Unknown message type " + message);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
