package tiles;

import game.Game;
import players.Player;

import java.awt.*;

public class Temple extends JungleTile {

    public Temple() {
        setTileEnum(TileEnum.TEMPLE);
    }

    @Override
    protected void processNeighbour(Point coord, Game game, int numberOfWorker) {
        if (!(coord.x < 0 || coord.x >= game.getBoard().getWidth() || coord.y < 0 || coord.y >= game.getBoard().getHeight())) {
            WorkerTile neighbour = (WorkerTile) game.getBoard().getField(coord.x, coord.y);
            Player activePlayer = game.getPlayerList().get(neighbour.getColour().getPlayerOrdinal()-1);

            for (int i=0; i<numberOfWorker; ++i){
                activePlayer.setTemplePoint(activePlayer.getTemplePoint() + 1);
            }
        }
    }

    @Override
    public String toString() {
        return "Temple{}"+" hashCode= "+System.identityHashCode(this);
    }

    @Override
    public String toShortString() {
        return "TEMPL";
    }
}
