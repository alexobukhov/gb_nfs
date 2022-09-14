package server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import server.ObjectRegistry;
import server.common.AuthService;
import server.common.dto.BasicRequest;
import server.common.dto.BasicResponse;
import server.common.dto.RegisterUserRequest;
import server.common.handler.HandlerRegistry;
import server.common.handler.RequestHandler;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println(ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) {
        BasicRequest request = (BasicRequest) msg;
        AuthService authService = ObjectRegistry.getInstance(AuthService.class);
        String authToken = request.getAuthToken();
        if (!(request instanceof RegisterUserRequest) && !authService.auth(authToken)) {
            BasicResponse authErrorResponse = new BasicResponse("Not authenticated!");
            channelHandlerContext.writeAndFlush(authErrorResponse);
        }

        RequestHandler handler = HandlerRegistry.getHandler(request.getClass());
        BasicResponse response = handler.handle(request, channelHandlerContext);
        channelHandlerContext.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }

}
