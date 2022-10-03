package ru.gb.nfs.server.common.handler;

import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Service;
import ru.gb.nfs.server.common.dto.GetFilesListRequest;
import ru.gb.nfs.server.common.dto.GetFilesListResponse;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

@Service
public class GetFilesListHandler implements RequestHandler<GetFilesListRequest, GetFilesListResponse> {

    @Override
    public GetFilesListResponse handle(GetFilesListRequest request, ChannelHandlerContext context) {
        String getFilesListRequestPath = request.getPath();
        Path path = Paths.get(getFilesListRequestPath);
        String[] list = path.toFile().list();
        return new GetFilesListResponse("OK", list != null ? List.of(list) : Collections.emptyList());
    }

}
