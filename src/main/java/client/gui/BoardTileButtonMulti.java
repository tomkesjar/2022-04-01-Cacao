package client.gui;

import common.game.Game;
import common.messages.TilePlacementMessageRequest;
import common.messages.TilePlacementMessageResponse;
import common.tiles.JungleTile;
import common.tiles.WorkerTile;

import java.awt.*;
import java.io.IOException;

public class BoardTileButtonMulti extends AbstractBoardTileButton{


    public BoardTileButtonMulti(Point coord, GuiBoard guiBoard) {
        super(coord, guiBoard);
    }

    @Override
    void placeWorkerTile(WorkerTile selectedWorkerTile) {
        try {
            System.out.println("[BoardTileButton]: Attempting to send SelectedWorkerTile. ");
            TilePlacementMessageRequest tilePlacementMessageRequest = new TilePlacementMessageRequest(getCoord(), selectedWorkerTile);
            guiBoard.getGameConnection().getObjectOutputStream().writeUnshared(tilePlacementMessageRequest);
            System.out.println("[BoardTileButton]: TilePlacementMessageRequest sent successfully. tilePlacementMessageRequest=" + tilePlacementMessageRequest);

            // waitForResponse() + update guiBoard if needed
            TilePlacementMessageResponse response = (TilePlacementMessageResponse) guiBoard.getGameConnection().getObjectInputStream().readUnshared();
            System.out.println("[BoardTileButton]: TilePlacementMessageResponse received successfully.");
            responseStatus = response.getStatus();
            Game gameReceived = response.getGame();

            guiBoard.setHasPlacedWorkerTile(gameReceived.hasPlacedWorkerTile());
            //update common.board + panels
            guiBoard.updateGuiBoard(gameReceived, response.getTextMessage());
            System.out.println("[BoardTileButton]: guiBoard updated after workerTile placement");


        } catch (IOException | ClassNotFoundException ioException) {
            ioException.printStackTrace();
            System.out.println("[BoardTileButton]: TilePlacementMessageRequest sending or reading failed. TilePlacementMessageRequest=" + selectedWorkerTile);
        }
    }

    @Override
    void placeJungleTile(JungleTile selectedJungleTile) {
        try {
            System.out.println("[BoardTileButton]: Attempting to send SelectedJungleTile. SelectedJungleTile=" + selectedJungleTile);
            TilePlacementMessageRequest tilePlacementMessageRequest = new TilePlacementMessageRequest(getCoord(), selectedJungleTile);
            guiBoard.getGameConnection().getObjectOutputStream().writeUnshared(tilePlacementMessageRequest);
            System.out.println("[BoardTileButton]: TilePlacementMessageRequest sent successfully. tilePlacementMessageRequest=" + tilePlacementMessageRequest);

            // waitForResponse() + update guiBoard if needed
            TilePlacementMessageResponse response = (TilePlacementMessageResponse) guiBoard.getGameConnection().getObjectInputStream().readUnshared();
            System.out.println("[BoardTileButton]: TilePlacementMessageResponse received successfully. tilePlacementMessageResponse=" + response);
            responseStatus = response.getStatus();
            Game gameReceived = response.getGame();

            guiBoard.setHasPlacedJungleTile(gameReceived.hasPlacedJungleTile());
            System.out.println("[BoardTileButton]: guiBoard hasPlacedJungleTile=" + guiBoard.hasPlacedJungleTile());

            if (guiBoard.hasPlacedJungleTile()) {

                guiBoard.getJungleCardsPanelLink().forEach(tile -> {
                    tile.setBorder(javax.swing.BorderFactory.createEmptyBorder());
                    tile.setTileSelected(false);
                });
                guiBoard.getWorkerCardsPanelLink().forEach(tile -> {
                    tile.setBorder(javax.swing.BorderFactory.createEmptyBorder());
                    tile.setTileSelected(false);
                });

                guiBoard.setSelectedJungleTile(null);
                guiBoard.setSelectedWorkerTile(null);
                guiBoard.setHasPlacedJungleTile(false);
                guiBoard.setHasPlacedWorkerTile(false);

            }

            guiBoard.updateGuiBoard(gameReceived, response.getTextMessage());
            System.out.println("[BoardTileButton]: guiBoard updated after jungleTile placement");

        } catch (IOException | ClassNotFoundException ioException) {
            ioException.printStackTrace();
            System.out.println("[BoardTileButton]: SelectedJungleTile sending or reading failed. SelectedJungleTile=" + selectedJungleTile);
        }
    }


}
