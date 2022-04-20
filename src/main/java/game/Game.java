package game;

import board.Board;
import deck.JungleTileDeck;
import players.Player;
import server.ServerClientHandler;
import tiles.JungleTile;

import java.io.Serializable;
import java.util.*;

public class Game implements Serializable {
    private static final int MAX_NUMBER_OF_JUNGLE_TILES_AVAILABLE = 1;
    private static final int MAX_NUMBER_OF_PLAYERS = 1;      //TODO <link with Server's MAX_PLAYER_NUMBER field>

    private static final int MAX_NUMBER_OF_CACAO_BEANS = 5;
    private static final int MAX_NUMBER_OF_WORSHIP_SITES = 3;
    private static final List<Integer> WATER_POSITION_VALUE_LIST = Arrays.asList(-10, -4, -1, 0, 2, 4, 7, 11, 16);


    private List<Player> playerList;
    private Board board;
    private JungleTileDeck jungleTileDeck;
    private List<JungleTile> jungleTilesAvailable;

    private int activePlayer;
    private boolean hasPlacedWorkerTile;    //TODO: reconsider if this is needed here (already in GuiBoard with same name)
    private boolean hasPlacedJungleTile;    //TODO: reconsider if this is needed here (already in GuiBoard with same name)
    private boolean isGameEnded;


    //TODO: delete it, it is only for testing purpose
    public Game(List<Player> playerList, Board board, JungleTileDeck jungleTileDeck) {
        this.playerList = playerList;
        this.board = board;
        this.jungleTileDeck = jungleTileDeck;
        this.jungleTilesAvailable = createJungleTilesAvailable();
        this.activePlayer = 0;
        this.isGameEnded = false;
    }

    public Game(List<ServerClientHandler> clients) {
        this.playerList = createPlayerList(clients);
        this.jungleTileDeck = new JungleTileDeck(clients.size());
        this.board = new Board();

        this.activePlayer = 0;
        this.hasPlacedWorkerTile = false;
        this.hasPlacedJungleTile = false;

        this.jungleTilesAvailable = createJungleTilesAvailable();
    }

    private List<JungleTile> createJungleTilesAvailable() {
        List<JungleTile> result = new LinkedList<>();
        for (int i = 0; i < MAX_NUMBER_OF_JUNGLE_TILES_AVAILABLE; ++i){
            Optional<JungleTile> drawnCard = jungleTileDeck.drawCard();
            if (drawnCard.isPresent()) {
                result.add(drawnCard.get());
            }
        }
        return result;
    }

    private List<Player> createPlayerList(List<ServerClientHandler> clients) {
        List<Player> result = new ArrayList<>();
        List<Integer> counter = Arrays.asList(1);
        clients.forEach( c -> {
            Player newPlayer = new Player(counter.get(0), clients.size());
            result.add(newPlayer);
            counter.set(0, counter.get(0) + 1);
        });
        return result;
    }

    public void callNextPlayer(){
        activePlayer = (activePlayer + 1) % MAX_NUMBER_OF_PLAYERS;
    }
/*
    public void placeWorkerTile(WorkerTile workerTile, int xCoord, int yCoord){
        //TODO check if placement is valid

        //remove from hand
        playerList.get(activePlayer).getCardsAtHand().stream()
                .filter(card -> card.isWorkerNumbersMatchOnEachSide(workerTile))
                .findFirst();

        //playerList.get(activePlayer).getCardsAtHand().;

        //place on board
        board.setField(xCoord,yCoord, workerTile);
        //((Player)playerList.get(activePlayer)).
    }

 */

    public static int getMaxNumberOfJungleTilesAvailable() {
        return MAX_NUMBER_OF_JUNGLE_TILES_AVAILABLE;
    }

    public static int getMaxNumberOfPlayers() {
        return MAX_NUMBER_OF_PLAYERS;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public Board getBoard() {
        return board;
    }

    public JungleTileDeck getJungleTileDeck() {
        return jungleTileDeck;
    }

    public List<JungleTile> getJungleTilesAvailable() {
        return jungleTilesAvailable;
    }

    public int getActivePlayer() {
        return activePlayer;
    }

    public boolean hasPlacedWorkerTile() {
        return hasPlacedWorkerTile;
    }

    public boolean hasPlacedJungleTile() {
        return hasPlacedJungleTile;
    }

    public void setHasPlacedWorkerTile(boolean hasPlacedWorkerTile) {
        this.hasPlacedWorkerTile = hasPlacedWorkerTile;
    }

    public void setHasPlacedJungleTile(boolean hasPlacedJungleTile) {
        this.hasPlacedJungleTile = hasPlacedJungleTile;
    }

    public static int getMaxNumberOfCacaoBeans() {
        return MAX_NUMBER_OF_CACAO_BEANS;
    }

    public static int getWaterPositionValue(int index) {
        return WATER_POSITION_VALUE_LIST.get(index);
    }

    public static List<Integer> getWaterPositionValueList() {
        return WATER_POSITION_VALUE_LIST;
    }

    public static int getMaxNumberOfWorshipSites() {
        return MAX_NUMBER_OF_WORSHIP_SITES;
    }

    @Override
    public String toString() {
        return "Game{" +
                "playerList=" + playerList +
                ", board=" + board +
                ", jungleTileDeck=" + jungleTileDeck +
                ", jungleTilesAvailable=" + jungleTilesAvailable +
                ", activePlayer=" + activePlayer +
                ", hasPlacedWorkerTile=" + hasPlacedWorkerTile +
                ", hasPlacedJungleTile=" + hasPlacedJungleTile +
                ", isGameEnded=" + isGameEnded +
                '}';
    }

    public String toShortString() {
        return "Game{" +
                ", hasPlacedWorkerTile=" + hasPlacedWorkerTile +
                ", hasPlacedJungleTile=" + hasPlacedJungleTile +
                '}';
    }
}
