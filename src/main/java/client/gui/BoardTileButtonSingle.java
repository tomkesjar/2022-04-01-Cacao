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

   /*
    @Override
    public void mouseReleased(MouseEvent e) {
        System.out.println("[BoardTileButtonSingle]: mouse release event happened");

        try {
            Thread.sleep(500);

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
 */

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



/*
                System.out.println("[GameHandler]: Moving to JungleTile placement, player=" + common.game.getActivePlayer());
                while (!isJungleTilePlacementValid) {
                    //* extract from here
                    GameServerClientHandler currentClient = clients.get(common.game.getActivePlayer());
                    System.out.println("[GameHandler]: clients=" + clients);

                    //extract method communicator.getMessage
                    TilePlacementMessageRequest tilePlacementMessageRequest = (TilePlacementMessageRequest) clients.get(common.game.getActivePlayer()).getObjectInputStream().readUnshared();
                    //System.out.println("[GameHandler]: TilePlacement message received from player=" + common.game.getActivePlayer() + " , tilePlacementMessageRequest=" + tilePlacementMessageRequest.toString());
                    Point jungleCoordExtracted = tilePlacementMessageRequest.getCoord();
                    AbstractTile jungleTileExtracted =  (JungleTile) tilePlacementMessageRequest.getTile();
                    //* end extract getMessage

                    if (common.game.getBoard().isValidPlacementAsJungleTile(jungleCoordExtracted)) {
                        System.out.println("[GameHandler]: Valid JungleTile Placement for player=" +common.game.getActivePlayer() + " , tilePlacementMessageRequest=" + tilePlacementMessageRequest.toString());

                        isJungleTilePlacementValid = true;

                        Point coord = tilePlacementMessageRequest.getCoord();
                        jungleTile = (JungleTile) tilePlacementMessageRequest.getTile();
                        placeAndEvaluateJungleTile(coord);


                        //* extract method manageJungleDeck  update + manage jungle tile common.deck
                        manageJungleTileDeck();
                        //* end of manageJungleDeck
                    } else {
                        //extract method communicator.sendMessage (ebbol a common.game + textMessage kell a single-be
                        System.out.println("[GameHandler]: Invalid JungleTile Placement for player=" +common.game.getActivePlayer() + " , tilePlacementMessageRequest=" + tilePlacementMessageRequest.toString());
                        TilePlacementMessageResponse response = new TilePlacementMessageResponse(common.game, ResponseStatus.FAILED, "Invalid placement, select an empty tile and place jungle tile adjacent to any worker tile");
                        sendMessageToPlayer(response, currentClient);
                        // end of extract sendMessage
                    }
                }




                System.out.println("[GameHandler]: values Before switch, isGameEnded=" + common.game.isGameEnded() + ", activePlayer=" + common.game.getActivePlayer() + "status: workerPlacement=" + common.game.hasPlacedWorkerTile() + ", junglePlacement=" + common.game.hasPlacedJungleTile());

                switchPlayer();
                checkEndGameStatus();
                ResponseStatus messageStatus = common.game.isGameEnded() ? ResponseStatus.FINAL : ResponseStatus.SUCCESSFUL;

                System.out.println("[GameHandler]: =================================================================SWITCH=");
                System.out.println("[GameHandler]: values After switched player, isGameEnded=" + common.game.isGameEnded() + ", activePlayer=" + common.game.getActivePlayer() + "status: workerPlacement=" + common.game.hasPlacedWorkerTile() + ", junglePlacement=" + common.game.hasPlacedJungleTile());

                //extract method communicator.sendMessage (ebbol a common.game + textMessage kell a single-be
                TilePlacementMessageResponse response = new TilePlacementMessageResponse(common.game, messageStatus, "'s turn, first select and place worker tile (Other players are inactive)");
                sendMessageToAll(response);
                System.out.println("[GameHandler]: successful response sent to all player after JUNGLE placement");
                // end of extract sendMessage
            }
                ///**************************************************************************************************
    }
    */




/*
    void placeWorkerTile2 (WorkerTile selectedWorkerTile) {
        try {
            System.out.println("[BoardTileButton]: Attempting to send SelectedWorkerTile. "); //SelectedWorkerTile=" + selectedWorkerTile);
            TilePlacementMessageRequest tilePlacementMessageRequest = new TilePlacementMessageRequest(getCoord(), selectedWorkerTile);
            guiBoard.getGameConnection().getObjectOutputStream().writeUnshared(tilePlacementMessageRequest);
            System.out.println("[BoardTileButton]: TilePlacementMessageRequest sent successfully. tilePlacementMessageRequest=" + tilePlacementMessageRequest);

            // waitForResponse() + update guiBoard if needed
            TilePlacementMessageResponse response = (TilePlacementMessageResponse) guiBoard.getGameConnection().getObjectInputStream().readUnshared();
            System.out.println("[BoardTileButton]: TilePlacementMessageResponse received successfully."); // tilePlacementMessageResponse=" + response);
            responseStatus = response.getStatus();
            Game gameReceived = response.getGame();

            guiBoard.setHasPlacedWorkerTile(gameReceived.hasPlacedWorkerTile());
            //System.out.println("[BoardTileButton]: guiBoard hasPlacedWorkerTile=" + guiBoard.hasPlacedWorkerTile());
            //update common.board + panels
            guiBoard.updateGuiBoard(gameReceived, response.getTextMessage());
            System.out.println("[BoardTileButton]: guiBoard updated after workerTile placement");


        } catch (IOException | ClassNotFoundException ioException) {
            ioException.printStackTrace();
            System.out.println("[BoardTileButton]: TilePlacementMessageRequest sending or reading failed. TilePlacementMessageRequest=" + selectedWorkerTile);
        }
    }

 */
}



