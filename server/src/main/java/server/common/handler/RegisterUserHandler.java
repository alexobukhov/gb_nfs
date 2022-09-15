package server.common.handler;

import io.netty.channel.ChannelHandlerContext;
import ru.gb.file.warehouse.netty.common.dto.RegisterUserRequest;
import ru.gb.file.warehouse.netty.common.dto.RegisterUserResponse;

public class RegisterUserHandler implements RequestHandler<RegisterUserRequest, RegisterUserResponse> {

    @Override
    public RegisterUserResponse handle(RegisterUserRequest request, ChannelHandlerContext context) {
        //... логика регистрации
        return new RegisterUserResponse("OK");
    }
}
