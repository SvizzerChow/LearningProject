package ink.chow.learning.service.netty;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

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
        byteBuf.writeCharSequence("hello", StandardCharsets.UTF_8);
        final ChannelFuture f = ctx.writeAndFlush(byteBuf);
        /*f.addListener((ChannelFutureListener) future -> {
            assert f == future;
            ctx.close();
        });*/
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        try {
            byte[] b = new byte[1024];
            int size = 0;
            while (in.isReadable()){
                if (size == b.length){
                    b = Arrays.copyOf(b, b.length*2);
                }
                b[size++] = in.readByte();
            }
            String str = new String(b, 0, size);
            System.out.println(str);
            System.out.flush();
            if ("你好".equals(str)){
                final ByteBuf byteBuf = ctx.alloc().buffer(4);
                byteBuf.writeCharSequence("time", StandardCharsets.UTF_8);
                ctx.writeAndFlush(byteBuf);
            }
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

}
