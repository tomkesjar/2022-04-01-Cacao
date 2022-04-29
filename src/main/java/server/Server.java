package server;

import game.GameHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    //connection & timing
    private static final int SERVER_PORT = 5555;
    private static int PLAYER_COUNTER = 0;
    private static LocalTime START_TIME;
    private static final long WAIT_TIME_IN_SECOND = 120;       //TODO to decide
    private static final int SO_TIMEOUT = 5;       //TODO to decide (millisec)

    private static final int MAX_NUMBER_OF_PLAYERS = 2;     //TODO <link with Game's MAX_NUMBER_OF_PLAYERS field>

    private static List<ServerClientHandler> clients = new ArrayList<>();
    private static int poolSize = 4;
    private static ExecutorService pool = Executors.newFixedThreadPool(poolSize);

    //*******************************************
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            serverSocket.setSoTimeout(SO_TIMEOUT * 1_000);
        } catch (IOException e) {
            e.printStackTrace();
        }

        START_TIME = LocalTime.now();


        while (checkWaitTime() && !isMaxNumberOfPlayersReached()) {
            System.out.println("[SERVER]: waiting for client connection...");

            Socket client = null;
            try {
                client = serverSocket.accept();
                System.out.println("[SERVER]: Connected to client, clients connected=" + clients.size());


                ServerClientHandler serverClientHandler = new ServerClientHandler(client, clients);

                String playerName = (String) serverClientHandler.getObjectInputStream().readUnshared();
                System.out.println("[SERVER]: playerName=" + playerName);
                if (Objects.isNull(playerName)){
                    playerName = "Player " + String.valueOf(clients.size()+1);
                    System.out.println("[SERVER]: playerName changed to playerName=" + playerName);
                }

                serverClientHandler.setPlayerName(playerName);


                clients.add(serverClientHandler);
                System.out.println("[SERVER]: client added to list");
                pool.execute(serverClientHandler);

            } catch (SocketException se) {
                System.out.println("SERVER]: No successful connection this time SE");
                //se.printStackTrace();
            } catch (IOException e) {
                System.out.println("SERVER]: No successful connection this time IOE");
                //e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        System.out.println("[SERVER]: waiting time expired or all slots are filled, clients connected=" + clients.size());

        GameHandler gameHandler = new GameHandler(clients);
        try {
            gameHandler.process();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static boolean checkWaitTime() {
        LocalTime currentTime = LocalTime.now();
        long timeSpent = ChronoUnit.SECONDS.between(START_TIME, currentTime);
        return (timeSpent < WAIT_TIME_IN_SECOND) ? true : false;
    }

    private static boolean isMaxNumberOfPlayersReached(){
        return clients.size() >= MAX_NUMBER_OF_PLAYERS;
    }

}
