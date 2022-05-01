package connection;

import game.Game;
import messages.ActionMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientConnection {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 5555;

    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;




    public ClientConnection() throws IOException, ClassNotFoundException {
        try {
            this.socket = new Socket(SERVER_IP, SERVER_PORT);
            //System.out.println("[ClientConnection]: Successful connection to server");
            this.objectInputStream = new ObjectInputStream(this.socket.getInputStream());
            //System.out.println("[ClientConnection]: InputStream successfully created");
            this.objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
            //System.out.println("[ClientConnection]: OutputStreams successfully created");
            System.out.println("[ClientConnection]: Connection, InputStream and OutputStreams successfully created");

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
