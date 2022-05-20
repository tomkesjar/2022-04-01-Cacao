package server.handlers;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ChatServerClientHandler implements  Runnable {
    private Socket socket;
    private List<ChatServerClientHandler> clients;
    //private ObjectOutputStream objectOutputStream;
    //private ObjectInputStream objectInputStream;


    private PrintWriter printWriter;
    private BufferedReader bufferedReader;

    String inputLine;
    String outputLine;

    private String playerName;



    public ChatServerClientHandler(Socket socket, List<ChatServerClientHandler> clients) {
        System.out.println("[SERVER_CLIENT_HANDLER]: server.handlers.ChatServerClientHandler created");
        this.socket = socket;
        this.clients = clients;
        try {
            this.printWriter = new PrintWriter(this.socket.getOutputStream(), true);
            this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            System.out.println("[SERVER_CLIENT_HANDLER]: printwriter and bufferedreader created");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
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
        */

        public PrintWriter getPrintWriter() {
            return printWriter;
        }

        public void setPrintWriter(PrintWriter printWriter) {
            this.printWriter = printWriter;
        }

        public BufferedReader getBufferedReader() {
            return bufferedReader;
        }

        public void setBufferedReader(BufferedReader bufferedReader) {
            this.bufferedReader = bufferedReader;
        }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    private void sendToAll(String message){
        for (ChatServerClientHandler client : clients){
            client.getPrintWriter().println(message);
        }
        System.out.println("[SERVER_CLIENT_HANDLER]: chat message sent to all, message=" + message);
    }



    @Override
    public void run() {

        System.out.println("[SERVER_CLIENT_HANDLER]: chat listener run method started for player=" + this.getPlayerName());
        try {
            System.out.println("[SERVER_CLIENT_HANDLER]: attempts to send Entered the Game message by player=" + this.getPlayerName());

            sendToAll("[" + this.getPlayerName() + "]: " + " Just entered the Game!");

            while ((inputLine = bufferedReader.readLine()) != null){
                sendToAll("[" + this.getPlayerName() + "]:" + inputLine);
                System.out.println("[ChatServerClientHandler]: message sent to everyone=" + inputLine);
            }

        } catch (IOException e) {
            clients.remove(this);
            sendToAll("[" + this.getPlayerName() + "]: just left the Game!");
            System.out.println("[ChatServerClientHandler]: " + this.getPlayerName() +" left the common.game");
            e.printStackTrace();
        }


        //try{    ide kellene a common.game reakciokat betenni    }
    }


}

