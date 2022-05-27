package common.tiles;

import common.game.Game;
import common.messages.Pair;

import java.awt.*;
import java.util.Optional;

public class EmptyTile extends JungleTile {

    public EmptyTile() {
        setTileEnum(TileEnum.EMPTY);
    }

    @Override
    protected void processNeighbour(Point side, Game game, int numberOfWorkers) {
    }

    @Override
    public Optional<Pair<Integer,Pair<Integer, Integer>>> processNeighbourForRobotEvaluation(Point coord, Game game, int numberOfWorker) {
        return Optional.empty();
    }

    @Override
    public AbstractTile clone() {
        return (EmptyTile) super.clone();
    }

    @Override
    public String toString() {
        return "EmptyTile{}"+" hashCode= "+System.identityHashCode(this);
    }

    @Override
    public String toShortString() {
        return "EMPTY";
    }
}
