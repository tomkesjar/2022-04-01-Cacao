package deck;

import players.PlayerColour;
import tiles.WorkerTile;

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
                System.out.println("[WorkerTileDeck]: Invalid number of players, by default 2 player setup will start");
                twoPlayerSetup();
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

    public void createTiles(int numberOfPieces, int rightWorker, int upWorker, int leftWorker, int downWorker) {
        for (int i = 0; i < numberOfPieces; ++i) {
            WorkerTile tempWorker = new WorkerTile(rightWorker,upWorker, leftWorker,downWorker, colour);
            deck.add(tempWorker);
        }
    }



    public void fourPlayerSetup() {
        //currently dummy
        createTiles(1, 1,1,1,1);
        createTiles(1, 1,2,1,0);
        createTiles(1, 3,1,0,0);
        createTiles(1, 1,3,0,0);
        shuffleDeck();
    }

    public void threePlayerSetup() {
        //currently dummy
        createTiles(2, 1,1,1,1);
        createTiles(1, 1,2,1,0);
        createTiles(1, 3,1,0,0);
        createTiles(1, 1,3,0,0);
        shuffleDeck();
    }

    public void twoPlayerSetup() {
        //currently dummy
        createTiles(2, 1,1,1,1);
        createTiles(2, 1,2,1,0);
        createTiles(1, 3,1,0,0);
        createTiles(1, 1,3,0,0);
        shuffleDeck();
    }

    /*
    //TODO delete, only for testing shuffle and drawCard methods
    public static void main(String[] args) {
        WorkerTileDeck deck = new WorkerTileDeck(PlayerColour.BLUE, 4);
        System.out.println("Before shuffle");
        deck.getDeck().forEach(d -> System.out.println(d.toString()));


        deck.shuffleDeck();
        System.out.println("After shuffle");
        deck.getDeck().forEach(d -> System.out.println(d.toString()));

        System.out.println("card 1");
        Optional<WorkerTile> card1 = deck.drawCard();
        System.out.println(card1.get());

        System.out.println("card 2");
        Optional<WorkerTile> card2 = deck.drawCard();
        System.out.println(card2.get());

        System.out.println("card 3");
        Optional<WorkerTile> card3 = deck.drawCard();
        System.out.println(card3.get());

        System.out.println("remaining deck");
        deck.getDeck().forEach(d -> System.out.println(d.toString()));


    }
    */
}
