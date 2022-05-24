package client.gui;

import common.messages.ResponseStatus;
import common.tiles.AbstractTile;
import common.tiles.JungleTile;
import common.tiles.WorkerTile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Objects;

public abstract class AbstractBoardTileButton extends JButton implements MouseListener {
    protected Point coord;
    protected GuiBoard guiBoard;
    protected ResponseStatus responseStatus;
    protected AbstractTile tile;

    protected boolean successfulWorkerTileSending;
    protected boolean successfulJungleTileSending;


    public AbstractBoardTileButton(Point coord, GuiBoard guiBoard) {
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
                placeWorkerTile(selectedWorkerTile);


                //sending JungleTile
            } else if (Objects.nonNull(selectedJungleTile) && guiBoard.getGame().hasPlacedWorkerTile() == true) {
                placeJungleTile(selectedJungleTile);
            } else {
                System.out.println("[BoardTileButton]: No tile selected yet");
            }
        }
    }

    abstract void placeWorkerTile(WorkerTile selectedWorkerTile);

    abstract void placeJungleTile(JungleTile selectedJungleTile);

    @Override
    public void mouseReleased(MouseEvent e) {
        System.out.println("[AbstractBoardTileButton]: mouse release event happened");
    }

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
