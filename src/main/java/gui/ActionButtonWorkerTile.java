package gui;

import game.Game;
import tiles.WorkerTile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Objects;

public class ActionButtonWorkerTile extends JButton implements MouseListener {
    private GuiBoard guiBoard;
    private Game game;
    private WorkerTile workerTile;
    private int playerIndex;

    private boolean isHasPlacedWorkerTile;
    private boolean isTileSelected;

    public ActionButtonWorkerTile(GuiBoard guiBoard, WorkerTile workerTile) {
        this.guiBoard = guiBoard;
        this.workerTile = workerTile;

        this.game = guiBoard.getGame();
        this.playerIndex = guiBoard.getPlayerIndex();
        this.isHasPlacedWorkerTile = Objects.isNull(guiBoard.getSelectedWorkerTile()) ? false : true;
        this.isTileSelected = false;

        this.setText(workerTile.toShortString());
        this.setBackground(Color.GREEN);
        addMouseListener(this);
        System.out.println("[ActionButtonWorkerTile]: actionButtonWorkerTile created for worker=" + workerTile.toShortString());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (playerIndex == game.getActivePlayer() && !guiBoard.hasPlacedWorkerTile()) {
            //rotate
            if (SwingUtilities.isRightMouseButton(e)) {
                workerTile.turnRightWorkersNinetyDegrees();
                System.out.println("[ActionButtonWorkerTile]: double click -> tile rotated=" + workerTile.toShortString());
                this.setText(workerTile.toShortString());
                if(guiBoard.getSelectedWorkerTile() != null) System.out.println("selected tile="+ guiBoard.getSelectedWorkerTile().toShortString());

                //select
            } else if ((SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) && !isTileSelected) {
                Arrays.stream(this.getParent().getComponents()).forEach(component -> {
                    if (component instanceof ActionButtonWorkerTile) {

                        ActionButtonWorkerTile actionButtonWorkerTile = (ActionButtonWorkerTile) component;
                        actionButtonWorkerTile.setBorder(
                                component == this
                                        ? BorderFactory.createLineBorder(Color.ORANGE, 4)
                                        : javax.swing.BorderFactory.createEmptyBorder());
                        actionButtonWorkerTile.setTileSelected(component == this ? true : false);
                    }
                });
                //System.out.println("[ActionButtonWorkerTile]: single click -> select tile=" + workerTile.toShortString());
                guiBoard.setSelectedWorkerTile(this.workerTile);
                isTileSelected = true;
                System.out.println("selectedWorkerTile=" + guiBoard.getSelectedWorkerTile().toShortString());


                //deselect
            } else if ((SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) && isTileSelected) {
                this.setBorder(javax.swing.BorderFactory.createEmptyBorder());
                guiBoard.setSelectedWorkerTile(null);
                isTileSelected = false;
                //System.out.println("[ActionButtonWorkerTile]: single click -> deselect tile=" + workerTile.toShortString());
            }
        }
    }

    public void clearSelection(){
        isTileSelected = false;
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //nothing to add here
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //nothing to add here
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (playerIndex == game.getActivePlayer() && !guiBoard.hasPlacedWorkerTile() && !isTileSelected) {
            this.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 4));
            //System.out.println("[ActionButtonWorkerTile]: mouseEntered=" + workerTile.toShortString());
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (playerIndex == game.getActivePlayer() && !guiBoard.hasPlacedWorkerTile() && !isTileSelected) {
            this.setBorder(javax.swing.BorderFactory.createEmptyBorder());
            //System.out.println("[ActionButtonWorkerTile]: mouseExited=" + workerTile.toShortString());
        }
    }


    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public WorkerTile getWorkerTile() {
        return workerTile;
    }

    public void setWorkerTile(WorkerTile workerTile) {
        this.workerTile = workerTile;
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    public void setPlayerIndex(int playerIndex) {
        this.playerIndex = playerIndex;
    }

    public boolean isHasPlacedWorkerTile() {
        return isHasPlacedWorkerTile;
    }

    public void setHasPlacedWorkerTile(boolean hasPlacedWorkerTile) {
        isHasPlacedWorkerTile = hasPlacedWorkerTile;
    }

    public boolean isTileSelected() {
        return isTileSelected;
    }

    public void setTileSelected(boolean tileSelected) {
        isTileSelected = tileSelected;
    }
}
