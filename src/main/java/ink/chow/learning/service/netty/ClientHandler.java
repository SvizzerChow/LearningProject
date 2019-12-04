package ink.chow.learning.service.netty;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Description:
 *
 * @author
 * @date 2019/12/4 19:57
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final ByteBuf byteBuf = ctx.alloc().buffer(4);
        byteBuf.writeCharSequence("hello", Charset.defaultCharset());
        final ChannelFuture f = ctx.writeAndFlush(byteBuf);
        f.addListener((ChannelFutureListener) future -> {
            assert f == future;
            ctx.close();
        });
    }
}