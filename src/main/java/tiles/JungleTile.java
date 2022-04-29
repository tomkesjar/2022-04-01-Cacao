package tiles;

import game.Game;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class JungleTile extends AbstractTile {

    public void processNeighbours(Point coord, Game game) {
        LinkedList<Pair<Point, Integer>> processOrder = new LinkedList<>();
        List<Point> sides = new ArrayList<>();

        Point leftNeighbour = new Point(coord.x - 1, coord.y);
        Point rightNeighbour = new Point(coord.x + 1, coord.y);
        Point upNeighbour = new Point(coord.x, coord.y - 1);
        Point downNeighbour = new Point(coord.x, coord.y + 1);

        if (validatePointAndTileType(leftNeighbour, game)) processOrder.add(new Pair(leftNeighbour, ((WorkerTile) game.getBoard().getField(leftNeighbour.x, leftNeighbour.y)).getRightWorker()));
        if (validatePointAndTileType(rightNeighbour, game)) processOrder.add(new Pair(rightNeighbour, ((WorkerTile) game.getBoard().getField(rightNeighbour.x, rightNeighbour.y)).getLeftWorker()));
        if (validatePointAndTileType(upNeighbour, game)) processOrder.add(new Pair(upNeighbour, ((WorkerTile) game.getBoard().getField(upNeighbour.x, upNeighbour.y)).getDownWorker()));
        if (validatePointAndTileType(downNeighbour, game)) processOrder.add(new Pair(downNeighbour, ((WorkerTile) game.getBoard().getField(downNeighbour.x, downNeighbour.y)).getUpWorker()));

        processOrder.forEach(side ->
            processNeighbour(side.getKey(), game, side.getValue()));
    }

    protected boolean validatePointAndTileType(Point coord, Game game){
        boolean location = (!(coord.x < 0 || coord.x >= game.getBoard().getWidth() || coord.y < 0 || coord.y >= game.getBoard().getHeight())) ? true : false;
        if (!location){
            return false;
        }
        boolean isWorkerTile = game.getBoard().getField(coord.x, coord.y) instanceof WorkerTile;
        return isWorkerTile;
    }

    protected abstract void processNeighbour(Point side, Game game, int numberOfWorkers);

    public String toShortString(){
        String className = this.getClass().toString();
        return className;
    }

}
