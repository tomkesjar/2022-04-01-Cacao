package gui;

import game.Game;
import tiles.JungleTile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Objects;

public class ActionButtonJungleTile extends JButton implements MouseListener {
    private GuiBoard guiBoard;
    private Game game;
    private int playerIndex;
    private JungleTile jungleTile;
    private boolean isTileSelected;


    public ActionButtonJungleTile(GuiBoard guiBoard, JungleTile jungleTile) {
        this.guiBoard = guiBoard;

        this.game = guiBoard.getGame();
        this.playerIndex = guiBoard.getPlayerIndex();
        this.jungleTile = jungleTile;

        this.isTileSelected = false;

        this.setBackground(Color.CYAN);
        addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (playerIndex == game.getActivePlayer() && !guiBoard.hasPlacedJungleTile() && guiBoard.hasPlacedWorkerTile() && !game.isGameEnded()) {
            //select
            if ((SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) && !isTileSelected) {
                Arrays.stream(this.getParent().getComponents()).forEach(component -> {
                    if (component instanceof ActionButtonJungleTile) {

                        ActionButtonJungleTile actionButtonJungleTile = (ActionButtonJungleTile) component;
                        actionButtonJungleTile.setBorder(
                                component == this
                                        ? BorderFactory.createLineBorder(Color.ORANGE, 4)
                                        : javax.swing.BorderFactory.createEmptyBorder());
                        actionButtonJungleTile.setTileSelected(component == this ? true : false);
                    }
                });
                System.out.println("[ActionButtonJungleTile]: single click -> select tile=" + jungleTile.toShortString());

                guiBoard.getSelectableJunglePanelLink().forEach( boardTileButton -> boardTileButton.setBorder(BorderFactory.createLineBorder(Color.MAGENTA, 4)));
                System.out.println("[ActionButtonJungleTile]: getSelectableJunglePanelPositions=" + guiBoard.getGame().getBoard().getSelectableJunglePanelPositions().toString() );

                guiBoard.setSelectedJungleTile(this.jungleTile);
                isTileSelected = true;
                System.out.println("[ActionButtonJungleTile]: selectedJungleTile=" + guiBoard.getSelectedJungleTile().toShortString());
                //deselect
            } else {
                this.setBorder(javax.swing.BorderFactory.createEmptyBorder());
                guiBoard.setSelectedJungleTile(null);
                isTileSelected = false;

                guiBoard.getSelectableJunglePanelLink().forEach( boardTileButton -> boardTileButton.setBorder(javax.swing.BorderFactory.createEmptyBorder()));

                System.out.println("[ActionButtonJungleTile]: single click -> deselect tile=" + jungleTile.toShortString());
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
        if (playerIndex == game.getActivePlayer() && !guiBoard.hasPlacedJungleTile() && !isTileSelected && !game.isGameEnded()) {
            this.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 4));
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (playerIndex == game.getActivePlayer() && !guiBoard.hasPlacedJungleTile() && !isTileSelected && !game.isGameEnded()) {
            this.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        }
    }



    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    public void setPlayerIndex(int playerIndex) {
        this.playerIndex = playerIndex;
    }

    public JungleTile getJungleTile() {
        return jungleTile;
    }

    public void setJungleTile(JungleTile jungleTile) {
        this.jungleTile = jungleTile;
    }

    public boolean isTileSelected() {
        return isTileSelected;
    }

    public void setTileSelected(boolean tileSelected) {
        isTileSelected = tileSelected;
    }
}