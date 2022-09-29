package ru.gb.nfs.server.common.handler;

import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Value;
import ru.gb.nfs.server.common.dto.UploadFileRequest;
import ru.gb.nfs.server.common.dto.UploadFileResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class UploadFileHandler implements RequestHandler<UploadFileRequest, UploadFileResponse> {

    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public UploadFileResponse handle(UploadFileRequest request, ChannelHandlerContext context) {
        String fileName = request.getFileName();
        byte[] filePartData = request.getFilePartData();

        Path newFilePath = Paths.get(uploadPath + fileName);
        try {
            Files.write(newFilePath, filePartData, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            return new UploadFileResponse("Не удалось сохранить файл на сервере");
        }
        return new UploadFileResponse("OK");
    }
}
