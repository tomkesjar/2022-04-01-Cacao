package common.tiles;

import common.board.Board;
import common.game.Game;
import common.messages.Pair;
import common.players.Player;
import common.players.PlayerColour;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.when;

public class JungleTileTest {

    private Game mockGame;
    private Board mockBoard;
    private JungleTile underTest;

    @Before
    public void setup(){
        mockBoard = createMockBoard();
        mockGame = createMockGame();
    }


    private Board createMockBoard(){
        Board mockBoard = Mockito.mock(Board.class);
        when(mockBoard.getWidth()).thenReturn(16);
        when(mockBoard.getHeight()).thenReturn(10);

        when(mockBoard.getField(1,2)).thenReturn(new WorkerTile(1,1,1,1, PlayerColour.RED));
        when(mockBoard.getField(2,1)).thenReturn(new WorkerTile(1,0,0,3, PlayerColour.BLUE));
        when(mockBoard.getField(3,2)).thenReturn(new WorkerTile(2,1,0,1, PlayerColour.GREEN));
        when(mockBoard.getField(2,3)).thenReturn(new WorkerTile(1,1,1,1, PlayerColour.YELLOW));
        when(mockBoard.getField(2,2)).thenReturn(underTest);

        when(mockBoard.getField(4,5)).thenReturn(new WorkerTile(0,0,3,1, PlayerColour.RED));
        when(mockBoard.getField(5,4)).thenReturn(new WorkerTile(1,0,0,3, PlayerColour.BLUE));
        when(mockBoard.getField(6,5)).thenReturn(new WorkerTile(3,1,0,0, PlayerColour.GREEN));
        when(mockBoard.getField(5,6)).thenReturn(new WorkerTile(0,3,1,0, PlayerColour.YELLOW));
        when(mockBoard.getField(5,5)).thenReturn(underTest);

        return mockBoard;
    }

    private Game createMockGame(){
        Game mockGame = Mockito.mock(Game.class);
        when(mockGame.getBoard()).thenReturn(mockBoard);

        Player dummyPlayer1 = new Player.PlayerBuilder()
                .setPlayerColour(PlayerColour.RED)
                .setCoins(0)
                .setNumberOfCacaoBean(0)
                .setTemplePoint(0)
                .setTemplePointBonus(0)
                .setWorshipSymbol(0)
                .build();

        Player dummyPlayer2 = new Player.PlayerBuilder()
                .setPlayerColour(PlayerColour.BLUE)
                .setCoins(0)
                .setNumberOfCacaoBean(0)
                .setTemplePoint(0)
                .setTemplePointBonus(0)
                .setWorshipSymbol(0)
                .build();

        Player dummyPlayer3 = new Player.PlayerBuilder()
                .setPlayerColour(PlayerColour.GREEN)
                .setCoins(0)
                .setNumberOfCacaoBean(0)
                .setTemplePoint(0)
                .setTemplePointBonus(0)
                .setWorshipSymbol(0)
                .build();

        Player dummyPlayer4 = new Player.PlayerBuilder()
                .setPlayerColour(PlayerColour.YELLOW)
                .setCoins(0)
                .setNumberOfCacaoBean(0)
                .setTemplePoint(0)
                .setTemplePointBonus(0)
                .setWorshipSymbol(0)
                .build();

        List<Player> playerList = new ArrayList<>();
        playerList.add(dummyPlayer1);
        playerList.add(dummyPlayer2);
        playerList.add(dummyPlayer3);
        playerList.add(dummyPlayer4);

        when(mockGame.getPlayerList()).thenReturn(playerList);
        //when(mockGame.getActivePlayer()).thenReturn(0);

        when(mockGame.isFieldValid(1,2)).thenReturn(true);
        when(mockGame.isFieldValid(2,1)).thenReturn(true);
        when(mockGame.isFieldValid(3,2)).thenReturn(true);
        when(mockGame.isFieldValid(2,3)).thenReturn(true);

        return mockGame;
    }

    private JungleTile changeJungleTile(JungleTile newTile){
        return newTile;
    }

    @Test
    public void testProcessNeighboursWhenPlantationIsPlaced(){
        underTest = changeJungleTile(new Plantation(1));
        underTest.processNeighbours(new Point(2,2),mockGame);
        Assert.assertEquals(1,mockGame.getPlayerList().get(0).getNumberOfCacaoBean());
        Assert.assertEquals(3,mockGame.getPlayerList().get(1).getNumberOfCacaoBean());
        Assert.assertEquals(2,mockGame.getPlayerList().get(2).getNumberOfCacaoBean());
        Assert.assertEquals(1,mockGame.getPlayerList().get(3).getNumberOfCacaoBean());
    }

