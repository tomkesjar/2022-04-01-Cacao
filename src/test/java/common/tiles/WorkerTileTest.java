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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WorkerTileTest {

    private Game mockGame;
    private Board mockBoard;
    private WorkerTile underTest;

    @Before
    public void setup(){
        underTest = new WorkerTile(1,2,1,0, PlayerColour.RED);
        mockBoard = createMockBoard();
        mockGame = createMockGame();

    }

    private Board createMockBoard(){
        Board mockBoard = Mockito.mock(Board.class);
        when(mockBoard.getWidth()).thenReturn(16);
        when(mockBoard.getHeight()).thenReturn(10);

        when(mockBoard.getField(1,2)).thenReturn(new Temple());
        when(mockBoard.getField(2,1)).thenReturn(new Plantation(2));
        when(mockBoard.getField(3,2)).thenReturn(new Market(Market.MarketPrice.MID));
        when(mockBoard.getField(2,3)).thenReturn(new WorshipSite());
        when(mockBoard.getField(2,2)).thenReturn(underTest);

        return mockBoard;
    }

    private Game createMockGame(){
        Game mockGame = Mockito.mock(Game.class);
        when(mockGame.getBoard()).thenReturn(mockBoard);

        List<Player> playerList = new ArrayList<>();
        Player dummyPlayer = new Player.PlayerBuilder()
                .setPlayerColour(PlayerColour.RED)
                .setCoins(0)
                .setNumberOfCacaoBean(0)
                .setTemplePoint(0)
                .setTemplePointBonus(0)
                .setWorshipSymbol(0)
                .build();
        playerList.add(dummyPlayer);
        when(mockGame.getPlayerList()).thenReturn(playerList);
        when(mockGame.getActivePlayer()).thenReturn(0);

        when(mockGame.isFieldValid(1,2)).thenReturn(true);
        when(mockGame.isFieldValid(2,1)).thenReturn(true);
        when(mockGame.isFieldValid(3,2)).thenReturn(true);
        when(mockGame.isFieldValid(2,3)).thenReturn(true);

        return mockGame;
    }


    @Test
    public void testRotateMethodWhenRotatedOnce(){
        WorkerTile workerTile = new WorkerTile(3,1,0,0, PlayerColour.RED);

        for(int i=0; i<1; ++i){
            workerTile.turnRightWorkersNinetyDegrees();
        }

        Assert.assertEquals(0, workerTile.getLeftWorker());
        Assert.assertEquals(3, workerTile.getUpWorker());
        Assert.assertEquals(1, workerTile.getRightWorker());
        Assert.assertEquals(0, workerTile.getDownWorker());
        Assert.assertEquals(1, workerTile.getNumberOfRotation());
    }

    @Test
    public void testRotateMethodWhenRotatedTwice(){
        WorkerTile workerTile = new WorkerTile(3,1,0,0, PlayerColour.RED);

        for(int i=0; i<2; ++i){
            workerTile.turnRightWorkersNinetyDegrees();
        }

        Assert.assertEquals(0, workerTile.getLeftWorker());
        Assert.assertEquals(0, workerTile.getUpWorker());
        Assert.assertEquals(3, workerTile.getRightWorker());
        Assert.assertEquals(1, workerTile.getDownWorker());
        Assert.assertEquals(2, workerTile.getNumberOfRotation());
    }

    @Test
    public void testRotateMethodWhenRotatedThreeTimes(){
        WorkerTile workerTile = new WorkerTile(3,1,0,0, PlayerColour.RED);

        for(int i=0; i<3; ++i){
            workerTile.turnRightWorkersNinetyDegrees();
        }

        Assert.assertEquals(1, workerTile.getLeftWorker());
        Assert.assertEquals(0, workerTile.getUpWorker());
        Assert.assertEquals(0, workerTile.getRightWorker());
        Assert.assertEquals(3, workerTile.getDownWorker());
        Assert.assertEquals(3, workerTile.getNumberOfRotation());
    }

    @Test
    public void testRotateMethodWhenRotatedFourTimes(){
        WorkerTile workerTile = new WorkerTile(3,1,0,0, PlayerColour.RED);

        for(int i=0; i<4; ++i){
            workerTile.turnRightWorkersNinetyDegrees();
        }

        Assert.assertEquals(3, workerTile.getLeftWorker());
        Assert.assertEquals(1, workerTile.getUpWorker());
        Assert.assertEquals(0, workerTile.getRightWorker());
        Assert.assertEquals(0, workerTile.getDownWorker());
        Assert.assertEquals(0, workerTile.getNumberOfRotation());
    }

    @Test
    public void testRotateMethodWhenRotatedFiveTimes(){
        WorkerTile workerTile = new WorkerTile(3,1,0,0, PlayerColour.RED);

        for(int i=0; i<5; ++i){
            workerTile.turnRightWorkersNinetyDegrees();
        }

        Assert.assertEquals(0, workerTile.getLeftWorker());
        Assert.assertEquals(3, workerTile.getUpWorker());
        Assert.assertEquals(1, workerTile.getRightWorker());
        Assert.assertEquals(0, workerTile.getDownWorker());
        Assert.assertEquals(1, workerTile.getNumberOfRotation());
    }


    @Test
    public void testEqualsMethodWhenTilesAreIdenticals(){
        WorkerTile workerTile1 = new WorkerTile(3,1,0,0, PlayerColour.RED);
        WorkerTile workerTile2 = new WorkerTile(0,3,1,0, PlayerColour.RED);
        WorkerTile workerTile3 = new WorkerTile(0,0,3,1, PlayerColour.RED);
        WorkerTile workerTile4 = new WorkerTile(1,0,0,3, PlayerColour.RED);

        Assert.assertTrue(workerTile1.equals(workerTile2));
        Assert.assertTrue(workerTile1.equals(workerTile3));
        Assert.assertTrue(workerTile1.equals(workerTile4));

    }

    @Test
    public void testEqualsMethodWhenTilesAreDifferent(){
        WorkerTile workerTile1 = new WorkerTile(3,1,0,0, PlayerColour.RED);
        WorkerTile workerTile2 = new WorkerTile(0,3,1,0, PlayerColour.BLUE);
        WorkerTile workerTile3 = new WorkerTile(0,1,3,0, PlayerColour.RED);
        WorkerTile workerTile4 = new WorkerTile(1,2,1,0, PlayerColour.RED);

        Assert.assertFalse(workerTile1.equals(workerTile2));
        Assert.assertFalse(workerTile1.equals(workerTile3));
        Assert.assertFalse(workerTile1.equals(workerTile4));

    }


    //process
    @Test
    public void testProcessNeighbours(){
        underTest.processNeighbours(new Point(2,2),mockGame);
        Assert.assertEquals(1,mockGame.getPlayerList().get(mockGame.getActivePlayer()).getTemplePoint());
        Assert.assertEquals(3,mockGame.getPlayerList().get(mockGame.getActivePlayer()).getNumberOfCacaoBean());
        Assert.assertEquals(3,mockGame.getPlayerList().get(mockGame.getActivePlayer()).getCoins());
    }

}
