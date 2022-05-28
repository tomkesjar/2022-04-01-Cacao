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
        Board result = Board.getInstance(); //new Board();
        return result;
    }

    @Test
    public void testInitialSize() {
        underTest.resetBoardToInitialState();

        int expectedWidth = 16;
        int expectedHeight = 10;
        Assert.assertEquals(expectedHeight, underTest.getHeight());
        Assert.assertEquals(expectedWidth, underTest.getWidth());
    }

    @Test
    public void testInitialFields() {
        underTest.resetBoardToInitialState();

        JungleTile expectedUpperJungleTile = new Plantation(1);
        JungleTile expectedLowerJungleTile = new Market(Market.MarketPrice.LOW);
        Assert.assertEquals(expectedUpperJungleTile, underTest.getField(7, 4));
        Assert.assertEquals(expectedLowerJungleTile, underTest.getField(8, 5));
    }

    @Test
    public void testSelectablePositionAtStart() {
        underTest.resetBoardToInitialState();

        underTest.selectPossibleWorkerAndJungleTilesForPlacement();
        Assert.assertEquals(6, underTest.getSelectableWorkerPanelPositions().size());
        Assert.assertEquals(0, underTest.getSelectableJunglePanelPositions().size());
    }

    @Test
    public void testisValidPlacementAsWorkerTile() {
        underTest.resetBoardToInitialState();

        Assert.assertTrue(underTest.isValidPlacementAsWorkerTile(7, 5));
        Assert.assertFalse(underTest.isValidPlacementAsWorkerTile(5, 5));
    }

    @Test
    public void testisValidPlacementAsJungleTile() {

        underTest.resetBoardToInitialState();
        underTest.setField(7, 5, new WorkerTile(1, 1, 1, 1, PlayerColour.RED));

        Assert.assertTrue(underTest.isValidPlacementAsJungleTile(7, 6));
        Assert.assertTrue(underTest.isValidPlacementAsJungleTile(6, 5));

        Assert.assertFalse(underTest.isValidPlacementAsJungleTile(4, 5));
    }

    @Test
    public void testSelectablePositionAfterWorkerTilePlacement() {
        underTest.resetBoardToInitialState();

        Assert.assertTrue(underTest.isValidPlacementAsWorkerTile(7, 5));
        Assert.assertFalse(underTest.isValidPlacementAsWorkerTile(5, 5));

        underTest.setField(7, 5, new WorkerTile(1, 1, 1, 1, PlayerColour.RED));

        underTest.selectPossibleWorkerAndJungleTilesForPlacement();
        Assert.assertEquals(5, underTest.getSelectableWorkerPanelPositions().size());
        Assert.assertEquals(2, underTest.getSelectableJunglePanelPositions().size());
    }

    @Test
    public void testSelectablePositionAfterJungleTilePlacement() {
        underTest.resetBoardToInitialState();
        underTest.setField(7, 5, new WorkerTile(1, 1, 1, 1, PlayerColour.RED));

        Assert.assertTrue(underTest.isValidPlacementAsJungleTile(6, 5));
        Assert.assertTrue(underTest.isValidPlacementAsJungleTile(7, 6));

        underTest.setField(6, 5, new Temple());

        underTest.selectPossibleWorkerAndJungleTilesForPlacement();
        Assert.assertEquals(7, underTest.getSelectableWorkerPanelPositions().size());
        Assert.assertEquals(1, underTest.getSelectableJunglePanelPositions().size());
    }

    @Test
    public void testisValidPlacementAsWorkerTileWhenWorshipSymbolIsUsed() {
        underTest.resetBoardToInitialState();

        underTest.setField(7, 5, new WorkerTile(1, 1, 1, 1, PlayerColour.RED));

        Player activePlayer = new Player.PlayerBuilder()
                .setPlayerColour(PlayerColour.RED)
                .build();
        activePlayer.setWorshipSymbol(2);

        Assert.assertFalse(underTest.isValidPlacementAsWorkerTile(7, 5));
        Assert.assertTrue(underTest.isValidPlacementAsWorkerTileWhenWorshipSymbolIsUsed(7, 5, activePlayer));
    }
}
