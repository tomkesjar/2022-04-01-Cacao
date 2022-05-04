package connection;

import game.Game;
import messages.ActionMessage;

import java.io.*;
import java.net.Socket;

public class ClientConnection {
    public enum ConnectionType {
        OBJECT,
        TEXT
    }

    private static final String SERVER_IP = "127.0.0.1";

    private static final int SERVER_GAME_PORT = 5555;
    private static final int SERVER_CHAT_PORT = 5550;

    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    private BufferedReader bufferedReader;
    private PrintWriter printWriter;

    private ConnectionType type;




    public ClientConnection(ConnectionType type) throws IOException, ClassNotFoundException {
        try {
            this.type = type;

            if (this.type == ConnectionType.OBJECT) {
                this.socket = new Socket(SERVER_IP, SERVER_GAME_PORT);
                System.out.println("[ClientConnection]: Successful connection to game server");
                this.objectInputStream = new ObjectInputStream(this.socket.getInputStream());
                System.out.println("[ClientConnection]: InputStream successfully created");
                this.objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
                System.out.println("[ClientConnection]: OutputStreams successfully created");
            }
            else {
                this.socket = new Socket(SERVER_IP, SERVER_CHAT_PORT);
                System.out.println("[ClientConnection]: Successful connection to chat server");
                this.printWriter = new PrintWriter(this.socket.getOutputStream(), true);
                System.out.println("[ClientConnection]: PrintWriter successfully created");
                this.bufferedReader =  new BufferedReader(new InputStreamReader(this.socket.getInputStream()));;;
                System.out.println("[ClientConnection]: BufferedReader successfully created");

            }
            System.out.println("[ClientConnection]: Connection, InputStream and OutputStream are successfully created");

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public Socket getSocket() {
        return socket;
    }

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    public ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }

    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public PrintWriter getPrintWriter() {
        return printWriter;
    }

    public Object readObject(){
        try {
            return objectInputStream.readUnshared();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
