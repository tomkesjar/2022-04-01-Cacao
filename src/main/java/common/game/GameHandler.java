package common.game;


import common.messages.ResponseStatus;
import common.messages.TilePlacementMessageRequest;
import common.messages.TilePlacementMessageResponse;
import common.players.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.ServerGui;
import server.handlers.GameServerClientHandler;
import common.tiles.AbstractTile;
import common.tiles.JungleTile;
import common.tiles.WorkerTile;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class GameHandler {
    private volatile Game game;
    private List<GameServerClientHandler> clients;

    private boolean isWorkerTilePlacementValid = false;
    private boolean isJungleTilePlacementValid = false;

    private JungleTile jungleTile;
    private WorkerTile workerTile;

    private static Logger logger;

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
        this.clients = gameClients;
        this.game = new Game(gameClients);
        logger = (Logger) LogManager.getLogger(GameHandler.class);
        //System.out.println("[GameHandler]: Game instance created with common.players=" + gameClients.size());
        logger.info("[GameHandler]: Game instance created with common.players=" + gameClients.size());
    }

    public GameHandler(String playerName, int numberOfBot, Player.PlayerType playerType) {
        logger = (Logger) LogManager.getLogger(GameHandler.class);
        List<String> nameList = new ArrayList<String>(Arrays.asList(playerName));
        for (int i=0; i<numberOfBot; ++i){
            String botName = "Bot " + String.valueOf(i+1);
            nameList.add(botName);
        }
        this.game = new Game(nameList, 1, playerType);
    }


    public Game getGame() {
        return game;
    }

    public void process() throws IOException, ClassNotFoundException {
        game.getBoard().selectPossibleWorkerAndJungleTilesForPlacement();

        sendStartingGameInstanceToPlayers();



        while (!game.isGameEnded()) {
            //System.out.println("[GameHandler]: starting the loop, checkIfIsGameEnd=" +game.checkIfIsGameEnd());
            logger.info("[GameHandler]: starting the loop, checkIfIsGameEnd=" +game.checkIfIsGameEnd());
            //System.out.println("[GameHandler]: Moving to WorkerTile placement for player=" +game.getActivePlayer());
            logger.info("[GameHandler]: Moving to WorkerTile placement for player=" +game.getActivePlayer());
            while (!isWorkerTilePlacementValid) {
                GameServerClientHandler currentClient = clients.get(game.getActivePlayer());

                TilePlacementMessageRequest tilePlacementMessageRequest = (TilePlacementMessageRequest) currentClient.getObjectInputStream().readUnshared();
                Point workerCoordExtracted = tilePlacementMessageRequest.getCoord();
                AbstractTile workerTileExtracted =  (WorkerTile) tilePlacementMessageRequest.getTile();

                int activePlayerIndex = game.getActivePlayer();
                Player activePlayer = game.getPlayerList().get(activePlayerIndex);

                if (game.getBoard().isValidPlacementAsWorkerTile(workerCoordExtracted)
                ||  game.getBoard().isValidPlacementAsWorkerTileWhenWorshipSymbolIsUsed(workerCoordExtracted, activePlayer)){
                    //System.out.println("[GameHandler]: Valid WorkerTile Placement for player=" +game.getActivePlayer() + " tilePlacementMessageRequest=" + tilePlacementMessageRequest.toString());
                    logger.info("[GameHandler]: Valid WorkerTile Placement for player=" +game.getActivePlayer() + " tilePlacementMessageRequest=" + tilePlacementMessageRequest.toString());
                    if (game.getBoard().isValidPlacementAsWorkerTileWhenWorshipSymbolIsUsed(workerCoordExtracted, activePlayer)) {
                        activePlayer.setWorshipSymbol(activePlayer.getWorshipSymbol()  - 1);
                    }

                    //place tile
                    Point coord = tilePlacementMessageRequest.getCoord();
                    WorkerTile workerTileToPlace =  (WorkerTile) tilePlacementMessageRequest.getTile();
                    placeAndEvaluateWorkerTile(coord, workerTileToPlace);


                    manageWorkerTileDeck(activePlayer);

                    TilePlacementMessageResponse response = new TilePlacementMessageResponse(game, ResponseStatus.SUCCESSFUL, ": worker tile placement successful, now select and place jungle tile");
                    sendMessageToAll(response);

                    //System.out.println("[GameHandler]: successful response sent to all player after WORKER placement.");
                    logger.info("[GameHandler]: successful response sent to all player after WORKER placement.");
                } else {
                    //System.out.println("[GameHandler]: Invalid WorkerTile Placement for player=" +game.getActivePlayer() + " , tilePlacementMessageRequest=" + tilePlacementMessageRequest.toString());
                    logger.info("[GameHandler]: Invalid WorkerTile Placement for player=" +game.getActivePlayer() + " , tilePlacementMessageRequest=" + tilePlacementMessageRequest.toString());
                    TilePlacementMessageResponse response = new TilePlacementMessageResponse(game, ResponseStatus.FAILED, ": Invalid placement, select an empty tile and place worker tile adjacent to a jungle tile");
                    sendMessageToPlayer(response, currentClient);
                }
            }




            //System.out.println("[GameHandler]: Moving to JungleTile placement, player=" + game.getActivePlayer());
            logger.info("[GameHandler]: Moving to JungleTile placement, player=" + game.getActivePlayer());
            while (!isJungleTilePlacementValid) {
                GameServerClientHandler currentClient = clients.get(game.getActivePlayer());
                //System.out.println("[GameHandler]: clients=" + clients);
                logger.info("[GameHandler]: clients=" + clients);

                TilePlacementMessageRequest tilePlacementMessageRequest = (TilePlacementMessageRequest) clients.get(game.getActivePlayer()).getObjectInputStream().readUnshared();
                Point jungleCoordExtracted = tilePlacementMessageRequest.getCoord();
                AbstractTile jungleTileExtracted =  (JungleTile) tilePlacementMessageRequest.getTile();

                if (game.getBoard().isValidPlacementAsJungleTile(jungleCoordExtracted)) {
                    //System.out.println("[GameHandler]: Valid JungleTile Placement for player=" +game.getActivePlayer() + " , tilePlacementMessageRequest=" + tilePlacementMessageRequest.toString());
                    logger.info("[GameHandler]: Valid JungleTile Placement for player=" +game.getActivePlayer() + " , tilePlacementMessageRequest=" + tilePlacementMessageRequest.toString());

                    Point coord = tilePlacementMessageRequest.getCoord();
                    JungleTile jungleTileToPlace = (JungleTile) tilePlacementMessageRequest.getTile();
                    placeAndEvaluateJungleTile(coord, jungleTileToPlace);


                    manageJungleTileDeck();
                } else {
                    //System.out.println("[GameHandler]: Invalid JungleTile Placement for player=" +game.getActivePlayer() + " , tilePlacementMessageRequest=" + tilePlacementMessageRequest.toString());
                    logger.info("[GameHandler]: Invalid JungleTile Placement for player=" +game.getActivePlayer() + " , tilePlacementMessageRequest=" + tilePlacementMessageRequest.toString());
                    TilePlacementMessageResponse response = new TilePlacementMessageResponse(game, ResponseStatus.FAILED, ": Invalid placement, select an empty tile and place jungle tile adjacent to any worker tile");
                    sendMessageToPlayer(response, currentClient);
                }
            }




            logger.info("[GameHandler]: values Before switch, isGameEnded=" + game.isGameEnded() + ", activePlayer=" + game.getActivePlayer() + "status: workerPlacement=" + game.hasPlacedWorkerTile() + ", junglePlacement=" + game.hasPlacedJungleTile());

            switchPlayer();
            checkEndGameStatus();
            ResponseStatus messageStatus = game.isGameEnded() ? ResponseStatus.FINAL : ResponseStatus.SUCCESSFUL;

            logger.info("[GameHandler]: values After switched player, isGameEnded=" + game.isGameEnded() + ", activePlayer=" + game.getActivePlayer() + "status: workerPlacement=" + game.hasPlacedWorkerTile() + ", junglePlacement=" + game.hasPlacedJungleTile());

            TilePlacementMessageResponse response = new TilePlacementMessageResponse(game, messageStatus, "'s turn, first select and place worker tile (Other players are inactive)");
            sendMessageToAll(response);
            //System.out.println("[GameHandler]: successful response sent to all player after JUNGLE placement");
            logger.info("[GameHandler]: successful response sent to all player after JUNGLE placement");
        }








        TilePlacementMessageResponse finalMessage = new TilePlacementMessageResponse(game, ResponseStatus.FINAL, "Game End)");
            sendMessageToAll(finalMessage);
        //System.out.println("[GameHandler]: final game status sent to all players");
        logger.info("[GameHandler]: final game status sent to all players");
        //System.out.println("[GameHandler]: Game Ended");
        logger.info("[GameHandler]: Game Ended");
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
        Optional<JungleTile> matchingJungleTile = game.getJungleTilesAvailable().stream().filter(tile -> jungleTile.equals(tile)).findFirst();
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
            //System.out.println("[GameHandler]: ERROR!! matchingWorkerTile was not found");
            logger.error("[GameHandler]: ERROR!! matchingWorkerTile was not found");
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
        //System.out.println("[GameHandler]: Starting common.game instance and player indices sent to common.players");
        logger.info("[GameHandler]: Starting common.game instance and player indices sent to common.players");
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
        //System.out.println("[GameHandler]: Message sent to Player " + (int) (clients.indexOf(client) + 1) + "; message=" + message.toString());
        logger.info("[GameHandler]: Message sent to Player " + (int) (clients.indexOf(client) + 1) + "; message=" + message.toString());
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
        //System.out.println("[GameHandler]: Message sent to all common.players, message=" + message.toString());
        logger.info("[GameHandler]: Message sent to all common.players, message=" + message.toString());
    }

}
