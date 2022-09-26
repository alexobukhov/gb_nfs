package ru.gb.nfs.server.common.handler;

import io.netty.channel.ChannelHandlerContext;
import ru.gb.nfs.server.common.dto.BasicRequest;
import ru.gb.nfs.server.common.dto.BasicResponse;

public interface RequestHandler<REQUEST extends BasicRequest, RESPONSE extends BasicResponse> {

    RESPONSE handle(REQUEST request, ChannelHandlerContext channelHandlerContext);
}