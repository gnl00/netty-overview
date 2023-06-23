package com.demo.ssl;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

/**
 * TODO
 *
 * @author gnl
 * @since 2023/5/26
 */
public class SSLChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext ctx;
    private final boolean enableTSL;
    private final boolean clientMode;

    public SSLChannelInitializer(SslContext ctx, boolean enableTSL, boolean clientMode) {
        this.ctx = ctx;
        this.enableTSL = enableTSL;
        this.clientMode = clientMode;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        SSLEngine sslEngine = ctx.newEngine(ch.alloc());
        sslEngine.setUseClientMode(clientMode);

        ch.pipeline().addLast(new SslHandler(sslEngine, enableTSL));
    }
}
