package client.connection;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class ClientConnection {
    public enum ConnectionType {
        OBJECT,
        TEXT
    }

    private static final int SERVER_GAME_PORT = 5555;
    private static final int SERVER_CHAT_PORT = 5550;

    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    private BufferedReader bufferedReader;
    private PrintWriter printWriter;

    private ConnectionType type;




    public ClientConnection(String hostName, ConnectionType type) throws IOException, ClassNotFoundException {
        try {
            this.type = type;

            if (this.type == ConnectionType.OBJECT) {
                this.socket = new Socket(hostName, SERVER_GAME_PORT);
                System.out.println("[ClientConnection]: Successful client.connection to common.game server");
                this.objectInputStream = new ObjectInputStream(this.socket.getInputStream());
                System.out.println("[ClientConnection]: InputStream successfully created");
                this.objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
                System.out.println("[ClientConnection]: OutputStreams successfully created");
            }
            else {
                this.socket = new Socket(hostName, SERVER_CHAT_PORT);
                System.out.println("[ClientConnection]: Successful client.connection to chat server");
                this.printWriter = new PrintWriter(this.socket.getOutputStream(), true);
                System.out.println("[ClientConnection]: PrintWriter successfully created");
                this.bufferedReader =  new BufferedReader(new InputStreamReader(this.socket.getInputStream()));;;
                System.out.println("[ClientConnection]: BufferedReader successfully created");

            }
            System.out.println("[ClientConnection]: Connection, InputStream and OutputStream are successfully created");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(new JFrame(), "An error occured during client.connection.", "Connection Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();

            System.exit(-1);
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
