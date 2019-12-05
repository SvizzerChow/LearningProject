package ink.chow.learning.service.netty;

import java.util.Date;

import io.netty.buffer.ByteBuf;
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
            StringBuffer stringBuffer = new StringBuffer();
            while (in.isReadable()){
                stringBuffer.append((char)in.readByte());
            }
            String str = stringBuffer.toString();
            if ("hello".equals(str)){
                ctx.write("你好,干嘛");
            }
            if ("时间".equals(str)){
                ctx.write(System.currentTimeMillis());
            }
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