    @Test
    public void testProcessNeighboursWhenTempleIsPlaced(){
        underTest = changeJungleTile(new Temple());
        underTest.processNeighbours(new Point(2,2),mockGame);
        Assert.assertEquals(1,mockGame.getPlayerList().get(0).getTemplePoint());
        Assert.assertEquals(3,mockGame.getPlayerList().get(1).getTemplePoint());
        Assert.assertEquals(2,mockGame.getPlayerList().get(2).getTemplePoint());
        Assert.assertEquals(1,mockGame.getPlayerList().get(3).getTemplePoint());
    }

    @Test
    public void testProcessNeighboursWhenMarketIsPlaced(){
        testProcessNeighboursWhenPlantationIsPlaced();

        underTest = changeJungleTile(new Market(Market.MarketPrice.HIGH));
        underTest.processNeighbours(new Point(5,5),mockGame);
        Assert.assertEquals(4,mockGame.getPlayerList().get(0).getCoins());
        Assert.assertEquals(12,mockGame.getPlayerList().get(1).getCoins());
        Assert.assertEquals(8,mockGame.getPlayerList().get(2).getCoins());
        Assert.assertEquals(4,mockGame.getPlayerList().get(3).getCoins());
    }

    @Test
    public void testProcessNeighboursWhenWorshipIsPlaced(){
        underTest = changeJungleTile(new WorshipSite());
        underTest.processNeighbours(new Point(2,2),mockGame);
        Assert.assertEquals(1,mockGame.getPlayerList().get(0).getWorshipSymbol());
        Assert.assertEquals(3,mockGame.getPlayerList().get(1).getWorshipSymbol());
        Assert.assertEquals(2,mockGame.getPlayerList().get(2).getWorshipSymbol());
        Assert.assertEquals(1,mockGame.getPlayerList().get(3).getWorshipSymbol());
    }

    @Test
    public void testProcessNeighboursWhenWorshipIsPlacedSecondTime(){
        underTest = changeJungleTile(new WorshipSite());

        underTest = changeJungleTile(new WorshipSite());
        underTest.processNeighbours(new Point(5,5),mockGame);
        Assert.assertEquals(3,mockGame.getPlayerList().get(0).getWorshipSymbol());
        Assert.assertEquals(3,mockGame.getPlayerList().get(1).getWorshipSymbol());
        Assert.assertEquals(3,mockGame.getPlayerList().get(2).getWorshipSymbol());
        Assert.assertEquals(3,mockGame.getPlayerList().get(3).getWorshipSymbol());
    }








/*
    public void processNeighbours(Point coord, Game game) {
        LinkedList<Pair<Point, Integer>> processOrder = new LinkedList<>();
        List<Point> sides = new ArrayList<>();

        Point leftNeighbour = new Point(coord.x - 1, coord.y);
        Point rightNeighbour = new Point(coord.x + 1, coord.y);
        Point upNeighbour = new Point(coord.x, coord.y - 1);
        Point downNeighbour = new Point(coord.x, coord.y + 1);

        if (validatePointAndTileType(leftNeighbour, game)) processOrder.add(new Pair(leftNeighbour, ((WorkerTile) game.getBoard().getField(leftNeighbour.x, leftNeighbour.y)).getRightWorker()));
        if (validatePointAndTileType(rightNeighbour, game)) processOrder.add(new Pair(rightNeighbour, ((WorkerTile) game.getBoard().getField(rightNeighbour.x, rightNeighbour.y)).getLeftWorker()));
        if (validatePointAndTileType(upNeighbour, game)) processOrder.add(new Pair(upNeighbour, ((WorkerTile) game.getBoard().getField(upNeighbour.x, upNeighbour.y)).getDownWorker()));
        if (validatePointAndTileType(downNeighbour, game)) processOrder.add(new Pair(downNeighbour, ((WorkerTile) game.getBoard().getField(downNeighbour.x, downNeighbour.y)).getUpWorker()));

        processOrder.forEach(side ->
                processNeighbour(side.getKey(), game, side.getValue()));
    }

 */
}
