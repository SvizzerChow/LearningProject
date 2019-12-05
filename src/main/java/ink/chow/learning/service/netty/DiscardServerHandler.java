package ink.chow.learning.service.netty;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
 * @date 2019/12/4 17:18
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {

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
            final ByteBuf byteBuf = ctx.alloc().buffer(4);
            if ("hello".equals(str)){
                byteBuf.writeCharSequence("你好", StandardCharsets.UTF_8);
            }
            if ("time".equals(str)){
                byteBuf.writeCharSequence(new Date().toString(), StandardCharsets.UTF_8);
            }
            final ChannelFuture f = ctx.writeAndFlush(byteBuf);
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
