package tiles;

import game.Game;
import players.Player;

import java.awt.*;

public class Water extends JungleTile {

    public Water() {
        setTileType(TileEnum.WATER);
    }

    @Override
    protected void processNeighbour(Point coord, Game game, int numberOfWorker) {
        if (!(coord.x < 0 || coord.x >= game.getBoard().getWidth() || coord.y < 0 || coord.y >= game.getBoard().getHeight())) {
            WorkerTile neighbour = (WorkerTile) game.getBoard().getField(coord.x, coord.y);
            Player activePlayer = game.getPlayerList().get(neighbour.getColour().getPlayerOrdinal()-1);

            for (int i=0; i<numberOfWorker; ++i){
                if (activePlayer.getWaterPointIndex() < Game.getWaterPositionValueList().size()) {
                    activePlayer.setWaterPointIndex(activePlayer.getWaterPointIndex() + 1);       //TODO: meet actual game rule
                    activePlayer.setWaterPoint(Game.getWaterPositionValue(activePlayer.getWaterPointIndex()));
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Water{}"+" hashCode= "+System.identityHashCode(this);
    }

    @Override
    public String toShortString() {
        return "WATER";
    }
}
