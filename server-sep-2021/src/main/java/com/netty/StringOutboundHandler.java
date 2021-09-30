package com.netty;



import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;


@Slf4j
public class StringOutboundHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        String massage = (String) msg;
        ByteBuf buf = ctx.alloc().buffer();
        buf.writeCharSequence(massage, StandardCharsets.UTF_8);
        buf.retain();

        ctx.writeAndFlush(buf);

    }
}
