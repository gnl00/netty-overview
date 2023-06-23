package com.demo.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

/**
 * HttpServerChannelHandler
 *
 * @author gnl
 * @since 2023/5/22
 */
public class HttpServerChannelHandler extends SimpleChannelInboundHandler<HttpObject> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) msg;

            // handle
            // 资源过滤，只处理感兴趣的资源路径
            String uriStr = httpRequest.uri();
            System.out.println("uri: " + uriStr);

            URI uri = new URI(uriStr);
            if (uri.getPath().contains("favicon.ico")) {
                System.out.println("无法处理该资源请求～ " + uri.getPath());
            }

            // response
            ByteBuf buffer = Unpooled.copiedBuffer("[response] hi~ I am a server based on Netty", CharsetUtil.UTF_8);
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buffer);

            // set response headers
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, buffer.readableBytes());

            // flush to remote
            ctx.writeAndFlush(response);
        }
    }
}
