package deck;

import players.PlayerColour;
import tiles.*;

import java.io.Serializable;
import java.util.*;

public class JungleTileDeck implements Serializable {
    public enum JungleTileEnum {
        PLANTATION_1,
        PLANTATION_2,
        MINE_1,
        MINE_2,
        WATER,
        TEMPLE,
        MARKET_LOW,
        MARKET_MID,
        MARKET_HIGH,
        WORSHIP_SITE
    }

    private List<JungleTile> deck;


    public JungleTileDeck(int numberOfPlayers) {
        this.deck = new LinkedList<>(); //new ArrayList<>();

        switch (numberOfPlayers) {
            case 2:
                twoPlayerSetup();
                break;
            case 3:
                threePlayerSetup();
                break;
            case 4:
                fourPlayerSetup();
                break;
            default:
                //TODO clear this out, potentially with exception
                System.out.println("[JungleTileDeck]: Invalid number of players, by default 2 player setup will start");
                twoPlayerSetup();
        }
    }


    public void shuffleDeck() {
        Collections.shuffle(deck);
    }

    public void createTiles(int numberOfPieces, JungleTileEnum type) {

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


    public void fourPlayerSetup() {
        //currently dummy
        createTiles(1, JungleTileEnum.PLANTATION_1);
        createTiles(1, JungleTileEnum.PLANTATION_2);
        createTiles(1, JungleTileEnum.MARKET_LOW);
        createTiles(1, JungleTileEnum.MARKET_MID);
        createTiles(1, JungleTileEnum.MARKET_HIGH);
        createTiles(1, JungleTileEnum.MINE_1);
        createTiles(1, JungleTileEnum.MINE_2);
        createTiles(1, JungleTileEnum.TEMPLE);
        createTiles(1, JungleTileEnum.WATER);
        createTiles(1, JungleTileEnum.WORSHIP_SITE);

        shuffleDeck();
    }

    public void threePlayerSetup() {
        //currently dummy
        createTiles(2, JungleTileEnum.PLANTATION_1);
        createTiles(2, JungleTileEnum.PLANTATION_2);
        createTiles(1, JungleTileEnum.MARKET_LOW);
        createTiles(1, JungleTileEnum.MARKET_MID);
        createTiles(1, JungleTileEnum.MARKET_HIGH);
        createTiles(1, JungleTileEnum.MINE_1);
        createTiles(1, JungleTileEnum.MINE_2);
        createTiles(1, JungleTileEnum.TEMPLE);
        createTiles(1, JungleTileEnum.WATER);
        createTiles(1, JungleTileEnum.WORSHIP_SITE);
        shuffleDeck();
    }

    public void twoPlayerSetup() {
        //currently dummy
        createTiles(3, JungleTileEnum.PLANTATION_1);
        createTiles(3, JungleTileEnum.PLANTATION_2);
        createTiles(2, JungleTileEnum.MARKET_LOW);
        createTiles(2, JungleTileEnum.MARKET_MID);
        createTiles(1, JungleTileEnum.MARKET_HIGH);
        createTiles(1, JungleTileEnum.MINE_1);
        createTiles(1, JungleTileEnum.MINE_2);
        createTiles(1, JungleTileEnum.TEMPLE);
        createTiles(2, JungleTileEnum.WATER);
        createTiles(1, JungleTileEnum.WORSHIP_SITE);
        shuffleDeck();
    }

    public List<JungleTile> getDeck() {
        return deck;
    }

}
