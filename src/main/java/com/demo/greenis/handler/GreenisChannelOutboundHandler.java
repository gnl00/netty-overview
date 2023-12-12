package com.demo.greenis.handler;

import com.demo.greenis.model.Symbol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class GreenisChannelOutboundHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        String[] msgArr = (String[]) msg;
        System.out.println("outbound: " + Arrays.asList(msgArr));

        String commands = buildCommand(msgArr);
        ByteBuf buffer = Unpooled.buffer(commands.length());
        buffer.writeBytes(commands.getBytes(StandardCharsets.US_ASCII));

        super.write(ctx, buffer, promise);
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        super.flush(ctx);
    }

    private String buildCommand(String...commands) {
        StringBuilder cmdStr = new StringBuilder();
        cmdStr.append(Symbol.STAR);
        cmdStr.append(commands.length).append(Symbol.CRLF);
        for (String command : commands) {
            cmdStr.append(Symbol.DOLLAR).append(command.length()).append(Symbol.CRLF).append(command).append(Symbol.CRLF);
        }
        return cmdStr.toString();
    }

}
