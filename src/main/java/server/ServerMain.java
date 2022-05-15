package server;

import game.GameHandler;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ServerMain {
    //connection & timing
    private static final int SERVER_GAME_PORT = 5555;
    private static final int SERVER_CHAT_PORT = 5550;
    private static int PLAYER_COUNTER = 0;
    private static LocalTime START_TIME;
    private static long WAIT_TIME_IN_SECOND = 60;       //TODO to decide
    private static final int SO_TIMEOUT = 10;       //TODO to decide (sec)

    private static int MAX_NUMBER_OF_PLAYERS = 4;     //TODO <link with Game's MAX_NUMBER_OF_PLAYERS field>

    private static List<GameServerClientHandler> gameClients = new ArrayList<>();
    private static List<ChatServerClientHandler> chatClients = new ArrayList<>();

    private static int poolSize = 4;
    private static ExecutorService gamePool = Executors.newFixedThreadPool(poolSize);
    private static ExecutorService chatPool = Executors.newFixedThreadPool(poolSize);

    private static ServerGui serverGui;

    //*******************************************
    public static void main(String[] args) {
        serverGui = new ServerGui();

        synchronized (serverGui.getSendButton()){
            try{
                serverGui.getSendButton().wait();
            }catch (InterruptedException e){
                System.out.println("[SERVER]: Interrupted exception occured");
            }
        }


        WAIT_TIME_IN_SECOND = Integer.parseInt(serverGui.getWaitTimeInput().getText());
        serverGui.appendToTextArea("[Server]: wait time is set to " + WAIT_TIME_IN_SECOND + " sec");
        MAX_NUMBER_OF_PLAYERS = Integer.parseInt(serverGui.getNumberOfPlayersInput().getText());
        serverGui.appendToTextArea("[Server]: max number of players= " + MAX_NUMBER_OF_PLAYERS);



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

        Map<String, Integer> namesMap = new HashMap<>();

        while (checkWaitTime() && !isMaxNumberOfPlayersReached()) {             //**
            String messageToSend = "[SERVER]: waiting for client (game + chat) connection...";
            System.out.println(messageToSend);
            serverGui.appendToTextArea(messageToSend);

            Socket gameClient = null;
            Socket chatClient = null;
            try {
                if ((gameClients.size() <= chatClients.size())) {
                    messageToSend = "[SERVER]: Attempts to connect to gameClient, gameClients connected=" + gameClients.size();
                    System.out.println(messageToSend);
                    serverGui.appendToTextArea(messageToSend);
                    gameClient = serverSocketGame.accept();

                    GameServerClientHandler gameServerClientHandler = new GameServerClientHandler(gameClient, gameClients);
                    gameClients.add(gameServerClientHandler);
                    messageToSend = "[SERVER]: Connected to gameClient, gameClients connected=" + gameClients.size();
                    System.out.println(messageToSend);
                    serverGui.appendToTextArea(messageToSend);
                }

                if ((gameClients.size() > chatClients.size())){
                    messageToSend = "[SERVER]: Attempts to connect to chatClient, chatClients connected=" + chatClients.size();
                    System.out.println(messageToSend);
                    serverGui.appendToTextArea(messageToSend);
                    chatClient = serverSocketChat.accept();

                    ChatServerClientHandler chatServerClientHandler = new ChatServerClientHandler(chatClient, chatClients);
                    chatClients.add(chatServerClientHandler);

                    messageToSend = "[SERVER]: Connected to chatClient, chatClients connected=" + chatClients.size();
                    System.out.println(messageToSend);
                    serverGui.appendToTextArea(messageToSend);
                }


        //creates oos ois
                messageToSend = "[SERVER]: Attempts to create gameServerClientHandler";
                System.out.println(messageToSend);
                serverGui.appendToTextArea(messageToSend);

                String playerName = (String) gameClients.get(gameClients.size()-1).getObjectInputStream().readUnshared();
                System.out.println("[SERVER]: playerName received from gameServerClientHandler, playerName=" + playerName);

                if (Objects.isNull(playerName)){
                    int index = 0;
                    playerName = "Player " + String.valueOf(gameClients.size());
                    System.out.println("[SERVER]: playerName changed to playerName=" + playerName);

                    if (!namesMap.containsKey("Player")) {
                        namesMap.put("Player", 1);
                    } else {
                        namesMap.put("Player", namesMap.get("Player") + 1);
                    }

                }else if(namesMap.containsKey(playerName)) {
                    namesMap.put(playerName, namesMap.get(playerName)+1);
                    playerName = playerName + String.valueOf(namesMap.get(playerName));
                } else {
                    namesMap.put(playerName, 1);
                }

                gameClients.get(gameClients.size()-1).setPlayerName(playerName);
                if (gameClients.size() == chatClients.size()) {
                    chatClients.get(chatClients.size() - 1).setPlayerName(playerName);
                    System.out.println("[SERVER]: gameClients and chatClients sizes are equal, playerName=" + playerName);
                }


                gamePool.execute(gameClients.get(gameClients.size()-1));
                messageToSend = "[SERVER]: start processing gamePool threads with executorService";
                System.out.println(messageToSend);
                serverGui.appendToTextArea(messageToSend);

                chatPool.execute(chatClients.get(chatClients.size() - 1));
                messageToSend = "[SERVER]: start processing chatPool threads with executorService";
                System.out.println(messageToSend);
                serverGui.appendToTextArea(messageToSend);

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
        String messageToSend = "[SERVER]: waiting time expired or all slots are filled, clients connected=" + gameClients.size();
        System.out.println(messageToSend);
        serverGui.appendToTextArea(messageToSend);

        if (gameClients.isEmpty() || chatClients.isEmpty()) {
            messageToSend = "[SERVER]: Nobody joined, closing server.  gameClients=" + gameClients.size() + ", chatClients=" + gameClients.size();
            System.out.println(messageToSend);
            serverGui.appendToTextArea(messageToSend);
            JOptionPane.showMessageDialog(new Frame(), "Nobody joined, closing server.  gameClients=" + gameClients.size() + ", chatClients=" + gameClients.size(), "Error", JOptionPane.ERROR_MESSAGE);
            serverGui.dispose();
            return;
        }


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
