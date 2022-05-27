package common.player;

import common.board.Board;
import common.game.Game;
import common.game.GameHandler;
import common.messages.Pair;
import common.players.Player;
import common.players.PlayerColour;
import common.tiles.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public class PlayerTest {
    private GameHandler mockGameHandler;
    private Game mockGame;
    private Board board;
    private Player underTest;

    @Before
    public void setup(){
        board = createBoard();
        underTest = createPlayer();
        mockGame = createMockGame();
        mockGameHandler = createMockGameHandler();

    }

    private Player createPlayer() {
        List<WorkerTile> cardsAtHand = Arrays.asList(
                new WorkerTile(1,1,1,1, PlayerColour.RED),
                new WorkerTile(1,2,1,0, PlayerColour.RED),
                new WorkerTile(3,1,0,0, PlayerColour.RED)
                );

        return new Player.PlayerBuilder()
                .setPoint(0)
                .setCoins(0)
                .setNumberOfCacaoBean(0)
                .setWaterPoint(0)
                .setPlayerColour(PlayerColour.RED)
                .setTemplePoint(0)
                .setTemplePointBonus(0)
                .setRank(1)
                .setWorshipSymbol(0)
                .setCardsAtHand(cardsAtHand)
                .build();
    }

    private Board createBoard(){
        Board board = Board.getInstance(); //new Board();
        return board;
    }

    private Game createMockGame(){
        Game mockGame = Mockito.mock(Game.class);
        when(mockGame.getBoard()).thenReturn(board);

        List<Player> playerList = new ArrayList<>();
        playerList.add(underTest);

        when(mockGame.getPlayerList()).thenReturn(playerList);
        when(mockGame.getActivePlayer()).thenReturn(0);

        when(mockGame.isFieldValid(anyInt(),anyInt())).thenReturn(true);

        List<JungleTile> jungleTiles = Arrays.asList(new Plantation(2), new Temple());
        when(mockGame.getJungleTilesAvailable()).thenReturn(jungleTiles);

        return mockGame;
    }

    private GameHandler createMockGameHandler() {
        GameHandler result = Mockito.mock(GameHandler.class);
        when(result.getGame()).thenReturn(mockGame);

        return result;
    }

    @Test
    public void testEqualRank(){
        Player player1 = new Player.PlayerBuilder()
                .setPlayerColour(PlayerColour.RED)
                .setPoint(5)
                .setNumberOfCacaoBean(2)
                .build();

        Player player2 = new Player.PlayerBuilder()
                .setPlayerColour(PlayerColour.RED)
                .setPoint(6)
                .setNumberOfCacaoBean(2)
                .build();

        Player player3 = new Player.PlayerBuilder()
                .setPlayerColour(PlayerColour.RED)
                .setPoint(5)
                .setNumberOfCacaoBean(3)
                .build();

        Player player4 = new Player.PlayerBuilder()
                .setPlayerColour(PlayerColour.RED)
                .setPoint(5)
                .setNumberOfCacaoBean(2)
                .build();

        Assert.assertFalse(player1.isEqualRank(player2));
        Assert.assertFalse(player1.isEqualRank(player3));
        Assert.assertTrue(player1.isEqualRank(player4));

    }

    @Test
    public void testPlaceBasicWorkerTile(){
        board.selectPossibleWorkerAndJungleTilesForPlacement();
        List<Pair<Integer, Integer>> initialSelectableWorkerTiles = board.getSelectableWorkerPanelPositions();
        List<Pair<Integer, Integer>> initialSelectableJungleTiles = board.getSelectableJunglePanelPositions();

        underTest.placeBasicWorkerTile(mockGameHandler);
        board.selectPossibleWorkerAndJungleTilesForPlacement();
        List<Pair<Integer, Integer>> selectableWorkerTilesAfterPlacement = board.getSelectableWorkerPanelPositions();
        List<Pair<Integer, Integer>> selectableJungleTilesAfterPlacement = board.getSelectableJunglePanelPositions();

        Assert.assertTrue(selectableJungleTilesAfterPlacement.size() > 0);
        Assert.assertTrue(initialSelectableWorkerTiles.size() > selectableWorkerTilesAfterPlacement.size());
    }

    @Test
    public void testPlaceBasicJungleTile(){
        board.setField(7,5, new WorkerTile(1,1,1,1,PlayerColour.RED));
        board.selectPossibleWorkerAndJungleTilesForPlacement();
        List<Pair<Integer, Integer>> initialSelectableWorkerTiles = board.getSelectableWorkerPanelPositions();
        List<Pair<Integer, Integer>> initialSelectableJungleTiles = board.getSelectableJunglePanelPositions();

        underTest.placeBasicJungleTile(mockGameHandler);
        board.selectPossibleWorkerAndJungleTilesForPlacement();
        List<Pair<Integer, Integer>> selectableWorkerTilesAfterPlacement = board.getSelectableWorkerPanelPositions();
        List<Pair<Integer, Integer>> selectableJungleTilesAfterPlacement = board.getSelectableJunglePanelPositions();

        Assert.assertTrue(selectableJungleTilesAfterPlacement.size() == 1);
        Assert.assertTrue(7 == selectableWorkerTilesAfterPlacement.size());
    }


}
