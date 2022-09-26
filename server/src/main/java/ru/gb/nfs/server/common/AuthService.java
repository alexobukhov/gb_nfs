package ru.gb.nfs.server.common;

public class AuthService {

    public boolean auth(String token) {
        return "Bogdan:1234".equals(token);
    }
}
