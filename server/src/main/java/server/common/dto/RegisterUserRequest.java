package server.common.dto;

public class RegisterUserRequest extends BasicRequest {

    private final String login;

    public RegisterUserRequest(String login) {
        super("NOPE");
        this.login = login;
    }

    public String getLogin() {
        return login;
    }
}
