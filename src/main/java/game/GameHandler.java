package game;


import messages.ResponseStatus;
import messages.TilePlacementMessageRequest;
import messages.TilePlacementMessageResponse;
import players.Player;
import server.GameServerClientHandler;
import tiles.AbstractTile;
import tiles.JungleTile;
import tiles.WorkerTile;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class GameHandler {
    private volatile Game game;     //TODO lehet nem kell a volatile
    private List<GameServerClientHandler> clients;

    private boolean isWorkerTilePlacementValid = false;
    private boolean isJungleTilePlacementValid = false;

    JungleTile jungleTile;
    WorkerTile workerTile;

    public JungleTile getJungleTile() {
        return jungleTile;
    }

    public void setJungleTile(JungleTile jungleTile) {
        this.jungleTile = jungleTile;
    }

    public WorkerTile getWorkerTile() {
        return workerTile;
    }

    public void setWorkerTile(WorkerTile workerTile) {
        this.workerTile = workerTile;
    }

    public GameHandler(List<GameServerClientHandler> gameClients) {
        this.clients = gameClients;             //for sending/receiving objects (game)
        this.game = new Game(gameClients);      //only the gameClients.size() matters
        System.out.println("[GameHandler]: Game instance created with players=" + gameClients.size());
    }

    public GameHandler(String playerName, int numberOfBot) {
        List<String> nameList = new ArrayList<String>(Arrays.asList(playerName));
        for (int i=0; i<numberOfBot; ++i){
            String botName = "Bot " + String.valueOf(i+1);
            nameList.add(botName);
        }
        this.game = new Game(nameList, 1);      //only the gameClients.size() matters
    }


    public Game getGame() {
        return game;
    }

    public void process() throws IOException, ClassNotFoundException {
        game.getBoard().selectPossibleWorkerAndJungleTilesForPlacement();

        sendStartingGameInstanceToPlayers();



        while (!game.isGameEnded()) {
            System.out.println("[GameHandler]: starting the loop, checkIfIsGameEnd=" +game.checkIfIsGameEnd());

            System.out.println("[GameHandler]: Moving to WorkerTile placement for player=" +game.getActivePlayer());
            while (!isWorkerTilePlacementValid) {
                //* extract from here
                GameServerClientHandler currentClient = clients.get(game.getActivePlayer());

                //* extract communicator.getMessage (Type argument) return point, abstractTile pair
                TilePlacementMessageRequest tilePlacementMessageRequest = (TilePlacementMessageRequest) currentClient.getObjectInputStream().readUnshared();
                Point workerCoordExtracted = tilePlacementMessageRequest.getCoord();
                AbstractTile workerTileExtracted =  (WorkerTile) tilePlacementMessageRequest.getTile();
                //* end extract getMessage

                if (game.getBoard().isValidPlacementAsWorkerTile(workerCoordExtracted)) {
                    System.out.println("[GameHandler]: Valid WorkerTile Placement for player=" +game.getActivePlayer() + " tilePlacementMessageRequest=" + tilePlacementMessageRequest.toString());

                    //place tile
                    Point coord = tilePlacementMessageRequest.getCoord();
                    WorkerTile workerTileToPlace =  (WorkerTile) tilePlacementMessageRequest.getTile();        //EZT BE KELL ALLITANI
                    placeAndEvaluateWorkerTile(coord, workerTileToPlace);

                    //** extract method manageDeck
                    int activePlayerIndex = game.getActivePlayer();
                    Player activePlayer = game.getPlayerList().get(activePlayerIndex);
                    manageWorkerTileDeck(activePlayer);
                    //* end of extract manageDeck

                    //extract method communicator.sendMessage (ebbol a game + textMessage kell a single-be
                    TilePlacementMessageResponse response = new TilePlacementMessageResponse(game, ResponseStatus.SUCCESSFUL, "worker tile placement successful, now select and place jungle tile");
                    sendMessageToAll(response);
                    // end of extract sendMessage

                    System.out.println("[GameHandler]: successful response sent to all player after WORKER placement."/* response=" + response*/);
                    //extract until here
                } else {
                    //extract method communicator.sendMessage (ebbol a game + textMessage kell a single-be
                    System.out.println("[GameHandler]: Invalid WorkerTile Placement for player=" +game.getActivePlayer() + " , tilePlacementMessageRequest=" + tilePlacementMessageRequest.toString());
                    TilePlacementMessageResponse response = new TilePlacementMessageResponse(game, ResponseStatus.FAILED, "Invalid placement, select an empty tile and place worker tile adjacent to a jungle tile");
                    sendMessageToPlayer(response, currentClient);
                    // end of extract sendMessage
                }
            }




            System.out.println("[GameHandler]: Moving to JungleTile placement, player=" + game.getActivePlayer());
            while (!isJungleTilePlacementValid) {
                //* extract from here
                GameServerClientHandler currentClient = clients.get(game.getActivePlayer());
                System.out.println("[GameHandler]: clients=" + clients);

                //extract method communicator.getMessage
                TilePlacementMessageRequest tilePlacementMessageRequest = (TilePlacementMessageRequest) clients.get(game.getActivePlayer()).getObjectInputStream().readUnshared();
                //System.out.println("[GameHandler]: TilePlacement message received from player=" + game.getActivePlayer() + " , tilePlacementMessageRequest=" + tilePlacementMessageRequest.toString());
                Point jungleCoordExtracted = tilePlacementMessageRequest.getCoord();
                AbstractTile jungleTileExtracted =  (JungleTile) tilePlacementMessageRequest.getTile();
                //* end extract getMessage

                if (game.getBoard().isValidPlacementAsJungleTile(jungleCoordExtracted)) {
                    System.out.println("[GameHandler]: Valid JungleTile Placement for player=" +game.getActivePlayer() + " , tilePlacementMessageRequest=" + tilePlacementMessageRequest.toString());

                    Point coord = tilePlacementMessageRequest.getCoord();
                    JungleTile jungleTileToPlace = (JungleTile) tilePlacementMessageRequest.getTile();
                    placeAndEvaluateJungleTile(coord, jungleTileToPlace);


                    //* extract method manageJungleDeck  update + manage jungle tile deck
                    manageJungleTileDeck();
                    //* end of manageJungleDeck
                } else {
                    //extract method communicator.sendMessage (ebbol a game + textMessage kell a single-be
                    System.out.println("[GameHandler]: Invalid JungleTile Placement for player=" +game.getActivePlayer() + " , tilePlacementMessageRequest=" + tilePlacementMessageRequest.toString());
                    TilePlacementMessageResponse response = new TilePlacementMessageResponse(game, ResponseStatus.FAILED, "Invalid placement, select an empty tile and place jungle tile adjacent to any worker tile");
                    sendMessageToPlayer(response, currentClient);
                    // end of extract sendMessage
                }
            }




            System.out.println("[GameHandler]: values Before switch, isGameEnded=" + game.isGameEnded() + ", activePlayer=" + game.getActivePlayer() + "status: workerPlacement=" + game.hasPlacedWorkerTile() + ", junglePlacement=" + game.hasPlacedJungleTile());

            switchPlayer();
            checkEndGameStatus();
            ResponseStatus messageStatus = game.isGameEnded() ? ResponseStatus.FINAL : ResponseStatus.SUCCESSFUL;

            System.out.println("[GameHandler]: =================================================================SWITCH=");
            System.out.println("[GameHandler]: values After switched player, isGameEnded=" + game.isGameEnded() + ", activePlayer=" + game.getActivePlayer() + "status: workerPlacement=" + game.hasPlacedWorkerTile() + ", junglePlacement=" + game.hasPlacedJungleTile());

            //extract method communicator.sendMessage (ebbol a game + textMessage kell a single-be
            TilePlacementMessageResponse response = new TilePlacementMessageResponse(game, messageStatus, "'s turn, first select and place worker tile (Other players are inactive)");
            sendMessageToAll(response);
            System.out.println("[GameHandler]: successful response sent to all player after JUNGLE placement");
            // end of extract sendMessage
        }







        //TODO
            //send rank
        TilePlacementMessageResponse finalMessage = new TilePlacementMessageResponse(game, ResponseStatus.FINAL, "Game End)");
            sendMessageToAll(finalMessage);
        System.out.println("[GameHandler]: final game status sent to all players");
        System.out.println("[GameHandler]: Game Ended");
    }

    public void checkEndGameStatus() {
        game.setGameEnded(game.checkIfIsGameEnd());
    }

    public void placeAndEvaluateJungleTile(Point coord, JungleTile jungleTileToPlace) {
        this.setJungleTile(jungleTileToPlace);
        game.getBoard().setField(coord.x, coord.y, jungleTile);
        isJungleTilePlacementValid = true;
        game.getBoard().setFreshJungleTile(jungleTile);
        game.getBoard().setFreshJungleTilePoint(coord);
        game.setHasPlacedJungleTile(true);
        game.setHasPlacedWorkerTile(false);


        JungleTile currentTile = (JungleTile) game.getBoard().getField(coord.x, coord.y);
        currentTile.processNeighbours(coord, game);
        game.calculatePoints();
        game.calculateRanks();

        game.getBoard().selectPossibleWorkerAndJungleTilesForPlacement();
    }

    public void placeAndEvaluateWorkerTile(Point coord, WorkerTile workerTileToPlace) {
        this.setWorkerTile(workerTileToPlace);
        game.getBoard().setField(coord.x, coord.y, workerTile);
        isWorkerTilePlacementValid = true;
        game.getBoard().setFreshWorkerTile(workerTile);
        game.getBoard().setFreshWorkerTilePoint(coord);
        game.setHasPlacedWorkerTile(true);

        WorkerTile currentTile = (WorkerTile) game.getBoard().getField(coord.x, coord.y);
        currentTile.processNeighbours(coord, game);
        game.calculatePoints();
        game.calculateRanks();

        game.getBoard().selectPossibleWorkerAndJungleTilesForPlacement();
    }

    public void manageJungleTileDeck() {
        Optional<JungleTile> matchingJungleTile = game.getJungleTilesAvailable().stream().filter(tile -> jungleTile.equals(tile)).findFirst();      //TODO SOS CHECK!!!
        if (matchingJungleTile.isPresent()) {
            game.getJungleTilesAvailable().remove(matchingJungleTile.get());
        }
        Optional<JungleTile> drawnCard = game.getJungleTileDeck().drawCard();
        if (drawnCard.isPresent() &&  game.getJungleTilesAvailable().size() < Game.getMaxNumberOfJungleTilesAvailable()) {
            game.getJungleTilesAvailable().add(drawnCard.get());
        }
    }

    public void manageWorkerTileDeck(Player activePlayer) {
        Optional<WorkerTile> matchingWorkerTile = activePlayer.getCardsAtHand().stream().filter(tile -> workerTile.equals(tile)).findFirst();
        if (matchingWorkerTile.isPresent()){
            activePlayer.getCardsAtHand().remove(matchingWorkerTile.get());
        } else {
            System.out.println("[GameHandler]: ERROR!! matchingWorkerTile was not found");
        }

        Optional<WorkerTile> optionalWorkerTile = activePlayer.getWorkerTileDeck().drawCard();
        if (optionalWorkerTile.isPresent() && activePlayer.getCardsAtHand().size() < Player.getMaxNumberOfCardsAtHand()) {
            activePlayer.getCardsAtHand().add(optionalWorkerTile.get());
        }
    }

    public boolean isWorkerTilePlacementValid() {
        return isWorkerTilePlacementValid;
    }

    public void setWorkerTilePlacementValid(boolean workerTilePlacementValid) {
        isWorkerTilePlacementValid = workerTilePlacementValid;
    }

    public boolean isJungleTilePlacementValid() {
        return isJungleTilePlacementValid;
    }

    public void setJungleTilePlacementValid(boolean jungleTilePlacementValid) {
        isJungleTilePlacementValid = jungleTilePlacementValid;
    }

    private void sendStartingGameInstanceToPlayers() {
        List<Integer> playerIndex = Arrays.asList(0);
        clients.forEach(c -> {
            try {
                c.getObjectOutputStream().writeUnshared(game);
                c.getObjectOutputStream().writeUnshared(playerIndex.get(0));
                playerIndex.set(0, playerIndex.get(0) + 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        System.out.println("[GameHandler]: Starting game instance and player indices sent to players");
    }

    public void switchPlayer() {
        game.setHasPlacedWorkerTile(false);
        game.setHasPlacedJungleTile(false);
        isWorkerTilePlacementValid = false;
        isJungleTilePlacementValid = false;
        game.callNextPlayer();
    }


    private void sendMessageToPlayer(Object message, GameServerClientHandler client) {
        try {
            client.getObjectOutputStream().reset();
            client.getObjectOutputStream().writeUnshared(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("[GameHandler]: Message sent to Player " + (int) (clients.indexOf(client) + 1) + "; message=" + message.toString());
    }

    private void sendMessageToAll(Object message) {
        clients.forEach(c -> {
            try {
                c.getObjectOutputStream().reset();
                c.getObjectOutputStream().writeUnshared(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        System.out.println("[GameHandler]: Message sent to all players, message=" + message.toString());
    }

}
