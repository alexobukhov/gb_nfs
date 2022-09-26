package ru.gb.nfs.server.handler;

import ru.gb.nfs.server.MyServer;
import ru.gb.nfs.server.authentication.AuthenticationService;
import ru.gb.nfs.server.models.FileSent;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler {
    private static final String AUTH_CMD_PREFIX = "/auth"; // + login + password
    private static final String AUTHOK_CMD_PREFIX = "/authok"; // + username
    private static final String AUTHERR_CMD_PREFIX = "/autherr"; // + error message
    private static final String CLIENT_MSG_CMD_PREFIX = "/cMsg"; // + msg
    private static final String SERVER_MSG_CMD_PREFIX = "/sMsg"; // + msg
    private static final String PRIVATE_MSG_CMD_PREFIX = "/w"; // + msg
    private static final String STOP_SERVER_CMD_PREFIX = "/stop";
    private static final String END_CLIENT_CMD_PREFIX = "/end";
    private static final String GET_CLIENTS_CMD_PREFIX = "/gcMsg";

    private MyServer myServer;
    private Socket clientSocket;
    private DataOutputStream out;
    private DataInputStream in;
    private String username;

    private ObjectInputStream objectInputStream = null;

    private FileSent fileSent;

    private File destinationFile;

    private FileOutputStream fileOutputStream = null;

    public ClientHandler(MyServer myServer, Socket socket) {
        this.myServer = myServer;
        clientSocket = socket;
    }

    public void handleFile() {
        new Thread(() -> {
            try {
                objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
                downloadFile(objectInputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void handle() throws IOException {
        out = new DataOutputStream(clientSocket.getOutputStream());
        in = new DataInputStream(clientSocket.getInputStream());

        new Thread(() -> {
            try {
                authentication();
                readMessage();
            } catch (IOException e) {
                e.printStackTrace();
                myServer.unSubscribe(this);
                try {
                    myServer.broadcastClientDisconnected(this);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }).start();
    }

    public void downloadFile(ObjectInputStream objectInputStream) {
        while (true) {
            try {
                fileSent = (FileSent) objectInputStream.readObject();
                String outFile = "data/" + fileSent.getFileName();
                destinationFile = new File(outFile);
                fileOutputStream = new FileOutputStream(destinationFile);
                fileOutputStream.write(fileSent.getFileData());
                fileOutputStream.flush();
                fileOutputStream.close();
                System.out.println(outFile + " is saved");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void authentication() throws IOException {
        while (true) {
            String message = in.readUTF();
            if (message.startsWith(AUTH_CMD_PREFIX)) {
                boolean isSuccessAuth = processAuthentication(message);
                if (isSuccessAuth) {
                    break;
                }

            } else {
                out.writeUTF(AUTHERR_CMD_PREFIX + " Ошибка аутентификации");
                System.out.println("Неудачная попытка аутентификации");
            }
        }
    }

    private boolean processAuthentication(String message) throws IOException {
        String[] parts = message.split("\\s+");
        if (parts.length != 3) {
            out.writeUTF(AUTHERR_CMD_PREFIX + " Ошибка аутентификации");
        }
        String login = parts[1];
        String password = parts[2];

        AuthenticationService auth = myServer.getAuthenticationService();

        username = auth.getUsernameByLoginAndPassword(login, password);

        if (username != null) {
            if (myServer.isUsernameBusy(username)) {
                out.writeUTF(AUTHERR_CMD_PREFIX + " Логин уже используется");
                return false;
            }

            out.writeUTF(AUTHOK_CMD_PREFIX + " " + username);
            connectUser(username);
            return true;
        } else {
            out.writeUTF(AUTHERR_CMD_PREFIX + " Логин или пароль не соответствуют действительности");
            return false;
        }
    }

    private void connectUser(String username) throws IOException {
        myServer.subscribe(this);
        System.out.println("Пользователь " + username + " подключился к чату");
        myServer.broadcastClients(this);
    }

    private void readMessage() throws IOException {
        while (true) {
            String message = in.readUTF();
            System.out.println("message | " + username + ": " + message);
            if (message.startsWith(STOP_SERVER_CMD_PREFIX)) {
                System.exit(0);
            } else if (message.startsWith(END_CLIENT_CMD_PREFIX)) {
                return;
            } else if (message.startsWith(PRIVATE_MSG_CMD_PREFIX)) {
                myServer.sendPrivateMessage(getMessage(message), getRecipientName(message), this);
            } else {
                myServer.broadcastMessage(message, this);
            }

        }
    }

    public void sendMessage(String sender, String message) throws IOException {
        out.writeUTF(String.format("%s %s %s", CLIENT_MSG_CMD_PREFIX, sender, message));
    }

    public void sendServerMessage(String message) throws IOException {
        out.writeUTF(String.format("%s %s", SERVER_MSG_CMD_PREFIX, message));
    }

    public String getUsername() {
        return username;
    }

    public void sendClientsList(List<ClientHandler> clients) throws IOException {
        String message = String.format("%s %s", GET_CLIENTS_CMD_PREFIX, clients.toString());
        out.writeUTF(message);
        System.out.println(message);
    }

    public String getRecipientName(String message) {
        return message.split(" ", 2)[1];
    }

    public String getMessage(String message) {
        return message.split(" ", 2)[2];
    }
}
