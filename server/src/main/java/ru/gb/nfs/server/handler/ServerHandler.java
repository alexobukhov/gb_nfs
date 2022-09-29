package ru.gb.nfs.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gb.nfs.server.authentication.DBAuthenticationService;
import ru.gb.nfs.server.common.dto.AuthRequest;
import ru.gb.nfs.server.common.dto.BasicRequest;
import ru.gb.nfs.server.common.dto.BasicResponse;
import ru.gb.nfs.server.common.handler.HandlerRegistry;
import ru.gb.nfs.server.common.handler.RequestHandler;

@Service
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    DBAuthenticationService dbAuthenticationService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println(ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) {
        BasicRequest request = (BasicRequest) msg;
        if (msg instanceof BasicRequest) {
            AuthRequest authRequest = (AuthRequest) msg;
            String username = dbAuthenticationService.getUsernameByLoginAndPassword(authRequest.getLogin(),
                    authRequest.getPassword());
            if (username == null) {
                BasicResponse authErrorResponse = new BasicResponse("Not authenticated!");
                channelHandlerContext.writeAndFlush(authErrorResponse);
            }
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
