
import connection.ClientConnection;
import gui.GuiStartPage;
import messages.ActionMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 5555;



    public static void main(String[] args) throws IOException {
        //ClientConnection connection = new ClientConnection();
        GuiStartPage guiStartPage = new GuiStartPage();

    }

/*
    private static void establishConnection(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
        try {
            System.out.println("[Client]: Successful connection to server.Server");
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            System.out.println("[Client]: Input/OutputStreams successfully created");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void closeConnection(Socket socket) {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
*/

}
