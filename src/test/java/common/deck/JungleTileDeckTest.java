package common.deck;

import common.players.PlayerColour;
import common.tiles.JungleTile;
import common.tiles.WorkerTile;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class JungleTileDeckTest {

    @Test
    public void testWorkerTileDeckWithFourPlayers() {
        JungleTileDeck deck = new JungleTileDeck(4);

        int expectedSize = 28;
        Assert.assertEquals(expectedSize, deck.getDeck().size());
    }

    @Test
    public void testWorkerTileDeckWithTwoPlayers() {
        JungleTileDeck deck = new JungleTileDeck(2);

        int expectedSize = 20;
        Assert.assertEquals(expectedSize, deck.getDeck().size());
    }




    @Test
    public void testDrawCardMethodWhenValueIsEmpty() {
        JungleTileDeck deck = new JungleTileDeck( 2);

        while(deck.getDeck().size() > 0) {
            Optional<JungleTile> tempJungleTile = deck.drawCard();
        }

        Assert.assertEquals(0, deck.getDeck().size());

        Optional<JungleTile> cardDrawnWhenValueIsEmpty = deck.drawCard();
        Assert.assertFalse(cardDrawnWhenValueIsEmpty.isPresent());
    }

    @Test
    public void testDrawCardMethodWhenValueIsNotEmpty() {
        JungleTileDeck deck = new JungleTileDeck( 2);

        JungleTile topCardWhenValueIsNotEmpty = deck.getDeck().get(0);
        Optional<JungleTile> cardDrawnWhenValueIsNotEmpty = deck.drawCard();

        int expectedSizeAfterCardDraw = 19;
        Assert.assertEquals(expectedSizeAfterCardDraw, deck.getDeck().size());
        Assert.assertTrue(cardDrawnWhenValueIsNotEmpty.isPresent());
        Assert.assertEquals(topCardWhenValueIsNotEmpty, cardDrawnWhenValueIsNotEmpty.get());
    }
}
