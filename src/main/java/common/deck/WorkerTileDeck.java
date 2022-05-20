package common.deck;

import common.players.PlayerColour;
import common.tiles.WorkerTile;

import java.io.Serializable;
import java.util.*;

//TODO: hogyan lehetne a 2 osztalyt kozos oshoz kotni, ha a List<> belso resze eltero?
public class WorkerTileDeck implements Serializable {
    private List<WorkerTile> deck;
    private PlayerColour colour;


    public WorkerTileDeck(PlayerColour colour, int numberOfPlayers) {
        this.deck = new LinkedList<>();  //new ArrayList<>();
        this.colour = colour;

        switch (numberOfPlayers) {
            case 2:
                generateTileDeck(4,5,1,1);
                break;
            case 3:
                generateTileDeck(3,5,1,1);
                break;
            case 4:
                generateTileDeck(3,4,1,1);
                break;
            default:
                //TODO clear this out, potentially with exception
                System.out.println("[WorkerTileDeck]: Invalid number of common.players, by default 2 player setup will start");
                generateTileDeck(3,5,1,1);
        }
    }

    public List<WorkerTile> getDeck() {
        return deck;
    }

    public PlayerColour getColour() {
        return colour;
    }

    public void shuffleDeck(){
        Collections.shuffle(deck);
    }

    public Optional<WorkerTile> drawCard(){
        WorkerTile result = null;
        if (deck.size() > 0) {
            result = deck.remove(0);
        }
        return Optional.ofNullable(result);
    }

    public void createTiles(int numberOfPieces, int leftWorker, int upWorker, int rightWorker, int downWorker) {
        for (int i = 0; i < numberOfPieces; ++i) {
            WorkerTile tempWorker = new WorkerTile(leftWorker,upWorker, rightWorker,downWorker, colour);
            deck.add(tempWorker);
        }
    }

    public void generateTileDeck (int w1111, int w2101, int w3001, int w3100){
        createTiles(w1111, 1,1,1,1);
        createTiles(w2101, 2,1,0,1);
        createTiles(w3001, 3,0,0,1);
        createTiles(w3100, 3,1,0,0);
        shuffleDeck();
    }


    @Override
    public String toString() {
        String topCard = deck.size()>0 ? deck.get(0).toString() : "";
        return "WorkerTileDeck{" +
                " deckSize=" + deck.size() +
                " topCard=" + topCard +
                '}';
    }

    /*
    //TODO delete, only for testing shuffle and drawCard methods
    public static void main(String[] args) {
        WorkerTileDeck common.deck = new WorkerTileDeck(PlayerColour.BLUE, 4);
        System.out.println("Before shuffle");
        common.deck.getDeck().forEach(d -> System.out.println(d.toString()));


        common.deck.shuffleDeck();
        System.out.println("After shuffle");
        common.deck.getDeck().forEach(d -> System.out.println(d.toString()));

        System.out.println("card 1");
        Optional<WorkerTile> card1 = common.deck.drawCard();
        System.out.println(card1.get());

        System.out.println("card 2");
        Optional<WorkerTile> card2 = common.deck.drawCard();
        System.out.println(card2.get());

        System.out.println("card 3");
        Optional<WorkerTile> card3 = common.deck.drawCard();
        System.out.println(card3.get());

        System.out.println("remaining common.deck");
        common.deck.getDeck().forEach(d -> System.out.println(d.toString()));


    }
    */
}
