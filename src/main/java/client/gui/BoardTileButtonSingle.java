package client.gui;

import common.game.GameHandler;
import common.players.Player;
import common.tiles.JungleTile;
import common.tiles.WorkerTile;

import java.awt.*;
import java.awt.event.MouseEvent;

public class BoardTileButtonSingle extends AbstractBoardTileButton {

    private GameHandler gameHandler;

    public BoardTileButtonSingle(Point coord, GuiBoard guiBoard, GameHandler gameHandler) {
        super(coord, guiBoard);
        this.gameHandler = gameHandler;
    }


    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);

        try {
            gameHandler.checkEndGameStatus();
            if(gameHandler.getGame().isGameEnded()){
                Thread.sleep(1_000);
                GuiEndGameResult guiEndGameResult = new GuiEndGameResult(gameHandler.getGame());
                System.out.println("[BoardTileButtonSingle]: guiEndGameResult created");

                guiEndGameResult.setVisible(true);
                guiEndGameResult.setFocusable(true);
                guiEndGameResult.requestFocusInWindow();
            }
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }



    @Override
    void placeWorkerTile(WorkerTile selectedWorkerTile) {
        boolean isGameEndVariable = gameHandler.getGame().isGameEnded();
        boolean isWorkerTilePlacementValidVariable = gameHandler.isWorkerTilePlacementValid();
        boolean isValidPlacementAsWorkerTile = (gameHandler.getGame().getBoard().isValidPlacementAsWorkerTile(this.getCoord()));
        Player activePlayer = gameHandler.getGame().getPlayerList().get(gameHandler.getGame().getActivePlayer());
        boolean isValidPlacementAsWorkerTileWhenWorshipSymbolIsUsed = gameHandler.getGame().getBoard().isValidPlacementAsWorkerTileWhenWorshipSymbolIsUsed(this.getCoord(), activePlayer);

        if (!isGameEndVariable) {
            if (!isWorkerTilePlacementValidVariable
                    && (isValidPlacementAsWorkerTile || isValidPlacementAsWorkerTileWhenWorshipSymbolIsUsed)
            ) {
                if (isValidPlacementAsWorkerTileWhenWorshipSymbolIsUsed) {
                    activePlayer.setWorshipSymbol(activePlayer.getWorshipSymbol() - 1);
                }

                WorkerTile workerTileToPlace = guiBoard.getSelectedWorkerTile();
                gameHandler.placeAndEvaluateWorkerTile(this.coord, workerTileToPlace);
                gameHandler.manageWorkerTileDeck(activePlayer);

                String workerTileSuccessfulMessage = ": worker tile placement successful, now select and place jungle tile";
                guiBoard.updateGuiBoard(gameHandler.getGame(), workerTileSuccessfulMessage);
            } else {
                String workerTileUnsuccessfulMessage = ": Invalid placement, select an empty tile and place worker tile adjacent to a jungle tile";
                guiBoard.updateGuiBoard(gameHandler.getGame(), workerTileUnsuccessfulMessage);
            }
        }
    }





    @Override
    void placeJungleTile(JungleTile selectedJungleTile) {
        gameHandler.getGame().getBoard().getSelectableJunglePanelPositions();
        if (!gameHandler.getGame().isGameEnded()) {
            if (!gameHandler.isJungleTilePlacementValid()
                    && gameHandler.getGame().getBoard().isValidPlacementAsJungleTile(this.getCoord())) {
                JungleTile jungleTileToPlace = guiBoard.getSelectedJungleTile();
                gameHandler.placeAndEvaluateJungleTile(this.coord, jungleTileToPlace);

                gameHandler.manageJungleTileDeck();
                String workerTileSuccessfulMessage = "'s turn, first select and place worker tile (Other players are inactive)";


                gameHandler.switchPlayer();
                gameHandler.checkEndGameStatus();
                guiBoard.updateGuiBoard(gameHandler.getGame(), workerTileSuccessfulMessage);

                for (Player botPlayer : gameHandler.getGame().getPlayerList()) {
                    if (Player.PlayerType.HUMAN != botPlayer.getPlayerType()) {
                        if (!gameHandler.getGame().isGameEnded()) {
                            //worker

                            botPlayer.placeBasicWorkerTile(gameHandler);
                            gameHandler.manageWorkerTileDeck(botPlayer);
                            botPlayer.evaluateWorkerTilePlacement(gameHandler, gameHandler.getGame().getBoard().getFreshWorkerTilePoint(), gameHandler.getWorkerTile());

                            gameHandler.getGame().calculatePoints();
                            gameHandler.getGame().calculateRanks();
                            gameHandler.getGame().getBoard().selectPossibleWorkerAndJungleTilesForPlacement();


                            //jungle
                            botPlayer.placeBasicJungleTile(gameHandler);
                            gameHandler.manageJungleTileDeck();
                            gameHandler.getJungleTile().processNeighbours(gameHandler.getGame().getBoard().getFreshJungleTilePoint(), gameHandler.getGame());

                            gameHandler.getGame().calculatePoints();
                            gameHandler.getGame().calculateRanks();
                            gameHandler.getGame().getBoard().selectPossibleWorkerAndJungleTilesForPlacement();

                            gameHandler.switchPlayer();
                            gameHandler.checkEndGameStatus();
                            guiBoard.updateGuiBoard(gameHandler.getGame(), workerTileSuccessfulMessage);
                        }
                    }
                }

            } else {
                String workerTileUnsuccessfulMessage = " Invalid placement, select an empty tile and place jungle tile adjacent to any worker tile";
                guiBoard.updateGuiBoard(gameHandler.getGame(), workerTileUnsuccessfulMessage);
            }
        }
    }


}



