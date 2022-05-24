package common.players;

import common.deck.WorkerTileDeck;
import common.game.Game;
import common.game.GameHandler;
import common.messages.Pair;
import common.tiles.JungleTile;
import common.tiles.WorkerTile;

import java.awt.*;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Player implements Serializable {
    public enum PlayerType{
        HUMAN,
        BASIC_AI,
        SMARTER_AI
    }

    private static final int MAX_NUMBER_OF_CARDS_AT_HAND = 3;
    private PlayerColour playerColour;

    private String name;
    private PlayerType playerType;

    private int numberOfCacaoBean;
    private int coins;
    private int waterPointIndex;
    private int waterPoint;
    private int worshipSymbol;
    private int templePoint;
    private int templePointBonus;
    private int point;
    private int rank;


    private WorkerTileDeck workerTileDeck;
    private List<WorkerTile> cardsAtHand;


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

    public Player(int nthPlayer, int numberOfPlayers, String playerName) {
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
        this.point = -10;
        this.rank = 1;
        this.name = playerName;

        this.workerTileDeck = new WorkerTileDeck(this.playerColour, numberOfPlayers);
        this.workerTileDeck.shuffleDeck();

        cardsAtHand = new LinkedList<>();
        for (int i = 0; i < MAX_NUMBER_OF_CARDS_AT_HAND; ++i) {
            Optional<WorkerTile> drawnCard = workerTileDeck.drawCard();
            if (drawnCard.isPresent()) {
                cardsAtHand.add(drawnCard.get());
            }
        }
    }

    public Player(PlayerBuilder builder){
        this.name = builder.getName();
        this.rank = builder.getRank();
        this.playerColour = builder.getPlayerColour();
        this.numberOfCacaoBean = builder.getNumberOfCacaoBean();
        this.coins = builder.getCoins();
        this.waterPointIndex = builder.getWaterPointIndex();
        this.waterPoint = builder.getWaterPoint();
        this.point = builder.getPoint();
        this.workerTileDeck = builder.getWorkerTileDeck();
        this.cardsAtHand = builder.getCardsAtHand();
        this.templePoint = builder.getTemplePoint();
        this.templePointBonus = builder.getTemplePointBonus();
    }

    public void evaluateWorkerTilePlacement(GameHandler gameHandler, Point coord, WorkerTile workerTileToPlace) {
        WorkerTile currentTile = (WorkerTile) gameHandler.getGame().getBoard().getField(coord.x, coord.y);
        currentTile.processNeighbours(coord, gameHandler.getGame());
        gameHandler.getGame().calculatePoints();
        gameHandler.getGame().calculateRanks();

        gameHandler.getGame().getBoard().selectPossibleWorkerAndJungleTilesForPlacement();
    }


    public void placeBasicWorkerTile(GameHandler gameHandler ){
        gameHandler.getGame().getBoard().selectPossibleWorkerAndJungleTilesForPlacement();

        Random random = new Random();
        List<Pair<Integer, Integer>> selectablePositions = gameHandler.getGame().getBoard().getSelectableWorkerPanelPositions();

        int randomWorkerTileIndex = random.nextInt(100) % cardsAtHand.size();
        int randomSelectablePositionIndex = random.nextInt(100) % selectablePositions.size();
        int randomNumberOfRotation = random.nextInt(100) % 4;

        Pair<Integer, Integer> position = selectablePositions.get(randomSelectablePositionIndex);

        WorkerTile workerTileToPlace = cardsAtHand.get(randomWorkerTileIndex).turnRightWorkersNinetyDegreesTimes(randomNumberOfRotation);
        gameHandler.setWorkerTile(workerTileToPlace);
        gameHandler.getGame().getBoard().setField(position.getKey(), position.getValue(), workerTileToPlace);

        gameHandler.setWorkerTilePlacementValid(true);
        gameHandler.getGame().getBoard().setFreshWorkerTile(workerTileToPlace);
        gameHandler.getGame().getBoard().setFreshWorkerTilePoint(new Point(position.getKey(), position.getValue()));
        gameHandler.getGame().setHasPlacedWorkerTile(true);
    }

    public void placeBasicJungleTile(GameHandler gameHandler){
        gameHandler.getGame().getBoard().selectPossibleWorkerAndJungleTilesForPlacement();

        Random random = new Random();
        List<Pair<Integer, Integer>> selectablePositions = gameHandler.getGame().getBoard().getSelectableJunglePanelPositions();

        int pickJungleTileIndex = random.nextInt(100) % gameHandler.getGame().getJungleTilesAvailable().size();
        int pickSelectablePositionIndex = random.nextInt(100) % selectablePositions.size();
        Pair<Integer, Integer> position = selectablePositions.get(pickSelectablePositionIndex);

        JungleTile jungleTileToPlace = gameHandler.getGame().getJungleTilesAvailable().get(pickJungleTileIndex);

        gameHandler.getGame().getBoard().setField(position.getKey(), position.getValue(), jungleTileToPlace);
        gameHandler.setJungleTile(jungleTileToPlace);

        gameHandler.setJungleTilePlacementValid(true);
        gameHandler.getGame().getBoard().setFreshJungleTile(jungleTileToPlace);
        gameHandler.getGame().getBoard().setFreshJungleTilePoint(new Point(position.getKey(), position.getValue()));
        gameHandler.getGame().setHasPlacedJungleTile(true);
        gameHandler.getGame().setHasPlacedWorkerTile(false);
    }





    public static int getMaxNumberOfCardsAtHand() {
        return MAX_NUMBER_OF_CARDS_AT_HAND;
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

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getTemplePointBonus() {
        return templePointBonus;
    }

    public void setTemplePointBonus(int templePointBonus) {
        this.templePointBonus = templePointBonus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }

    public void setPlayerType(PlayerType playerType) {
        this.playerType = playerType;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Player{" +
                "playerColour=" + playerColour +
                ", playerName=" + name +
                ", rank=" + rank +
                ", point=" + point +
                ", numberOfCacaoBean=" + numberOfCacaoBean +
                ", coins=" + coins +
                ", waterPointIndex=" + waterPointIndex +
                ", waterPoint=" + waterPoint +
                ", worshipSymbol=" + worshipSymbol +
                ", templePoint=" + templePoint +
                ", workerTileDeck=" + workerTileDeck);
        result.append(System.lineSeparator());
        result.append("cardsAtHand=" + cardsAtHand);
        return result.toString();
    }

    public boolean isEqualRank(Player otherPlayer) {
        if (getPoint() == otherPlayer.getPoint() && getNumberOfCacaoBean() == otherPlayer.getNumberOfCacaoBean()) {
            return true;
        }
        return false;
    }


    public static class PlayerBuilder {
        private PlayerColour playerColour;
        private String name;
        private int numberOfCacaoBean;
        private int coins;
        private int waterPointIndex;
        private int waterPoint;
        private int worshipSymbol;
        private WorkerTileDeck workerTileDeck;
        private List<WorkerTile> cardsAtHand;
        private int templePoint;
        private int templePointBonus;
        private int point;
        private int rank;

        public PlayerBuilder() {
        }

        public PlayerBuilder(Player player) {
            PlayerBuilder builder = new PlayerBuilder();
            builder
                    .setPlayerColour(player.getPlayerColour())
                    .setName(player.getName())
                    .setNumberOfCacaoBean(player.getNumberOfCacaoBean())
                    .setCoins(player.getCoins())
                    .setWaterPointIndex(player.getWaterPointIndex())
                    .setWaterPoint(player.getWaterPoint())
                    .setWorshipSymbol(player.getWorshipSymbol())
                    .setWorkerTileDeck(player.getWorkerTileDeck())
                    .setCardsAtHand(player.getCardsAtHand())
                    .setTemplePoint(player.getTemplePoint())
                    .setTemplePointBonus(player.getTemplePointBonus())
                    .setPoint(player.getPoint())
                    .setRank(player.getRank());
        }


        public PlayerColour getPlayerColour() {
            return playerColour;
        }

        public PlayerBuilder setPlayerColour(PlayerColour playerColour) {
            this.playerColour = playerColour;
            return this;
        }

        public String getName() {
            return name;
        }

        public PlayerBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public int getNumberOfCacaoBean() {
            return numberOfCacaoBean;
        }

        public PlayerBuilder setNumberOfCacaoBean(int numberOfCacaoBean) {
            this.numberOfCacaoBean = numberOfCacaoBean;
            return this;
        }

        public int getCoins() {
            return coins;
        }

        public PlayerBuilder setCoins(int coins) {
            this.coins = coins;
            return this;
        }

        public int getWaterPointIndex() {
            return waterPointIndex;
        }

        public PlayerBuilder setWaterPointIndex(int waterPointIndex) {
            this.waterPointIndex = waterPointIndex;
            return this;
        }

        public int getWaterPoint() {
            return waterPoint;
        }

        public PlayerBuilder setWaterPoint(int waterPoint) {
            this.waterPoint = waterPoint;
            return this;
        }

        public int getWorshipSymbol() {
            return worshipSymbol;
        }

        public PlayerBuilder setWorshipSymbol(int worshipSymbol) {
            this.worshipSymbol = worshipSymbol;
            return this;
        }

        public WorkerTileDeck getWorkerTileDeck() {
            return workerTileDeck;
        }

        public PlayerBuilder setWorkerTileDeck(WorkerTileDeck workerTileDeck) {
            this.workerTileDeck = workerTileDeck;
            return this;
        }

        public List<WorkerTile> getCardsAtHand() {
            return cardsAtHand;
        }

        public PlayerBuilder setCardsAtHand(List<WorkerTile> cardsAtHand) {
            this.cardsAtHand = cardsAtHand;
            return this;
        }

        public int getTemplePoint() {
            return templePoint;
        }

        public PlayerBuilder setTemplePoint(int templePoint) {
            this.templePoint = templePoint;
            return this;
        }

        public int getTemplePointBonus() {
            return templePointBonus;
        }

        public PlayerBuilder setTemplePointBonus(int templePointBonus) {
            this.templePointBonus = templePointBonus;
            return this;
        }

        public int getPoint() {
            return point;
        }

        public PlayerBuilder setPoint(int point) {
            this.point = point;
            return this;
        }

        public int getRank() {
            return rank;
        }

        public PlayerBuilder setRank(int rank) {
            this.rank = rank;
            return this;
        }

        public Player build(){
            Player newPlayer = new Player(this);
            return newPlayer;
        }
    }
}
