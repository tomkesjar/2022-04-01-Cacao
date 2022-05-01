package deck;

import tiles.*;

import java.io.Serializable;
import java.util.*;

public class JungleTileDeck implements Serializable {

    private List<JungleTile> deck;


    public JungleTileDeck(int numberOfPlayers) {
        this.deck = new LinkedList<>(); //new ArrayList<>();

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
                //TODO clear this out, potentially with exception
                System.out.println("[JungleTileDeck]: Invalid number of players, by default 2 player setup will start");
                twoPlayersSetup();
        }
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
        //currently dummy
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
        //currently dummy
        createTiles(4, TileEnum.PLANTATION_1);
        createTiles(2, TileEnum.PLANTATION_2);
        createTiles(2, TileEnum.MARKET_LOW);
        createTiles(3, TileEnum.MARKET_MID);
        createTiles(1, TileEnum.MARKET_HIGH);
        createTiles(1, TileEnum.MINE_1);
        createTiles(1, TileEnum.MINE_2);
        createTiles(2, TileEnum.WATER);
        createTiles(3, TileEnum.TEMPLE);
        createTiles(1, TileEnum.WORSHIP_SITE);
        shuffleDeck();
    }

    public List<JungleTile> getDeck() {
        return deck;
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
