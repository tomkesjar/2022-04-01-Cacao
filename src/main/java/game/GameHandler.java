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

    JungleTile jungleTile;
    WorkerTile workerTile;

    public GameHandler(List<ServerClientHandler> clients) {
        this.clients = clients;
        this.game = new Game(clients);
        System.out.println("[GameHandler]: Game instance created with players=" + clients.size());
    }


    public void process() throws IOException, ClassNotFoundException {
        game.getBoard().selectPossibleWorkerAndJungleTilesForPlacement();

        sendStartingGameInstanceToPlayers();


        while (!game.checkIfIsGameEnd()) {
            System.out.println("[GameHandler]: starting the loop, checkIfIsGameEnd=" +game.checkIfIsGameEnd());

            System.out.println("[GameHandler]: Moving to WorkerTile placement for player=" +game.getActivePlayer());
            while (!isWorkerTilePlacementValid) {
                ServerClientHandler currentClient = clients.get(game.getActivePlayer());

                TilePlacementMessageRequest tilePlacementMessageRequest = (TilePlacementMessageRequest) currentClient.getObjectInputStream().readUnshared();
                if (game.getBoard().isValidPlacementAsWorkerTile(tilePlacementMessageRequest)) {
                    System.out.println("[GameHandler]: Valid WorkerTile Placement for player=" +game.getActivePlayer() + " tilePlacementMessageRequest=" + tilePlacementMessageRequest.toString());

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
                    game.calculatePoints();
                    game.calculateRanks();

                    game.getBoard().selectPossibleWorkerAndJungleTilesForPlacement();

                    int activePlayerIndex = game.getActivePlayer();
                    Player activePlayer = game.getPlayerList().get(activePlayerIndex);
                    Optional<WorkerTile> matchingWorkerTile = activePlayer.getCardsAtHand().stream().filter(tile -> workerTile.equals(tile)).findFirst();
                    if (matchingWorkerTile.isPresent()){
                        activePlayer.getCardsAtHand().remove(matchingWorkerTile.get());
                    } else {
                        System.out.println("[GameHandler]: ERROR!! matchingWorkerTile was not found");
                    }

                    Optional<WorkerTile> optionalWorkerTile = activePlayer.getWorkerTileDeck().drawCard();
                    if (optionalWorkerTile.isPresent() && activePlayer.getCardsAtHand().size() < 3) {
                        activePlayer.getCardsAtHand().add(optionalWorkerTile.get());
                    }

                    TilePlacementMessageResponse response = new TilePlacementMessageResponse(game, ResponseStatus.SUCCESSFUL, "worker tile placement successful, now select and place jungle tile");
                    sendMessageToAll(response);

                    System.out.println("[GameHandler]: successful response sent to all player after WORKER placement."/* response=" + response*/);
                } else {
                    System.out.println("[GameHandler]: Invalid WorkerTile Placement for player=" +game.getActivePlayer() + " , tilePlacementMessageRequest=" + tilePlacementMessageRequest.toString());
                    TilePlacementMessageResponse response = new TilePlacementMessageResponse(game, ResponseStatus.FAILED, "Invalid placement, select an empty tile and place worker tile adjacent to a jungle tile");
                    sendMessageToPlayer(response, currentClient);
                }
            }

            //System.out.println("[GameHandler]: Moving to JungleTile placement, player=" + game.getActivePlayer());
            while (!isJungleTilePlacementValid) {
                ServerClientHandler currentClient = clients.get(game.getActivePlayer());

                TilePlacementMessageRequest tilePlacementMessageRequest = (TilePlacementMessageRequest) clients.get(game.getActivePlayer()).getObjectInputStream().readUnshared();
                //System.out.println("[GameHandler]: TilePlacement message received from player=" + game.getActivePlayer() + " , tilePlacementMessageRequest=" + tilePlacementMessageRequest.toString());

                if (game.getBoard().isValidPlacementAsJungleTile(tilePlacementMessageRequest)) {
                    System.out.println("[GameHandler]: Valid JungleTile Placement for player=" +game.getActivePlayer() + " , tilePlacementMessageRequest=" + tilePlacementMessageRequest.toString());
                    isJungleTilePlacementValid = true;
                    Point coord = tilePlacementMessageRequest.getCoord();
                    jungleTile = (JungleTile) tilePlacementMessageRequest.getTile();
                    game.getBoard().setField(coord.x, coord.y, jungleTile);
                    game.getBoard().setFreshJungleTile(workerTile);
                    game.getBoard().setFreshJungleTilePoint(coord);
                    game.setHasPlacedJungleTile(true);
                    game.setHasPlacedWorkerTile(false);


                    JungleTile currentTile = (JungleTile) game.getBoard().getField(coord.x, coord.y);
                    currentTile.processNeighbours(coord, game);
                    game.calculatePoints();
                    game.calculateRanks();

                    game.getBoard().selectPossibleWorkerAndJungleTilesForPlacement();

                    //game.getBoard().getSelectableJunglePanelPositions().forEach(pos -> {
  //                      System.out.println(pos.get + " : " + pos.getValue());
//                    });


                    Optional<JungleTile> matchingJungleTile = game.getJungleTilesAvailable().stream().filter(tile -> jungleTile.equals(tile)).findFirst();      //TODO SOS CHECK!!!
                    if (matchingJungleTile.isPresent()) {
                        game.getJungleTilesAvailable().remove(matchingJungleTile.get());
                    }
                    Optional<JungleTile> drawnCard = game.getJungleTileDeck().drawCard();
                    if (drawnCard.isPresent()) {
                        game.getJungleTilesAvailable().add(drawnCard.get());
                    }
                } else {
                    System.out.println("[GameHandler]: Invalid JungleTile Placement for player=" +game.getActivePlayer() + " , tilePlacementMessageRequest=" + tilePlacementMessageRequest.toString());
                    TilePlacementMessageResponse response = new TilePlacementMessageResponse(game, ResponseStatus.FAILED, "Invalid placement, select an empty tile and place jungle tile adjacent to any worker tile");
                    sendMessageToPlayer(response, currentClient);
                }
            }

            switchPlayer();
            System.out.println("[GameHandler]: =================================================================SWITCH=");
            System.out.println("[GameHandler]: switched player, activePlayer=" + game.getActivePlayer() + "status: workerPlacement=" + game.hasPlacedWorkerTile() + ", junglePlacement=" + game.hasPlacedJungleTile());

            TilePlacementMessageResponse response = new TilePlacementMessageResponse(game, ResponseStatus.SUCCESSFUL, " 's turn, first select and place worker tile (Other players are inactive)");
            sendMessageToAll(response);
            System.out.println("[GameHandler]: successful response sent to all player after JUNGLE placement");

        }
        //TODO: do something here SOS Pop up window ranking
        System.out.println("[GameHandler]: ******************************************************************************");
        System.out.println("[GameHandler]: ******************************************************************************");
        System.out.println("[GameHandler]: Game Ended");
        System.out.println("[GameHandler]: ******************************************************************************");
        System.out.println("[GameHandler]: ******************************************************************************");

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
