package server.common.handler;

import io.netty.channel.ChannelHandlerContext;
import ru.gb.file.warehouse.netty.common.dto.GetFilesListRequest;
import ru.gb.file.warehouse.netty.common.dto.GetFilesListResponse;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class GetFilesListHandler implements RequestHandler<GetFilesListRequest, GetFilesListResponse> {

    @Override
    public GetFilesListResponse handle(GetFilesListRequest request, ChannelHandlerContext context) {
        String getFilesListRequestPath = request.getPath();
        Path path = Paths.get(getFilesListRequestPath);
        String[] list = path.toFile().list();
        return new GetFilesListResponse("OK", list != null ? List.of(list) : Collections.emptyList());
    }

}
