package server.handlers;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class GameServerClientHandler implements  Runnable {
    private Socket socket;
    private List<GameServerClientHandler> clients;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;


    private String playerName;



    public GameServerClientHandler(Socket socket, List<GameServerClientHandler> clients) {
        System.out.println("[SERVER_CLIENT_HANDLER]: server.handlers.GameServerClientHandler created");
        this.socket = socket;
        this.clients = clients;
        try {
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            System.out.println("[SERVER_CLIENT_HANDLER]: ois and oos created");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    public void setObjectOutputStream(ObjectOutputStream objectOutputStream) {
        this.objectOutputStream = objectOutputStream;
    }

    public ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }

    public void setObjectInputStream(ObjectInputStream objectInputStream) {
        this.objectInputStream = objectInputStream;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }



    @Override
    public void run() {
    }


}
