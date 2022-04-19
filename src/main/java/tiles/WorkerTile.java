package tiles;

import game.Game;
import javafx.util.Pair;
import players.Player;
import players.PlayerColour;

import java.awt.*;
import java.util.ArrayList;
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

    public void turnRightWorkersNinetyDegrees() {
        int temporaryValue = rightWorker;
        rightWorker = downWorker;
        downWorker = leftWorker;
        leftWorker = upWorker;
        upWorker = temporaryValue;
    }

    public WorkerTile(int rightWorker, int upWorker, int leftWorker, int downWorker, PlayerColour colour) {
        this.rightWorker = rightWorker;
        this.upWorker = upWorker;
        this.leftWorker = leftWorker;
        this.downWorker = downWorker;
        this.type = "Worker";
        this.colour = colour;
    }

    public PlayerColour getColour() {
        return colour;
    }


    @Override
    public String toString() {
        return "WorkerTile{" +
                "rightWorker=" + rightWorker +
                ", upWorker=" + upWorker +
                ", leftWorker=" + leftWorker +
                ", downWorker=" + downWorker +
                ", type='" + type + '\'' +
                ", colour=" + colour +
                '}' + " hashCode= " + System.identityHashCode(this);
    }

    @Override
    public String toShortString() {
        String colour = (getColour().toString());
        String shortColour = colour.substring(0, Math.min(colour.length(), 1));
        //return "W_"+ shortColour;
        return shortColour + this.rightWorker + this.upWorker + this.leftWorker + this.downWorker;
    }


    public void processNeighbours(Point coord, Game game) {
        LinkedList<Pair<Point, Integer>> processOrder = new LinkedList<>();

        List<Pair<Point, Integer>> sidesAndWorkers = Arrays.asList(
                new Pair(new Point(coord.x - 1, coord.y), leftWorker),
                new Pair(new Point(coord.x + 1, coord.y), rightWorker),
                new Pair(new Point(coord.x, coord.y - 1), upWorker),
                new Pair(new Point(coord.x, coord.y + 1), downWorker));

        for (Pair<Point, Integer> side: sidesAndWorkers) {
            AbstractTile tile = game.getBoard().getField(side.getKey().x, side.getKey().y);
            if ((tile.getTileType().equals(TileEnum.MARKET_LOW) || tile.getTileType().equals(TileEnum.MARKET_MID) || tile.getTileType().equals(TileEnum.MARKET_HIGH))) {
                processOrder.addLast(new Pair<>(new Point(side.getKey().x, side.getKey().y), side.getValue()));
            } else {
                processOrder.addFirst(new Pair<>(new Point(side.getKey().x, side.getKey().y), side.getValue()));
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

            switch (neighbourJungleTile.getTileType()) {
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
}
