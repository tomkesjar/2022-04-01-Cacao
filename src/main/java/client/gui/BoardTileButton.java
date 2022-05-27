package client.gui;

import common.game.Game;
import common.messages.ResponseStatus;
import common.messages.TilePlacementMessageRequest;
import common.messages.TilePlacementMessageResponse;
import common.tiles.AbstractTile;
import common.tiles.JungleTile;
import common.tiles.WorkerTile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Objects;

public class BoardTileButton extends JButton implements MouseListener {
    private Point coord;
    private GuiBoard guiBoard;
    private ResponseStatus responseStatus;
    private AbstractTile tile;

    private boolean successfulWorkerTileSending;
    private boolean successfulJungleTileSending;

    public BoardTileButton(Point coord, GuiBoard guiBoard) {
        super();
        this.coord = coord;
        this.guiBoard = guiBoard;
        this.tile = guiBoard.getGame().getBoard().getField(coord.x, coord.y);
        addMouseListener(this);
        successfulWorkerTileSending = false;
        successfulJungleTileSending = false;

    }

    @Override
    public void mousePressed(MouseEvent e) {
        System.out.println("[BoardTileButton]: btb press event guiBoard.getPlayerIndex=" + guiBoard.getPlayerIndex() + ", common.game.activePlayer=" + guiBoard.getGame().getActivePlayer());
        if (guiBoard.getPlayerIndex() == guiBoard.getGame().getActivePlayer()) {
            WorkerTile selectedWorkerTile = guiBoard.getSelectedWorkerTile();
            JungleTile selectedJungleTile = guiBoard.getSelectedJungleTile();

            //sending workerTile
            if (Objects.nonNull(selectedWorkerTile) && guiBoard.getGame().hasPlacedWorkerTile() == false) {
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


                //sending JungleTile
            } else if (Objects.nonNull(selectedJungleTile) && guiBoard.getGame().hasPlacedWorkerTile() == true) {
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


                    // clear selection  ONLY IF SUCCESSFUL JUNGLE TILE PLACEMENT
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

            } else {
                System.out.println("[BoardTileButton]: No tile selected yet");
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public Point getCoord() {
        return coord;
    }

    public void setCoord(Point coord) {
        this.coord = coord;
    }

    public GuiBoard getGuiBoard() {
        return guiBoard;
    }

    public void setGuiBoard(GuiBoard guiBoard) {
        this.guiBoard = guiBoard;
    }

    public AbstractTile getTile() {
        return tile;
    }
}
