package common.deck;

import common.tiles.*;

import java.io.Serializable;
import java.util.*;

public class JungleTileDeck implements Serializable {

    private List<JungleTile> deck;
    private final int initialJungleDeckSize;


    public JungleTileDeck(int numberOfPlayers) {
        this.deck = new LinkedList<>();

        switch (numberOfPlayers) {
            case 2:
                twoPlayersSetup();
                break;
            case 3:
                moreThanTwoPlayersSetup();
                break;
            case 4:
                moreThanTwoPlayersSetup();
                break;
            default:
                System.out.println("[JungleTileDeck]: Invalid number of common.players, by default 2 player setup will start");
                twoPlayersSetup();
        }

        initialJungleDeckSize = deck.size();
    }


    public void shuffleDeck() {
        Collections.shuffle(deck);
    }

    public void createTiles(int numberOfPieces, TileEnum type) {

        for (int i = 0; i < numberOfPieces; ++i) {
            switch (type) {
                case PLANTATION_1:
                    deck.add(new Plantation(1));
                    break;
                case PLANTATION_2:
                    deck.add(new Plantation(2));
                    break;
                case MINE_1:
                    deck.add(new Mine(1));
                    break;
                case MINE_2:
                    deck.add(new Mine(2));
                    break;
                case WATER:
                    deck.add(new Water());
                    break;
                case TEMPLE:
                    deck.add(new Temple());
                    break;
                case MARKET_LOW:
                    deck.add(new Market(Market.MarketPrice.LOW));
                    break;
                case MARKET_MID:
                    deck.add(new Market(Market.MarketPrice.MID));
                    break;
                case MARKET_HIGH:
                    deck.add(new Market(Market.MarketPrice.HIGH));
                    break;
                case WORSHIP_SITE:
                    deck.add(new WorshipSite());
                    break;
            }
        }
    }

    public Optional<JungleTile> drawCard(){
        JungleTile result = null;
        if (deck.size() > 0) {
            result = deck.remove(0);
        }
        return Optional.ofNullable(result);
    }


    public void moreThanTwoPlayersSetup() {
        createTiles(6, TileEnum.PLANTATION_1);
        createTiles(2, TileEnum.PLANTATION_2);
        createTiles(2, TileEnum.MARKET_LOW);
        createTiles(4, TileEnum.MARKET_MID);
        createTiles(1, TileEnum.MARKET_HIGH);
        createTiles(2, TileEnum.MINE_1);
        createTiles(1, TileEnum.MINE_2);
        createTiles(3, TileEnum.WATER);
        createTiles(5, TileEnum.TEMPLE);
        createTiles(2, TileEnum.WORSHIP_SITE);
        shuffleDeck();
    }

    public void twoPlayersSetup() {

        createTiles(4, TileEnum.PLANTATION_1);  //-2
        createTiles(2, TileEnum.PLANTATION_2);
        createTiles(2, TileEnum.MARKET_LOW);
        createTiles(3, TileEnum.MARKET_MID);    //-1
        createTiles(1, TileEnum.MARKET_HIGH);
        createTiles(1, TileEnum.MINE_1);        //-1
        createTiles(1, TileEnum.MINE_2);
        createTiles(2, TileEnum.WATER);         //-1
        createTiles(3, TileEnum.TEMPLE);        //-2
        createTiles(1, TileEnum.WORSHIP_SITE);  //-1
        shuffleDeck();
    }

    public List<JungleTile> getDeck() {
        return deck;
    }

    public int getInitialJungleDeckSize() {
        return initialJungleDeckSize;
    }

    @Override
    public String toString() {
        String topCard = deck.size()>0 ? deck.get(0).toString() : "";
        return "JungleTileDeck{" +
                " deckSize=" + deck.size() +
                " topCard=" + topCard +
                '}';
    }

}
