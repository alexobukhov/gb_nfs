package server.common.handler;

import io.netty.channel.ChannelHandlerContext;
import server.common.dto.BasicRequest;
import server.common.dto.BasicResponse;

public interface RequestHandler<REQUEST extends BasicRequest, RESPONSE extends BasicResponse> {

    RESPONSE handle(REQUEST request, ChannelHandlerContext channelHandlerContext);
}