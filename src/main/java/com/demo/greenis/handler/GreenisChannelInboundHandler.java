package com.demo.greenis.handler;

import com.demo.greenis.model.Symbol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class GreenisChannelInboundHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        System.out.println("inbound: " + msg);
        String recvStr = msg.toString(StandardCharsets.US_ASCII);

        String firstByte = recvStr.substring(0, 1); // 从第一个 byte 判断返回类型，来决定使用什么容器来接收数据
        // handle first byte...
        // 这里偷懒了...

        String subResponse = recvStr.substring(1); // 跳过 first byte 处理内容
        String[] responseArray = subResponse.split(Symbol.CRLF);
        Integer totalLen = Integer.valueOf(responseArray[0]);

        if (totalLen == -1) { // -1 表示返回 nil
            System.out.println("nil");
            return;
        }

        ArrayList<String> responseList = new ArrayList<>(totalLen);
        for (int i = 1; i < responseArray.length; i++) {
            String curr = responseArray[i];
            // check symbol
            if (!curr.startsWith(Symbol.DOLLAR)) {
                responseList.add(curr);
            }
        }

        System.out.println("*** received ***");
        if (responseList.size() > 1) {
            System.out.println(responseList);
        }
        System.out.println(responseList.get(0));
    }
}
