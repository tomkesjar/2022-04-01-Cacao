package tiles;

import game.Game;
import javafx.util.Pair;
import players.Player;
import players.PlayerColour;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class WorkerTile extends AbstractTile {

    private int rightWorker;
    private int upWorker;
    private int leftWorker;
    private int downWorker;


    private String type;
    private PlayerColour colour;
    private TileEnum tileEnum;
    private int numberOfRotation;

    public WorkerTile(int leftWorker, int upWorker, int rightWorker, int downWorker, PlayerColour colour) {
        this.leftWorker = leftWorker;
        this.upWorker = upWorker;
        this.rightWorker = rightWorker;
        this.downWorker = downWorker;
        this.type = "Worker";
        this.colour = colour;
        this.tileEnum = TileEnum.valueOf(this.toShortString());
        this.numberOfRotation = 0;
    }

    public WorkerTile turnRightWorkersNinetyDegreesTimes(int numberOfTurn) {
        if (numberOfTurn > 0) {
            for (int i = 0; i < numberOfTurn; ++i) {
                this.turnRightWorkersNinetyDegrees();
            }
        }
        return this;
    }

    public void turnRightWorkersNinetyDegrees() {
        int temporaryValue = downWorker;
        downWorker = rightWorker;
        rightWorker = upWorker;
        upWorker = leftWorker;
        leftWorker = temporaryValue;

        this.tileEnum = TileEnum.valueOf(this.toShortString());
        numberOfRotation = (numberOfRotation + 1) % 4;
    }

    public PlayerColour getColour() {
        return colour;
    }

    public int getRightWorker() {
        return rightWorker;
    }

    public int getUpWorker() {
        return upWorker;
    }

    public int getLeftWorker() {
        return leftWorker;
    }

    public int getDownWorker() {
        return downWorker;
    }

    @Override
    public TileEnum getTileEnum() {
        return tileEnum;
    }

    @Override
    public int getNumberOfRotation() {
        return numberOfRotation;
    }

    @Override
    public String toString() {
        return tileEnum.toString() + " hashCode= " + System.identityHashCode(this);
    }

    @Override
    public String toShortString() {
        String colour = (getColour().toString());
        String shortColour = colour.substring(0, Math.min(colour.length(), 1));
        //return "W_"+ shortColour;
        return shortColour + this.leftWorker + this.upWorker + this.rightWorker + this.downWorker;
    }


    public void processNeighbours(Point coord, Game game) {
        LinkedList<Pair<Point, Integer>> processOrder = new LinkedList<>();

        List<Pair<Point, Integer>> sidesAndWorkers = Arrays.asList(
                new Pair<Point, Integer>(new Point(coord.x - 1, coord.y), leftWorker),
                new Pair<Point, Integer>(new Point(coord.x + 1, coord.y), rightWorker),
                new Pair<Point, Integer>(new Point(coord.x, coord.y - 1), upWorker),
                new Pair<Point, Integer>(new Point(coord.x, coord.y + 1), downWorker));

        for (Pair<Point, Integer> side : sidesAndWorkers) {
            if (game.isFieldValid(side.getKey().x, side.getKey().y)) {
                //System.out.println("[WorkerTile]: validation for x=" + side.getKey().x + ", y=" + side.getKey().y);
                AbstractTile tile = game.getBoard().getField(side.getKey().x, side.getKey().y);
                if ((TileEnum.MARKET_LOW.equals(tile.getTileEnum()) || TileEnum.MARKET_MID.equals(tile.getTileEnum()) || TileEnum.MARKET_HIGH.equals(tile.getTileEnum()))) {
                    processOrder.addLast(new Pair<>(new Point(side.getKey().x, side.getKey().y), side.getValue()));
                } else if (!TileEnum.EMPTY.equals(tile.getTileEnum())) {
                    processOrder.addFirst(new Pair<>(new Point(side.getKey().x, side.getKey().y), side.getValue()));
                }
            }
        }

        processOrder.forEach(neighbour -> processNeighbour(neighbour.getKey(), game, neighbour.getValue()));
    }

    public void processRightNeighbourOfWorker(Point coord, Game game) {
        Player activePlayer = game.getPlayerList().get(game.getActivePlayer());

        processNeighbour(new Point(coord.x + 1, coord.y), game, rightWorker);

    }

    public void processLeftNeighbourOfWorker(Point coord, Game game) {
        Player activePlayer = game.getPlayerList().get(game.getActivePlayer());

        processNeighbour(new Point(coord.x - 1, coord.y), game, leftWorker);

    }

    public void processDownNeighbourOfWorker(Point coord, Game game) {
        Player activePlayer = game.getPlayerList().get(game.getActivePlayer());

        processNeighbour(new Point(coord.x, coord.y + 1), game, downWorker);

    }

    public void processUpNeighbourOfWorker(Point coord, Game game) {
        Player activePlayer = game.getPlayerList().get(game.getActivePlayer());

        processNeighbour(new Point(coord.x, coord.y - 1), game, upWorker);

    }

    private void processNeighbour(Point coord, Game game, int numberOfWorker) {
        if (!(coord.x < 0 || coord.x >= game.getBoard().getWidth() || coord.y < 0 || coord.y >= game.getBoard().getHeight())) {
            Player activePlayer = game.getPlayerList().get(game.getActivePlayer());

            JungleTile neighbourJungleTile = (JungleTile) game.getBoard().getField(coord.x, coord.y);

            switch (neighbourJungleTile.getTileEnum()) {
                case WATER:
                    for (int i = 0; i < numberOfWorker; ++i) {
                        if (activePlayer.getWaterPointIndex() < Game.getWaterPositionValueList().size()) {
                            activePlayer.setWaterPointIndex(activePlayer.getWaterPointIndex() + 1);       //TODO: meet actual game rule
                            activePlayer.setWaterPoint(Game.getWaterPositionValue(activePlayer.getWaterPointIndex()));
                        }
                    }
                    break;
                case TEMPLE:
                    for (int i = 0; i < numberOfWorker; ++i) {
                        activePlayer.setTemplePoint(activePlayer.getTemplePoint() + 1);
                    }
                    break;
                case WORSHIP_SITE:
                    for (int i = 0; i < numberOfWorker; ++i) {
                        activePlayer.setWorshipSymbol(Math.min(activePlayer.getWorshipSymbol() + 1, Game.getMaxNumberOfWorshipSites()));
                    }
                    break;
                case MINE_1:
                    for (int i = 0; i < numberOfWorker; ++i) {
                        activePlayer.setCoins(activePlayer.getCoins() + 1);
                    }
                    break;
                case MINE_2:
                    for (int i = 0; i < numberOfWorker; ++i) {
                        activePlayer.setCoins(activePlayer.getCoins() + 2);
                    }
                    break;
                case PLANTATION_1:
                    for (int i = 0; i < numberOfWorker; ++i) {
                        activePlayer.setNumberOfCacaoBean(Math.min(activePlayer.getNumberOfCacaoBean() + 1, Game.getMaxNumberOfCacaoBeans()));
                    }
                    break;
                case PLANTATION_2:
                    for (int i = 0; i < numberOfWorker; ++i) {
                        activePlayer.setNumberOfCacaoBean(Math.min(activePlayer.getNumberOfCacaoBean() + 2, Game.getMaxNumberOfCacaoBeans()));
                    }
                    break;

                case MARKET_LOW:
                    for (int i = 0; i < numberOfWorker; ++i) {
                        if (activePlayer.getNumberOfCacaoBean() > 0) {
                            activePlayer.setNumberOfCacaoBean(activePlayer.getNumberOfCacaoBean() - 1);
                            activePlayer.setCoins(activePlayer.getCoins() + Market.MarketPrice.LOW.getValue());
                        }
                    }
                    break;
                case MARKET_MID:
                    for (int i = 0; i < numberOfWorker; ++i) {
                        if (activePlayer.getNumberOfCacaoBean() > 0) {
                            activePlayer.setNumberOfCacaoBean(activePlayer.getNumberOfCacaoBean() - 1);
                            activePlayer.setCoins(activePlayer.getCoins() + Market.MarketPrice.MID.getValue());
                        }
                    }
                    break;
                case MARKET_HIGH:
                    for (int i = 0; i < numberOfWorker; ++i) {
                        if (activePlayer.getNumberOfCacaoBean() > 0) {
                            activePlayer.setNumberOfCacaoBean(activePlayer.getNumberOfCacaoBean() - 1);
                            activePlayer.setCoins(activePlayer.getCoins() + Market.MarketPrice.HIGH.getValue());
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkerTile that = (WorkerTile) o;
        if (colour != that.colour) return false;

        WorkerTile thatRotatedFirst = ((WorkerTile) that.clone()).turnRightWorkersNinetyDegreesTimes(1);
        WorkerTile thatRotatedSecond = ((WorkerTile) that.clone()).turnRightWorkersNinetyDegreesTimes(2);
        WorkerTile thatRotatedThird = ((WorkerTile) that.clone()).turnRightWorkersNinetyDegreesTimes(3);

        if (
                (this.leftWorker == that.leftWorker && this.rightWorker == that.rightWorker && this.upWorker == that.upWorker && this.downWorker == that.downWorker) ||
                (this.leftWorker == thatRotatedFirst.leftWorker && this.rightWorker == thatRotatedFirst.rightWorker && this.upWorker == thatRotatedFirst.upWorker && this.downWorker == thatRotatedFirst.downWorker) ||
                (this.leftWorker == thatRotatedSecond.leftWorker && this.rightWorker == thatRotatedSecond.rightWorker && this.upWorker == thatRotatedSecond.upWorker && this.downWorker == thatRotatedSecond.downWorker) ||
                (this.leftWorker == thatRotatedThird.leftWorker && this.rightWorker == thatRotatedThird.rightWorker && this.upWorker == thatRotatedThird.upWorker && this.downWorker == thatRotatedThird.downWorker)) {
            return true;
        }
        return false;

    }

    @Override
    public int hashCode() {
        int result = rightWorker;
        result = 31 * result + upWorker;
        result = 31 * result + leftWorker;
        result = 31 * result + downWorker;
        result = 31 * result + colour.hashCode();
        return result;
    }
}
