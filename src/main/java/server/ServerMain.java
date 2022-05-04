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

public class ServerMain {
    //connection & timing
    private static final int SERVER_GAME_PORT = 5555;
    private static final int SERVER_CHAT_PORT = 5550;
    private static int PLAYER_COUNTER = 0;
    private static LocalTime START_TIME;
    private static final long WAIT_TIME_IN_SECOND = 60;       //TODO to decide
    private static final int SO_TIMEOUT = 10;       //TODO to decide (millisec)

    private static final int MAX_NUMBER_OF_PLAYERS = 2;     //TODO <link with Game's MAX_NUMBER_OF_PLAYERS field>

    private static List<GameServerClientHandler> gameClients = new ArrayList<>();
    private static List<ChatServerClientHandler> chatClients = new ArrayList<>();

    private static int poolSize = 4;
    private static ExecutorService gamePool = Executors.newFixedThreadPool(poolSize);
    private static ExecutorService chatPool = Executors.newFixedThreadPool(poolSize);

    //*******************************************
    public static void main(String[] args) {
        ServerSocket serverSocketGame = null;
        ServerSocket serverSocketChat = null;
        try {
            serverSocketGame = new ServerSocket(SERVER_GAME_PORT);
            serverSocketGame.setSoTimeout(SO_TIMEOUT * 1_000);

            if (Objects.nonNull(serverSocketGame)){
                serverSocketChat = new ServerSocket(SERVER_CHAT_PORT);
                serverSocketChat.setSoTimeout(SO_TIMEOUT * 1_000);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        START_TIME = LocalTime.now();


        while (checkWaitTime() && !isMaxNumberOfPlayersReached()) {             //**
            System.out.println("[SERVER]: waiting for client (game + chat) connection...");

            Socket gameClient = null;
            Socket chatClient = null;
            try {
                if ((gameClients.size() <= chatClients.size())) {
                    System.out.println("[SERVER]: Attempts to connect to gameClient, gameClients connected=" + gameClients.size());
                    gameClient = serverSocketGame.accept();

                    GameServerClientHandler gameServerClientHandler = new GameServerClientHandler(gameClient, gameClients);
                    gameClients.add(gameServerClientHandler);
                    System.out.println("[SERVER]: Connected to gameClient, gameClients connected=" + gameClients.size());
                }

                if ((gameClients.size() > chatClients.size())){
                    System.out.println("[SERVER]: Attempts to connect to chatClient, chatClients connected=" + chatClients.size());
                    chatClient = serverSocketChat.accept();

                    ChatServerClientHandler chatServerClientHandler = new ChatServerClientHandler(chatClient, chatClients);
                    chatClients.add(chatServerClientHandler);
                    System.out.println("[SERVER]: Connected to chatClient, chatClients connected=" + chatClients.size());
                }
/*
                if (gameClients.size() != chatClients.size()){
                    System.out.println("[SERVER]: gameClients and chatClients size differ! gameClients=" +gameClients.size() + ", chatClients=" + chatClients.size());
                    //TODO Throw SocketException vmi
                }
*/


        //creates oos ois
                System.out.println("[SERVER]: Attempts to create gameServerClientHandler");
                //GameServerClientHandler gameServerClientHandler = new GameServerClientHandler(gameClient, gameClients);
                //ChatServerClientHandler chatServerClientHandler = new ChatServerClientHandler(chatClient, chatClients);

                //String playerName = (String) gameServerClientHandler.getObjectInputStream().readUnshared();

                String playerName = (String) gameClients.get(gameClients.size()-1).getObjectInputStream().readUnshared();
                System.out.println("[SERVER]: playerName received from gameServerClientHandler, playerName=" + playerName);

                if (Objects.isNull(playerName)){
                    playerName = "Player " + String.valueOf(gameClients.size());
                    System.out.println("[SERVER]: playerName changed to playerName=" + playerName);
                }
                //gameServerClientHandler.setPlayerName(playerName);
                //chatServerClientHandler.setPlayerName(playerName);

                gameClients.get(gameClients.size()-1).setPlayerName(playerName);
                if (gameClients.size() == chatClients.size()) {
                    chatClients.get(chatClients.size() - 1).setPlayerName(playerName);
                    System.out.println("[SERVER]: gameClients and chatClients sizes are equal, playerName=" + playerName);
                }



                //gameClients.add(gameServerClientHandler);
                //System.out.println("[SERVER]: gameServerClientHandler added to list");
                //chatClients.add(chatServerClientHandler);
                //System.out.println("[SERVER]: chatServerClientHandler added to list");

                //gamePool.execute(gameServerClientHandler);
                gamePool.execute(gameClients.get(gameClients.size()-1));
                System.out.println("[SERVER]: start processing gamePool threads with executorService" );
                //chatPool.execute(chatServerClientHandler);
                chatPool.execute(chatClients.get(chatClients.size() - 1));
                System.out.println("[SERVER]: start processing chatPool threads with executorService" );

            } catch (SocketException se) {
                System.out.println("SERVER]: No successful connection this time SE");
                se.printStackTrace();
            } catch (IOException e) {
                System.out.println("SERVER]: No successful connection this time IOE");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        System.out.println("[SERVER]: waiting time expired or all slots are filled, clients connected=" + gameClients.size());

        //System.out.println("[SERVER]: start processing threads with executorService" );
        //gameClients.forEach(client -> gamePool.execute(client));

        System.out.println("SERVER]: === Move to GameHandler ===");
        GameHandler gameHandler = new GameHandler(gameClients);     //chatClients
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
        return gameClients.size() >= MAX_NUMBER_OF_PLAYERS || chatClients.size() >= MAX_NUMBER_OF_PLAYERS;
    }

}
