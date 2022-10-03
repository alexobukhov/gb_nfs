package ru.gb.nfs.server.common.dto;

public class AuthRequest extends BasicRequest {

    private String login;

    private String password;

    public AuthRequest(String authToken, String login, String password) {
        super(authToken);
        this.login = login;
        this.password = password;
    }
}
