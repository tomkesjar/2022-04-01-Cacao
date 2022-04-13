package game;


import messages.ResponseStatus;
import messages.TilePlacementMessageRequest;
import messages.TilePlacementMessageResponse;
import server.ServerClientHandler;
import tiles.EmptyTile;
import tiles.JungleTile;
import tiles.WorkerTile;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GameHandler {
    private Game game;
    private List<ServerClientHandler> clients;

    private boolean hasGameStarted = false;
    private boolean isWorkerTilePlacementValid = false;
    private boolean isJungleTilePlacementValid = false;

    private boolean isGameEnd = false;

    public GameHandler(List<ServerClientHandler> clients) {
        this.clients = clients;
        this.game = new Game(clients);
        System.out.println("[GameHandler]: Game instance created with players=" + clients.size());
    }


    public void process() throws IOException, ClassNotFoundException {
        sendStartingGameInstanceToPlayers();

        while (!isWorkerTilePlacementValid) {
            ServerClientHandler currentClient = clients.get(game.getActivePlayer());

            TilePlacementMessageRequest tilePlacementMessageRequest =(TilePlacementMessageRequest) currentClient.getObjectInputStream().readUnshared();
            System.out.println("[GameHandler]: TilePlacement message received, tilePlacementMessageRequest=" + tilePlacementMessageRequest.toString());
            if (isValidPlacementAsWorkerTile(tilePlacementMessageRequest)) {
                System.out.println("[GameHandler]: Valid WorkerTile Placement, tilePlacementMessageRequest=" + tilePlacementMessageRequest.toString());
                isWorkerTilePlacementValid = true;
                Point coord = tilePlacementMessageRequest.getCoord();
                WorkerTile workerTile = (WorkerTile) tilePlacementMessageRequest.getTile();
                game.getBoard().setField(coord.x, coord.y, workerTile);
                // ide kene vmi, hogy at tudjon ugorni a jungletile select-re
                // update board to send back
                game.setHasPlacedWorkerTile(true);
                //System.out.println("[GameHandler]: board(0,0)=" + game.getBoard().getField(0,0).toString());

                //testing START
                game.getPlayerList().get(0).setWaterPoint(15);
                game.getPlayerList().get(0).setCoins(20);

                //testing END

                TilePlacementMessageResponse response = new TilePlacementMessageResponse(game, ResponseStatus.SUCCESSFUL, "Now select and place jungle tile");
                sendMessageToPlayer(response, currentClient);
                //System.out.println("[GameHandler]: BoardView board=" + game.getBoard().toString());
            }else {
                System.out.println("[GameHandler]: Invalid WorkerTile Placement, tilePlacementMessageRequest=" + tilePlacementMessageRequest.toString());
                TilePlacementMessageResponse response = new TilePlacementMessageResponse(game, ResponseStatus.FAILED, "Invalid placement, select and place worker tile");
                sendMessageToPlayer(response, currentClient);
            }
        }



        System.out.println("[GameHandler]: Moving to JungleTile placement");
        while (!isJungleTilePlacementValid) {
            ServerClientHandler currentClient = clients.get(game.getActivePlayer());

            TilePlacementMessageRequest tilePlacementMessageRequest =(TilePlacementMessageRequest) clients.get(game.getActivePlayer()).getObjectInputStream().readUnshared();
            System.out.println("[GameHandler]: TilePlacement message received, tilePlacementMessageRequest=" + tilePlacementMessageRequest.toString());

            if (isValidPlacementAsJungleTile(tilePlacementMessageRequest)) {
                System.out.println("[GameHandler]: Valid JungleTile Placement, tilePlacementMessageRequest=" + tilePlacementMessageRequest.toString());
                isJungleTilePlacementValid = true;
                Point coord = tilePlacementMessageRequest.getCoord();
                JungleTile jungleTile = (JungleTile) tilePlacementMessageRequest.getTile();
                game.getBoard().setField(coord.x, coord.y, jungleTile);
                // update board to send back
                game.setHasPlacedJungleTile(true);
                TilePlacementMessageResponse response = new TilePlacementMessageResponse(game, ResponseStatus.SUCCESSFUL, "successfully placed jungle tile");
                sendMessageToPlayer(response, currentClient);
            }else {
                System.out.println("[GameHandler]: Invalid JungleTile Placement, tilePlacementMessageRequest=" + tilePlacementMessageRequest.toString());
                TilePlacementMessageResponse response = new TilePlacementMessageResponse(game, ResponseStatus.FAILED, "Invalid placement, select and place jungle tile");
                sendMessageToPlayer(response, currentClient);
            }
        }





    }

    private void sendStartingGameInstanceToPlayers() {
        List<Integer> playerIndex = Arrays.asList(0);
        clients.forEach(c -> {
            try {
                c.getObjectOutputStream().writeUnshared(game);
                c.getObjectOutputStream().writeUnshared(playerIndex.get(0));
                playerIndex.set(0,playerIndex.get(0)+1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        System.out.println("[GameHandler]: Starting game instance and player indices sent to players");
    }

    private boolean isValidPlacementAsWorkerTile(TilePlacementMessageRequest tilePlacementMessageRequest){
        Point coord = new Point(tilePlacementMessageRequest.getCoord());
        boolean positionCheck = (coord.x + coord.y) % 2 == 0;
        boolean emptyCheck = game.getBoard().getField(coord.x, coord.y) instanceof EmptyTile;
        return positionCheck && emptyCheck;
    }

    private boolean isValidPlacementAsJungleTile(TilePlacementMessageRequest tilePlacementMessageRequest){
        Point coord = new Point(tilePlacementMessageRequest.getCoord());
        boolean positionCheck = (coord.x + coord.y) % 2 == 1;
        boolean emptyCheck = game.getBoard().getField(coord.x, coord.y) instanceof EmptyTile;
        return positionCheck && emptyCheck;
    }

    private void sendMessageToPlayer(Object message, ServerClientHandler client){
        try {
            client.getObjectOutputStream().reset();
            client.getObjectOutputStream().writeUnshared(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("[GameHandler]: Message sent to Player " + (int)(clients.indexOf(client)+1) + "; message="+message.toString());
    }

    private void sendMessageToAll(Object message){
        clients.forEach(c -> {
            try {
                c.getObjectOutputStream().reset();
                c.getObjectOutputStream().writeUnshared(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        System.out.println("[GameHandler]: Message sent to all players, message="+message.toString());
    }

}
