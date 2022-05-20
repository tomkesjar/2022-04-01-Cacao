package server.handlers;

import java.io.*;
import java.net.Socket;
import java.util.List;

//ez kell, hogy implements Runnable legyen, maskulonben megszakad a client.connection a client-tel
public class GameServerClientHandler implements  Runnable {
    private Socket socket;
    private List<GameServerClientHandler> clients;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    /*
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;

    String inputLine;
    String outputLine;
*/
    private String playerName;



    public GameServerClientHandler(Socket socket, List<GameServerClientHandler> clients) {
        System.out.println("[SERVER_CLIENT_HANDLER]: server.handlers.GameServerClientHandler created");
        this.socket = socket;
        this.clients = clients;
        try {
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            System.out.println("[SERVER_CLIENT_HANDLER]: ois and oos created");
/*
            this.printWriter = new PrintWriter(socket.getOutputStream(), true);
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("[SERVER_CLIENT_HANDLER]: printwriter and bufferedreader created");
 */
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
/*
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
*/
    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
/*
    private void sendToAll(String message){
        for (GameServerClientHandler client : clients){
            client.getPrintWriter().println(message);
        }
    }
*/


    @Override
    public void run() {
        /*
        System.out.println("[SERVER_CLIENT_HANDLER]: run method started");
        try {
            System.out.println("[SERVER_CLIENT_HANDLER]: attempts to send Entered the Game message");

            sendToAll("[" + this.getPlayerName() + "]: " + " Just entered the Game!");

            while ((inputLine = bufferedReader.readLine()) != null){
                sendToAll("[" + this.getPlayerName() + "]:" + inputLine);
                System.out.println("[GameServerClientHandler]: message sent to everyone=" + inputLine);
            }

        } catch (IOException e) {
            clients.remove(this);
            sendToAll("[" + this.getPlayerName() + "]: just left the Game!");
            System.out.println("[GameServerClientHandler]: " + this.getPlayerName() +" left the common.game");
            e.printStackTrace();
        }
         */

        //try{    ide kellene a common.game reakciokat betenni    }
    }


}
