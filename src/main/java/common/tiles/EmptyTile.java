package common.tiles;

import common.game.Game;

import java.awt.*;

public class EmptyTile extends JungleTile {

    public EmptyTile() {
        setTileEnum(TileEnum.EMPTY);
    }

    @Override
    protected void processNeighbour(Point side, Game game, int numberOfWorkers) {

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
