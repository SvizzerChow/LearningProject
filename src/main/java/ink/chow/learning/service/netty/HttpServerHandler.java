package ink.chow.learning.service.netty;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Description:
 *
 * @author
 * @date 2019/12/6 14:14
 */
public class HttpServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            System.out.println(request.uri());
            System.out.println(request.method());
            System.out.println(request.headers());
            //byte[] content = request.content().array();
            // 请求体
            String str = request.content().toString(CharsetUtil.UTF_8);
            // GET请求处理
            if (request.method().equals(HttpMethod.GET)){
                QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.content().toString(CharsetUtil.UTF_8));
                Map<String, List<String>> param = queryStringDecoder.parameters();
                System.out.println("get: " + param);
            }else if (request.method().equals(HttpMethod.POST)) { // post请求处理
                final boolean[] isForm = {false};
                HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);
                decoder.offer(request);
                decoder.getBodyHttpDatas().stream().forEach(s -> {
                    if (!isForm[0]) {
                        isForm[0] = true;
                    }
                    if (s.getHttpDataType().equals(InterfaceHttpData.HttpDataType.Attribute)) {
                        Attribute data = (Attribute) s;
                        try {
                            System.out.println(s.getName() + " " + data.getValue());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (s.getHttpDataType().equals(InterfaceHttpData.HttpDataType.FileUpload)) {
                        FileUpload fileUpload = (FileUpload) s;
                        // 文件上传
                        System.out.println("文件: "+fileUpload.getName());
                        System.out.println("长度: "+fileUpload.length());
                    }
                });
                if (!isForm[0]){
                    System.out.println("body: "+str);
                }
            }
        }
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(("啦啦啦"+System.currentTimeMillis()).getBytes()));
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

}
