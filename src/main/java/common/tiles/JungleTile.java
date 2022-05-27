package common.tiles;

import common.game.Game;
import common.messages.Pair;

import java.awt.*;
import java.util.*;
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

    public Map<Integer, Pair<Integer, Integer>> processForRobotEvaluation(Pair<Integer,Integer> coord, Game game) {
        Map<Integer, Pair<Integer, Integer>> resultMap = new HashMap<>();

        LinkedList<Pair<Point, Integer>> processOrder = new LinkedList<>();
        List<Point> sides = new ArrayList<>();

        Point leftNeighbour = new Point(coord.getKey() - 1, coord.getValue());
        Point rightNeighbour = new Point(coord.getKey() + 1, coord.getValue());
        Point upNeighbour = new Point(coord.getKey(), coord.getValue() - 1);
        Point downNeighbour = new Point(coord.getKey(), coord.getValue() + 1);

        if (validatePointAndTileType(leftNeighbour, game)) processOrder.add(new Pair(leftNeighbour, ((WorkerTile) game.getBoard().getField(leftNeighbour.x, leftNeighbour.y)).getRightWorker()));
        if (validatePointAndTileType(rightNeighbour, game)) processOrder.add(new Pair(rightNeighbour, ((WorkerTile) game.getBoard().getField(rightNeighbour.x, rightNeighbour.y)).getLeftWorker()));
        if (validatePointAndTileType(upNeighbour, game)) processOrder.add(new Pair(upNeighbour, ((WorkerTile) game.getBoard().getField(upNeighbour.x, upNeighbour.y)).getDownWorker()));
        if (validatePointAndTileType(downNeighbour, game)) processOrder.add(new Pair(downNeighbour, ((WorkerTile) game.getBoard().getField(downNeighbour.x, downNeighbour.y)).getUpWorker()));

        for (Pair<Point, Integer> side : processOrder) {
            Optional<Pair<Integer, Pair<Integer, Integer>>> result = processNeighbourForRobotEvaluation(side.getKey(), game, side.getValue());
            if(result.isPresent()){
                if (resultMap.containsKey(result.get().getKey())){
                    int index = result.get().getKey();
                    int cocoa = result.get().getValue().getKey() + resultMap.get(index).getKey();
                    int point = result.get().getValue().getValue() + resultMap.get(index).getValue();
                    resultMap.put(index, new Pair<>(cocoa, point));
                }else{
                    int index = result.get().getKey();
                    int cocoa = result.get().getValue().getKey();
                    int point = result.get().getValue().getValue();
                    resultMap.put(index, new Pair<>(cocoa, point));
                }
            }
        }
        return resultMap;
    }

    public abstract Optional<Pair<Integer,Pair<Integer, Integer>>> processNeighbourForRobotEvaluation(Point coord, Game game, int numberOfWorker);

    protected abstract void processNeighbour(Point side, Game game, int numberOfWorkers);

    protected boolean validatePointAndTileType(Point coord, Game game){
        boolean location = (!(coord.x < 0 || coord.x >= game.getBoard().getWidth() || coord.y < 0 || coord.y >= game.getBoard().getHeight())) ? true : false;
        if (!location){
            return false;
        }
        boolean isWorkerTile = game.getBoard().getField(coord.x, coord.y) instanceof WorkerTile;
        return isWorkerTile;
    }

    public String toShortString(){
        String className = this.getClass().toString();
        return className;
    }

}
