package common.board;

import common.players.Player;
import common.players.PlayerColour;
import common.tiles.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BoardTest {

    private Board underTest;

    @Before
    public void setup() {
        underTest = createTestBoard();
    }

    private Board createTestBoard() {
        Board result = new Board();
        return result;
    }

    @Test
    public void testInitialSize() {
        Board board = new Board();

        int expectedWidth = 16;
        int expectedHeight = 10;
        Assert.assertEquals(expectedHeight, board.getHeight());
        Assert.assertEquals(expectedWidth, board.getWidth());
    }

    @Test
    public void testInitialFields() {
        Board board = new Board();

        JungleTile expectedUpperJungleTile = new Plantation(1);
        JungleTile expectedLowerJungleTile = new Market(Market.MarketPrice.LOW);
        Assert.assertEquals(expectedUpperJungleTile, board.getField(7, 4));
        Assert.assertEquals(expectedLowerJungleTile, board.getField(8, 5));
    }

    @Test
    public void testSelectablePositionAtStart() {
        Board board = new Board();

        board.selectPossibleWorkerAndJungleTilesForPlacement();
        Assert.assertEquals(6, board.getSelectableWorkerPanelPositions().size());
        Assert.assertEquals(0, board.getSelectableJunglePanelPositions().size());
    }

    @Test
    public void testisValidPlacementAsWorkerTile() {
        Board board = new Board();

        Assert.assertTrue(board.isValidPlacementAsWorkerTile(7, 5));
        Assert.assertFalse(board.isValidPlacementAsWorkerTile(5, 5));
    }

    @Test
    public void testisValidPlacementAsJungleTile() {
        Board board = new Board();
        board.setField(7, 5, new WorkerTile(1, 1, 1, 1, PlayerColour.RED));

        Assert.assertTrue(board.isValidPlacementAsJungleTile(7, 6));
        Assert.assertTrue(board.isValidPlacementAsJungleTile(6, 5));

        Assert.assertFalse(board.isValidPlacementAsJungleTile(4, 5));
    }

    @Test
    public void testSelectablePositionAfterWorkerTilePlacement() {
        Board board = new Board();

        Assert.assertTrue(board.isValidPlacementAsWorkerTile(7, 5));
        Assert.assertFalse(board.isValidPlacementAsWorkerTile(5, 5));

        board.setField(7, 5, new WorkerTile(1, 1, 1, 1, PlayerColour.RED));

        board.selectPossibleWorkerAndJungleTilesForPlacement();
        Assert.assertEquals(5, board.getSelectableWorkerPanelPositions().size());
        Assert.assertEquals(2, board.getSelectableJunglePanelPositions().size());
    }

    @Test
    public void testSelectablePositionAfterJungleTilePlacement() {
        Board board = new Board();
        board.setField(7, 5, new WorkerTile(1, 1, 1, 1, PlayerColour.RED));

        Assert.assertTrue(board.isValidPlacementAsJungleTile(6, 5));
        Assert.assertTrue(board.isValidPlacementAsJungleTile(7, 6));

        board.setField(6, 5, new Temple());

        board.selectPossibleWorkerAndJungleTilesForPlacement();
        Assert.assertEquals(7, board.getSelectableWorkerPanelPositions().size());
        Assert.assertEquals(1, board.getSelectableJunglePanelPositions().size());
    }

    @Test
    public void testisValidPlacementAsWorkerTileWhenWorshipSymbolIsUsed() {
        Board board = new Board();
        board.setField(7, 5, new WorkerTile(1, 1, 1, 1, PlayerColour.RED));

        Player activePlayer = new Player.PlayerBuilder()
                .setPlayerColour(PlayerColour.RED)
                .build();
        activePlayer.setWorshipSymbol(2);

        Assert.assertFalse(board.isValidPlacementAsWorkerTile(7, 5));
        Assert.assertTrue(board.isValidPlacementAsWorkerTileWhenWorshipSymbolIsUsed(7, 5, activePlayer));
    }
}
