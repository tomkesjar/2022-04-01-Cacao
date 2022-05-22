package common.game;

import common.board.Board;
import common.players.Player;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class GameTest {

    private Game underTest;

    @Before
    public void setup(){
        underTest = createGame();
    }

    private Game createGame() {
        List<String> names = Arrays.asList("Joe", "Bot", "Bot2", "Bot3");
        Game result = new Game(names, 1);
        return result;
    }

    @Test
    public void testisFieldValidWhenInvalid(){
        Assert.assertFalse(underTest.isFieldValid(-1, 2));
        Assert.assertFalse(underTest.isFieldValid(17, 2));
        Assert.assertFalse(underTest.isFieldValid(2, -1));
        Assert.assertFalse(underTest.isFieldValid(2, 11));
    }

    @Test
    public void testisFieldValidWhenValid(){
        Assert.assertTrue(underTest.isFieldValid(0, 2));
        Assert.assertTrue(underTest.isFieldValid(15, 2));
        Assert.assertTrue(underTest.isFieldValid(2, 0));
        Assert.assertTrue(underTest.isFieldValid(2, 9));
    }


    @Test
    public void testPlayerRanking() {
        List<Player> playerList = new ArrayList<>();

        Player player1 = new Player(1, 4, "AAA");
        Player player2 = new Player(2, 4, "BBB");
        Player player3 = new Player(3, 4, "CCC");
        Player player4 = new Player(4, 4, "DDD");


        player3.setPoint(3);
        player3.setNumberOfCacaoBean(3);

        player4.setPoint(3);
        player4.setNumberOfCacaoBean(4);

        player2.setPoint(6);
        player2.setNumberOfCacaoBean(3);

        player1.setPoint(5);
        player1.setNumberOfCacaoBean(3);

        playerList.add(player1);
        playerList.add(player2);
        playerList.add(player3);
        playerList.add(player4);

        playerList.sort(Comparator.comparing(Player::getPoint).thenComparingInt(Player::getNumberOfCacaoBean).reversed());

        Assert.assertEquals(player2, playerList.get(0));
        Assert.assertEquals(player1, playerList.get(1));
        Assert.assertEquals(player4, playerList.get(2));
        Assert.assertEquals(player3, playerList.get(3));
    }

    @Test
    public void testCallNextPlayerWhenFirstPlayer(){
        Assert.assertEquals(0, underTest.getActivePlayer());
        underTest.callNextPlayer();

        Assert.assertEquals(1, underTest.getActivePlayer());
    }

    @Test
    public void testCallNextPlayerWhenLastPlayer(){
        Assert.assertEquals(0, underTest.getActivePlayer());
        for (int i=0; i<underTest.getPlayerList().size()-1; ++i) {
            underTest.callNextPlayer();
        }

        Assert.assertEquals(3, underTest.getActivePlayer());
        underTest.callNextPlayer();
        Assert.assertEquals(0, underTest.getActivePlayer());

    }
}
