package gui;

import game.Game;
import messages.TilePlacementMessageRequest;
import messages.TilePlacementMessageResponse;
import tiles.JungleTile;
import tiles.WorkerTile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Objects;

public class BoardTileButton extends JButton implements MouseListener {
    private Point coord;
    private GuiBoard guiBoard;

    public BoardTileButton(Point coord, GuiBoard guiBoard) {
        super();
        this.coord = coord;
        this.guiBoard = guiBoard;
        this.setText(guiBoard.getGame().getBoard().getField(coord.x, coord.y).toShortString() + " " + coord.x + " " + coord.y);
        addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (guiBoard.getPlayerIndex() == guiBoard.getGame().getActivePlayer()) {
            WorkerTile selectedWorkerTile = guiBoard.getSelectedWorkerTile();
            JungleTile selectedJungleTile = guiBoard.getSelectedJungleTile();
            //System.out.println("[BoardTileButton - TEST]: SelectedWorkerTile=" + selectedWorkerTile + " - SelectedJungleTile=" + selectedJungleTile + " -- coords=" + coord.x + ":" + coord.y);

            //sending workerTile
            if (Objects.nonNull(selectedWorkerTile)
                    && Objects.isNull(selectedJungleTile)) {
                try {
                    System.out.println("[BoardTileButton]: Attempting to send SelectedWorkerTile. "); //SelectedWorkerTile=" + selectedWorkerTile);
                    TilePlacementMessageRequest tilePlacementMessageRequest = new TilePlacementMessageRequest(getCoord(), selectedWorkerTile);
                    guiBoard.getConnection().getObjectOutputStream().writeUnshared(tilePlacementMessageRequest);
                    System.out.println("[BoardTileButton]: TilePlacementMessageRequest sent successfully. tilePlacementMessageRequest=" + tilePlacementMessageRequest);

                    // waitForResponse() + update guiBoard if needed
                    TilePlacementMessageResponse response = (TilePlacementMessageResponse) guiBoard.getConnection().getObjectInputStream().readUnshared();
                    System.out.println("[BoardTileButton]: TilePlacementMessageResponse received successfully. tilePlacementMessageResponse=" + response);
                    Game gameReceived = response.getGame();
                    //System.out.println("[BoardTileButton]: board(0,0)=" + gameReceived.getBoard().getField(0,0).toShortString()); //System.out.println("[BoardTileButton]: full board=" + gameReceived.getBoard().toString()); //System.out.println("[BoardTileButton]: game Players coins=" + gameReceived.getPlayerList().get(0).getCoins()); //System.out.println("[BoardTileButton]: game Players coins=" + gameReceived.getPlayerList().get(1).getCoins());

                    guiBoard.setHasPlacedWorkerTile(gameReceived.hasPlacedWorkerTile());
                    System.out.println("[BoardTileButton]: guiBoard hasPlacedWorkerTile=" + guiBoard.hasPlacedWorkerTile());
                    //update board + panels
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
                    guiBoard.getConnection().getObjectOutputStream().writeUnshared(tilePlacementMessageRequest);
                    System.out.println("[BoardTileButton]: TilePlacementMessageRequest sent successfully. tilePlacementMessageRequest=" + tilePlacementMessageRequest);

                    // waitForResponse() + update guiBoard if needed
                    TilePlacementMessageResponse response = (TilePlacementMessageResponse) guiBoard.getConnection().getObjectInputStream().readUnshared();
                    System.out.println("[BoardTileButton]: TilePlacementMessageResponse received successfully. tilePlacementMessageResponse=" + response);
                    Game gameReceived = response.getGame();

                    guiBoard.setHasPlacedJungleTile(gameReceived.hasPlacedJungleTile());
                    System.out.println("[BoardTileButton]: guiBoard hasPlacedJungleTile=" + guiBoard.hasPlacedJungleTile());

                    /*
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

                        //ez a game-be kellene
                        //Player activePlayer = guiBoard.getGame().getPlayerList().get(guiBoard.getPlayerIndex());
                        //activePlayer.getCardsAtHand().remove(selectedWorkerTile);
                        //activePlayer.getCardsAtHand().remove(activePlayer.getWorkerTileDeck().drawCard());

                        //guiBoard.getGame().get

                    }
                    //update cards
                    */


                    guiBoard.updateGuiBoard(gameReceived, response.getTextMessage());
                    System.out.println("[BoardTileButton]: guiBoard updated after jungleTile placement");


                    /*
                    //continuous wait & update status
                    Game updateGameReceived = null;
                    int updateGameReceivedActivePlayer = -1;
                    while (guiBoard.getPlayerIndex() != updateGameReceivedActivePlayer) {
                        guiBoard.getConnection().getObjectInputStream().readUnshared();
                        TilePlacementMessageResponse updateResponse = (TilePlacementMessageResponse) guiBoard.getConnection().getObjectInputStream().readUnshared();
                        updateGameReceived = updateResponse.getGame();
                        updateGameReceivedActivePlayer = updateGameReceived.getActivePlayer();
                        guiBoard.setGame(updateGameReceived);
                        guiBoard.updateGuiBoard(updateGameReceived, response.getTextMessage());
                    }
                    */

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
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

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
}
