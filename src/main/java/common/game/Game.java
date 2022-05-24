package common.game;

import common.board.Board;
import common.deck.JungleTileDeck;
//import javafx.util.common.messages.Pair;
import common.messages.Pair;
import common.players.Player;
import server.handlers.GameServerClientHandler;
import common.tiles.JungleTile;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Game implements Serializable {
    private static final int MAX_NUMBER_OF_JUNGLE_TILES_AVAILABLE = 2;
    private static int MAX_NUMBER_OF_PLAYERS;

    private static final int MAX_NUMBER_OF_CACAO_BEANS = 5;
    private static final int MAX_NUMBER_OF_WORSHIP_SITES = 3;
    private static final List<Integer> WATER_POSITION_VALUE_LIST = Arrays.asList(-10, -4, -1, 0, 2, 4, 7, 11, 16);


    private List<Player> playerList;
    private Board board;
    private JungleTileDeck jungleTileDeck;
    private List<JungleTile> jungleTilesAvailable;

    private int activePlayer;
    private boolean hasPlacedWorkerTile;
    private boolean hasPlacedJungleTile;
    private boolean isGameEnded;




    public Game(List<GameServerClientHandler> clients) {
        this(clients.stream().map(client -> client.getPlayerName()).collect(Collectors.toList()), clients.size());
    }

    public Game(List<String> nameList, int numberOfHumanPlayers) {
        this.MAX_NUMBER_OF_PLAYERS = nameList.size();
        this.playerList = createPlayerList(nameList, numberOfHumanPlayers);
        this.jungleTileDeck = new JungleTileDeck(nameList.size());
        this.board = new Board();

        this.activePlayer = 0;
        this.hasPlacedWorkerTile = false;
        this.hasPlacedJungleTile = false;

        this.jungleTilesAvailable = createJungleTilesAvailable();
    }

    private List<JungleTile> createJungleTilesAvailable() {
        List<JungleTile> result = new LinkedList<>();
        for (int i = 0; i < MAX_NUMBER_OF_JUNGLE_TILES_AVAILABLE; ++i) {
            Optional<JungleTile> drawnCard = jungleTileDeck.drawCard();
            if (drawnCard.isPresent()) {
                result.add(drawnCard.get());
            }
        }
        return result;
    }

    private List<Player> createPlayerList(List<String> clients, int numberOfHumanPlayers) {
        List<Player> result = new ArrayList<>();
        List<Integer> counter = Arrays.asList(1);
        clients.forEach(c -> {
            Player newPlayer = new Player(counter.get(0), clients.size(), c);
            if (numberOfHumanPlayers >= counter.get(0)) {
                newPlayer.setPlayerType(Player.PlayerType.HUMAN);
            }else{
                newPlayer.setPlayerType(Player.PlayerType.BASIC_AI);
            }
            result.add(newPlayer);
            counter.set(0, counter.get(0) + 1);
        });
        return result;
    }

    public void callNextPlayer() {
        activePlayer = (activePlayer + 1) % MAX_NUMBER_OF_PLAYERS;
    }

    public boolean checkIfIsGameEnd() {
        //last player, runs out worker or jungle tile
        boolean isNoMoreJungleTile = false;
        boolean isOnlyOneWorkerTileForLastPlayer = false;

        int activePlayerIndex = this.getActivePlayer();
        Player activePlayer = this.getPlayerList().get(activePlayerIndex);


        if (this.getJungleTileDeck().getDeck().size() == 0 && this.getJungleTilesAvailable().size() == 0) {
            isNoMoreJungleTile = true;
        }
        if (activePlayer.getWorkerTileDeck().getDeck().size() == 0 && activePlayer.getCardsAtHand().size() == 1) {
            isOnlyOneWorkerTileForLastPlayer = true;
        }

        return isNoMoreJungleTile || isOnlyOneWorkerTileForLastPlayer;

    }


    public void calculatePoints() {
        Pair<Optional<Integer>, Optional<Integer>> maxValues = calculateTempleBonuses();

        for (Player player : playerList) {
            if (maxValues.getKey().isPresent() && maxValues.getKey().get() == player.getTemplePoint()) {
                player.setTemplePointBonus(6);
            } else if (maxValues.getValue().isPresent() && maxValues.getValue().get() == player.getTemplePoint()) {
                player.setTemplePointBonus(3);
            } else {
                player.setTemplePointBonus(0);
            }

            int points = player.getCoins() + player.getWaterPoint() + player.getWorshipSymbol() + player.getTemplePointBonus();
            player.setPoint(points);
        }
    }

    private Pair<Optional<Integer>, Optional<Integer>> calculateTempleBonuses() {
        playerList.forEach(p -> p.setTemplePointBonus(0));

        Optional<Integer> largestValue = playerList.stream().map(p -> p.getTemplePoint()).sorted(Comparator.reverseOrder()).limit(1).findFirst();
        Optional<Integer> secondLargestValue = playerList.stream().map(p -> p.getTemplePoint()).filter(p -> p != largestValue.get()).sorted(Comparator.reverseOrder()).limit(1).findFirst();

        return new Pair(largestValue, secondLargestValue);


    }



    public void calculateRanks() {
        List<Player> sortedPlayers = new ArrayList<>(playerList);

        playerList.forEach(p -> sortedPlayers.add(p));
        sortedPlayers.sort(Comparator.comparing(Player::getPoint).thenComparingInt(Player::getNumberOfCacaoBean).reversed());

        int rank = 1;
        sortedPlayers.get(0).setRank(rank);
        for (int i = 1; i < sortedPlayers.size(); ++i) {
            if (sortedPlayers.get(i - 1).isEqualRank(sortedPlayers.get(i))){
                sortedPlayers.get(i).setRank(rank);
            }else{
                ++rank;
                sortedPlayers.get(i).setRank(rank);
            }
        }
    }


    public boolean isFieldValid(int x, int y){
        if ( (x >= board.getWidth() || x < 0 || y >= board.getHeight() || y < 0) ){
            return false;
        }
        return true;
    }

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
        if (index >= WATER_POSITION_VALUE_LIST.size()) {
            return WATER_POSITION_VALUE_LIST.get(WATER_POSITION_VALUE_LIST.size()-1);
        }
        return WATER_POSITION_VALUE_LIST.get(index);
    }

    public static List<Integer> getWaterPositionValueList() {
        return WATER_POSITION_VALUE_LIST;
    }

    public static int getMaxNumberOfWorshipSites() {
        return MAX_NUMBER_OF_WORSHIP_SITES;
    }

    public boolean isGameEnded() {
        return isGameEnded;
    }

    public void setGameEnded(boolean gameEnded) {
        isGameEnded = gameEnded;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("playerList=" + playerList);
        result.append(System.lineSeparator());
        result.append("activePlayer=" + activePlayer);
        result.append(System.lineSeparator());
        result.append("isGameEnded=" + isGameEnded);

        return result.toString();
    }

    public String toShortString() {
        return "Game{" +
                ", hasPlacedWorkerTile=" + hasPlacedWorkerTile +
                ", hasPlacedJungleTile=" + hasPlacedJungleTile +
                '}';
    }



}
