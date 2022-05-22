package common.deck;

import common.players.PlayerColour;
import common.tiles.WorkerTile;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;


public class WorkerTileDeckTest {

    @Test
    public void testWorkerTileDeckWithFourPlayers() {
        WorkerTileDeck deck = new WorkerTileDeck(PlayerColour.BLUE, 4);

        int expectedSize = 9;
        PlayerColour expectedColour = PlayerColour.BLUE;
        Assert.assertEquals(expectedSize, deck.getDeck().size());
        Assert.assertEquals(expectedColour, deck.getColour());
    }

    @Test
    public void testWorkerTileDeckWithThreePlayers() {
        WorkerTileDeck deck = new WorkerTileDeck(PlayerColour.BLUE, 3);

        int expectedSize = 10;
        PlayerColour expectedColour = PlayerColour.BLUE;
        Assert.assertEquals(expectedSize, deck.getDeck().size());
        Assert.assertEquals(expectedColour, deck.getColour());
    }

    @Test
    public void testWorkerTileDeckWithTwoPlayers() {
        WorkerTileDeck deck = new WorkerTileDeck(PlayerColour.BLUE, 2);

        int expectedSize = 11;
        PlayerColour expectedColour = PlayerColour.BLUE;
        Assert.assertEquals(expectedSize, deck.getDeck().size());
        Assert.assertEquals(expectedColour, deck.getColour());
    }

    @Test
    public void testDrawCardMethodWhenValueIsEmpty() {
        WorkerTileDeck deck = new WorkerTileDeck(PlayerColour.BLUE, 2);
        PlayerColour expectedColour = PlayerColour.BLUE;


        while(deck.getDeck().size() > 0) {
            Optional<WorkerTile> tempWorkerTile = deck.drawCard();
        }

        Assert.assertEquals(expectedColour, deck.getColour());
        Assert.assertEquals(0, deck.getDeck().size());

        Optional<WorkerTile> cardDrawnWhenValueIsEmpty = deck.drawCard();
        Assert.assertFalse(cardDrawnWhenValueIsEmpty.isPresent());
    }

    @Test
    public void testDrawCardMethodWhenValueIsNotEmpty() {
        WorkerTileDeck deck = new WorkerTileDeck(PlayerColour.BLUE, 2);


        PlayerColour expectedColour = PlayerColour.BLUE;

        Assert.assertEquals(expectedColour, deck.getColour());

        WorkerTile topCardWhenValueIsNotEmpty = deck.getDeck().get(0);
        Optional<WorkerTile> cardDrawnWhenValueIsNotEmpty = deck.drawCard();

        int expectedSizeAfterCardDraw = 10;
        Assert.assertEquals(expectedSizeAfterCardDraw, deck.getDeck().size());
        Assert.assertTrue(cardDrawnWhenValueIsNotEmpty.isPresent());
        Assert.assertEquals(topCardWhenValueIsNotEmpty, cardDrawnWhenValueIsNotEmpty.get());
    }
}
