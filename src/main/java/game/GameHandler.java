package game;


import messages.ResponseStatus;
import messages.TilePlacementMessageRequest;
import messages.TilePlacementMessageResponse;
import players.Player;
import server.ServerClientHandler;
import tiles.EmptyTile;
import tiles.JungleTile;
import tiles.WorkerTile;

import javax.swing.text.html.Option;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class GameHandler {
    private Game game;
    private List<ServerClientHandler> clients;

    private boolean isWorkerTilePlacementValid = false;
    private boolean isJungleTilePlacementValid = false;

    private boolean isGameEnd = false;

    JungleTile jungleTile;
    WorkerTile workerTile;

    public GameHandler(List<ServerClientHandler> clients) {
        this.clients = clients;
        this.game = new Game(clients);
        System.out.println("[GameHandler]: Game instance created with players=" + clients.size());
    }


    public void process() throws IOException, ClassNotFoundException {
        sendStartingGameInstanceToPlayers();


        while (!game.checkIfIsGameEnd()) {
            System.out.println("[GameHandler]: starting the loop, checkIfIsGameEnd=" +game.checkIfIsGameEnd());

            System.out.println("[GameHandler]: Moving to WorkerTile placement");
            while (!isWorkerTilePlacementValid) {
                ServerClientHandler currentClient = clients.get(game.getActivePlayer());

                TilePlacementMessageRequest tilePlacementMessageRequest = (TilePlacementMessageRequest) currentClient.getObjectInputStream().readUnshared();
                System.out.println("[GameHandler]: TilePlacement message received, tilePlacementMessageRequest=" + tilePlacementMessageRequest.toString());
                if (isValidPlacementAsWorkerTile(tilePlacementMessageRequest)) {
                    System.out.println("[GameHandler]: Valid WorkerTile Placement"/*, tilePlacementMessageRequest=" + tilePlacementMessageRequest.toString()*/);

                    //place tile
                    Point coord = tilePlacementMessageRequest.getCoord();
                    workerTile = (WorkerTile) tilePlacementMessageRequest.getTile();
                    game.getBoard().setField(coord.x, coord.y, workerTile);
                    game.getBoard().setFreshWorkerTile(workerTile);
                    game.getBoard().setFreshWorkerTilePoint(coord);
                    isWorkerTilePlacementValid = true;
                    game.setHasPlacedWorkerTile(true);

                    WorkerTile currentTile = (WorkerTile) game.getBoard().getField(coord.x, coord.y);
                    currentTile.processNeighbours(coord, game);

                    int activePlayerIndex = game.getActivePlayer();
                    Player activePlayer = game.getPlayerList().get(activePlayerIndex);
                    Optional<WorkerTile> matchingWorkerTile = activePlayer.getCardsAtHand().stream().filter(tile -> workerTile.equals(tile)).findFirst();
                    if (matchingWorkerTile.isPresent()){
                        activePlayer.getCardsAtHand().remove(matchingWorkerTile.get());
                    }
                    Optional<WorkerTile> optionalWorkerTile = activePlayer.getWorkerTileDeck().drawCard();
                    if (optionalWorkerTile.isPresent()) {
                        activePlayer.getCardsAtHand().add(optionalWorkerTile.get());
                    }

                    TilePlacementMessageResponse response = new TilePlacementMessageResponse(game, ResponseStatus.SUCCESSFUL, "worker tile placement successful, now select and place jungle tile");
                    sendMessageToAll(response);
                    //sendMessageToPlayer(response, currentClient);
                    System.out.println("[GameHandler]: successful response sent to all player after WORKER placement."/* response=" + response*/);
                } else {
                    System.out.println("[GameHandler]: Invalid WorkerTile Placement, tilePlacementMessageRequest=" + tilePlacementMessageRequest.toString());
                    TilePlacementMessageResponse response = new TilePlacementMessageResponse(game, ResponseStatus.FAILED, "Invalid placement, select an empty tile and place worker tile adjacent to a jungle tile");
                    sendMessageToPlayer(response, currentClient);
                }
            }
            //TODO: draw worker Tile

            System.out.println("[GameHandler]: Moving to JungleTile placement");
            while (!isJungleTilePlacementValid) {
                ServerClientHandler currentClient = clients.get(game.getActivePlayer());

                TilePlacementMessageRequest tilePlacementMessageRequest = (TilePlacementMessageRequest) clients.get(game.getActivePlayer()).getObjectInputStream().readUnshared();
                System.out.println("[GameHandler]: TilePlacement message received, tilePlacementMessageRequest=" + tilePlacementMessageRequest.toString());

                if (isValidPlacementAsJungleTile(tilePlacementMessageRequest)) {
                    System.out.println("[GameHandler]: Valid JungleTile Placement, tilePlacementMessageRequest=" + tilePlacementMessageRequest.toString());
                    isJungleTilePlacementValid = true;
                    Point coord = tilePlacementMessageRequest.getCoord();
                    jungleTile = (JungleTile) tilePlacementMessageRequest.getTile();
                    game.getBoard().setField(coord.x, coord.y, jungleTile);
                    game.getBoard().setFreshJungleTile(workerTile);
                    game.getBoard().setFreshJungleTilePoint(coord);
                    game.setHasPlacedJungleTile(true);

                    JungleTile currentTile = (JungleTile) game.getBoard().getField(coord.x, coord.y);
                    currentTile.processNeighbours(coord, game);

                    Optional<JungleTile> matchingJungleTile = game.getJungleTilesAvailable().stream().filter(tile -> jungleTile.equals(tile)).findFirst();
                    if (matchingJungleTile.isPresent()) {
                        game.getJungleTilesAvailable().remove(matchingJungleTile.get());
                    }
                    Optional<JungleTile> drawnCard = game.getJungleTileDeck().drawCard();
                    if (drawnCard.isPresent()) {
                        game.getJungleTilesAvailable().add(drawnCard.get());
                    }

                    switchPlayer();

                    TilePlacementMessageResponse response = new TilePlacementMessageResponse(game, ResponseStatus.SUCCESSFUL, " 's turn, first select and place worker tile (Other players are inactive)");
                    sendMessageToAll(response);
                    //sendMessageToPlayer(response, currentClient);
                    System.out.println("[GameHandler]: successful response sent to all player after JUNGLE placement."/* response=" + response*/);
                } else {
                    System.out.println("[GameHandler]: Invalid JungleTile Placement, tilePlacementMessageRequest=" + tilePlacementMessageRequest.toString());
                    TilePlacementMessageResponse response = new TilePlacementMessageResponse(game, ResponseStatus.FAILED, "Invalid placement, select an empty tile and place jungle tile adjacent to any worker tile");
                    sendMessageToPlayer(response, currentClient);
                }
            }
/*
            Optional<JungleTile> matchingJungleTile = game.getJungleTilesAvailable().stream().filter(tile -> jungleTile.equals(tile)).findFirst();
            if (matchingJungleTile.isPresent()) {
                game.getJungleTilesAvailable().remove(matchingJungleTile.get());
            }
            Optional<JungleTile> drawnCard = game.getJungleTileDeck().drawCard();
            if (drawnCard.isPresent()) {
                game.getJungleTilesAvailable().add(drawnCard.get());
            }

 */
/*
            int activePlayerIndex = game.getActivePlayer();
            Player activePlayer = game.getPlayerList().get(activePlayerIndex);
            Optional<WorkerTile> matchingWorkerTile = activePlayer.getCardsAtHand().stream().filter(tile -> workerTile.equals(tile)).findFirst();
            if (matchingWorkerTile.isPresent()){
                activePlayer.getCardsAtHand().remove(matchingWorkerTile.get());
            }
            Optional<WorkerTile> optionalWorkerTile = activePlayer.getWorkerTileDeck().drawCard();
            if (optionalWorkerTile.isPresent()) {
                activePlayer.getCardsAtHand().add(optionalWorkerTile.get());
            }
*/
/*
            switchPlayer();
            System.out.println("[GameHandler]: switched player");

            //TODO: send to all clients
            System.out.println("[GameHandler]: Move to sending message to all");
            TilePlacementMessageResponse responseUpdate = new TilePlacementMessageResponse(game, ResponseStatus.UPDATE, "'s turn (Other players are inactive)"); //first select and place a worker tile adjacent to any jungle tile
            sendMessageToAll(responseUpdate);
 */
            // System.out.println("[GameHandler]: successful response sent to all players."/* responseUpdate=" + responseUpdate*/);

        }
        //TODO: do something here SOS Pop up window ranking
        System.out.println("[GameHandler]: Game Ended");

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

    private void switchPlayer() {
        game.setHasPlacedWorkerTile(false);
        game.setHasPlacedJungleTile(false);
        isWorkerTilePlacementValid = false;
        isJungleTilePlacementValid = false;
        game.callNextPlayer();
    }
/*
    private boolean checkIfIsGameEnd() {
        //last player, runs out worker or jungle tile
        boolean isNoMoreJungleTile = false;
        boolean isNoMoreWorkerTileForLastPlayer = false;

        int activePlayerIndex = game.getActivePlayer();
        Player activePlayer = game.getPlayerList().get(activePlayerIndex);


        if (game.getJungleTileDeck().getDeck().size() == 0 && game.getJungleTilesAvailable().size() == 0) {
            isNoMoreJungleTile = true;
        }
        if (activePlayer.getWorkerTileDeck().getDeck().size() == 0 && activePlayer.getCardsAtHand().size() == 0) {
            isNoMoreWorkerTileForLastPlayer = true;
        }

        return isNoMoreJungleTile && isNoMoreWorkerTileForLastPlayer;       //TODO: SOS: add 'or' between the two booleans

    }
*/

    private boolean isValidPlacementAsWorkerTile(TilePlacementMessageRequest tilePlacementMessageRequest) {
        Point coord = new Point(tilePlacementMessageRequest.getCoord());
        boolean positionCheck = (coord.x + coord.y) % 2 == 0;
        boolean emptyCheck = game.getBoard().getField(coord.x, coord.y) instanceof EmptyTile;
        boolean isAdjacentToJungleTile = isAdjacentToJungleTile(coord);
        System.out.println("[GameHandler]: Placement validation: positionCheck=" + positionCheck + "; emptyCheck=" + emptyCheck + "; isAdjacentToJungleTile=" + isAdjacentToJungleTile);

        return positionCheck && emptyCheck && isAdjacentToJungleTile;
    }

    private boolean isValidPlacementAsJungleTile(TilePlacementMessageRequest tilePlacementMessageRequest) {
        Point coord = new Point(tilePlacementMessageRequest.getCoord());
        boolean positionCheck = (coord.x + coord.y) % 2 == 1;
        boolean emptyCheck = game.getBoard().getField(coord.x, coord.y) instanceof EmptyTile;
        boolean isAdjacenttoWorkerTile = isAdjacentToWorkerTile(coord);

        return positionCheck && emptyCheck && isAdjacenttoWorkerTile;
    }

    private boolean isJungleTile(Point coord) {
        if (coord.x < 0 || coord.x >= game.getBoard().getWidth() || coord.y < 0 || coord.y >= game.getBoard().getHeight()) {
            return false;
        }
        return game.getBoard().getField(coord.x, coord.y) instanceof JungleTile;
    }

    private boolean isAdjacentToJungleTile(Point coord) {
        boolean upNeighbour = isJungleTile(new Point(coord.x, coord.y - 1));
        boolean downNeighbour = isJungleTile(new Point(coord.x, coord.y + 1));
        boolean leftNeighbour = isJungleTile(new Point(coord.x - 1, coord.y));
        boolean rightNeighbour = isJungleTile(new Point(coord.x + 1, coord.y - 1));

        return upNeighbour || downNeighbour || leftNeighbour || rightNeighbour;
    }

    private boolean isWorkerTile(Point coord) {
        if (coord.x < 0 || coord.x >= game.getBoard().getWidth() || coord.y < 0 || coord.y >= game.getBoard().getHeight()) {
            return false;
        }
        return game.getBoard().getField(coord.x, coord.y) instanceof WorkerTile;
    }

    private boolean isAdjacentToWorkerTile(Point coord) {
        boolean upNeighbour = isWorkerTile(new Point(coord.x, coord.y - 1));
        boolean downNeighbour = isWorkerTile(new Point(coord.x, coord.y + 1));
        boolean leftNeighbour = isWorkerTile(new Point(coord.x - 1, coord.y));
        boolean rightNeighbour = isWorkerTile(new Point(coord.x + 1, coord.y));

        return upNeighbour || downNeighbour || leftNeighbour || rightNeighbour;
    }

    private void sendMessageToPlayer(Object message, ServerClientHandler client) {
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
