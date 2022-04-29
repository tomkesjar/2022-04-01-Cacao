package server;

import messages.ActionMessage;

import java.io.*;
import java.net.Socket;
import java.util.List;

//ez kell, hogy implements Runnable legyen, maskulonben megszakad a connection a client-tel
public class ServerClientHandler implements  Runnable {
    private Socket socket;
    private List<ServerClientHandler> clients;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private String playerName;

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

    public ServerClientHandler(Socket socket, List<ServerClientHandler> clients) {
        System.out.println("[SERVER_CLIENT_HANDLER]: server.ServerClientHandler created");
        this.socket = socket;
        this.clients = clients;
        try {
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
/*
        try {
            //System.out.println("[SERVER_CLIENT_HANDLER]: waiting for incoming message");
            //ActionMessage connectionMessage = (ActionMessage) objectInputStream.readUnshared();
            //System.out.println();
            objectOutputStream.writeUnshared(new ActionMessage("you are player " + (clients.size()+1)));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
*/
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    @Override
    public void run() {
        //try{    ide kellene a game reakciokat betenni    }
    }


}
