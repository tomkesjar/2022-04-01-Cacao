package players;

import deck.WorkerTileDeck;
import game.Game;
import tiles.WorkerTile;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Player implements Serializable {
    private static final int MAX_NUMBER_OF_CARDS_AT_HAND = 3;
    private PlayerColour playerColour;

    private int numberOfCacaoBean;
    private int coins;
    private int waterPointIndex;
    private int waterPoint;
    private int worshipSymbol;
    private WorkerTileDeck workerTileDeck;
    private List<WorkerTile> cardsAtHand;
    private int templePoint;

    public WorkerTileDeck getWorkerTileDeck() {
        return workerTileDeck;
    }

    public void setWorkerTileDeck(WorkerTileDeck workerTileDeck) {
        this.workerTileDeck = workerTileDeck;
    }

    public List<WorkerTile> getCardsAtHand() {
        return cardsAtHand;
    }

    public void setCardsAtHand(List<WorkerTile> cardsAtHand) {
        this.cardsAtHand = cardsAtHand;
    }

    public Player(int nthPlayer, int numberOfPlayers) {
        switch (nthPlayer) {
            case 1:
                this.playerColour = PlayerColour.RED;
                break;
            case 2:
                this.playerColour = PlayerColour.BLUE;
                break;
            case 3:
                this.playerColour = PlayerColour.GREEN;
                break;
            case 4:
                this.playerColour = PlayerColour.YELLOW;
                break;
        }

        this.numberOfCacaoBean = 0;
        this.coins = 0;
        this.waterPointIndex = 0;
        this.waterPoint = Game.getWaterPositionValue(this.waterPointIndex);

        this.workerTileDeck = new WorkerTileDeck(this.playerColour, numberOfPlayers);
        this.workerTileDeck.shuffleDeck();

        cardsAtHand = new LinkedList<>();
        for (int i = 0; i < MAX_NUMBER_OF_CARDS_AT_HAND; ++i){
            Optional<WorkerTile> drawnCard = workerTileDeck.drawCard();
            if (drawnCard.isPresent()) {
                cardsAtHand.add(drawnCard.get());
            }
        }


    }

    public PlayerColour getPlayerColour() {
        return playerColour;
    }

    public void setPlayerColour(PlayerColour playerColour) {
        this.playerColour = playerColour;
    }

    public int getNumberOfCacaoBean() {
        return numberOfCacaoBean;
    }

    public void setNumberOfCacaoBean(int numberOfCacaoBean) {
        this.numberOfCacaoBean = numberOfCacaoBean;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getWaterPointIndex() {
        return waterPointIndex;
    }

    public void setWaterPointIndex(int waterPointIndex) {
        this.waterPointIndex = waterPointIndex;
    }

    public int getWaterPoint() {
        return waterPoint;
    }

    public void setWaterPoint(int waterPoint) {
        this.waterPoint = waterPoint;
    }

    public int getWorshipSymbol() {
        return worshipSymbol;
    }

    public void setWorshipSymbol(int worshipSymbol) {
        this.worshipSymbol = worshipSymbol;
    }

    public int getTemplePoint() {
        return templePoint;
    }

    public void setTemplePoint(int templePoint) {
        this.templePoint = templePoint;
    }

    @Override
    public String toString() {
        return "Player{" +
                "playerColour=" + playerColour +
                ", numberOfCacaoBean=" + numberOfCacaoBean +
                ", coins=" + coins +
                ", waterPointIndex=" + waterPointIndex +
                ", waterPoint=" + waterPoint +
                ", worshipSymbol=" + worshipSymbol +
                ", workerTileDeck=" + workerTileDeck +
                ", cardsAtHand=" + cardsAtHand +
                ", templePoint=" + templePoint +
                '}';
    }
}
