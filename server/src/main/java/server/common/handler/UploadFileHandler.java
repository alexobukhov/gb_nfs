package server.common.handler;

import io.netty.channel.ChannelHandlerContext;
import ru.gb.file.warehouse.netty.common.dto.UploadFileRequest;
import ru.gb.file.warehouse.netty.common.dto.UploadFileResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class UploadFileHandler implements RequestHandler<UploadFileRequest, UploadFileResponse> {

    private static final String SERVER_PATH = "/Users/bchervoniy/IdeaProjects/file-warehouse/server-dir/";

    @Override
    public UploadFileResponse handle(UploadFileRequest request, ChannelHandlerContext context) {
        String fileName = request.getFileName();
        byte[] filePartData = request.getFilePartData();

        Path newFilePath = Paths.get(SERVER_PATH + fileName);
        try {
            Files.write(newFilePath, filePartData, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            return new UploadFileResponse("Не удалось сохранить файл на сервере");
        }
        return new UploadFileResponse("OK");
    }
}
