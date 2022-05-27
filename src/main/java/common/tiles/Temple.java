package common.tiles;

import common.game.Game;
import common.messages.Pair;
import common.players.Player;

import java.awt.*;
import java.util.Optional;

public class Temple extends JungleTile {

    public Temple() {
        setTileEnum(TileEnum.TEMPLE);
    }

    @Override
    protected void processNeighbour(Point coord, Game game, int numberOfWorker) {
        if (!(coord.x < 0 || coord.x >= game.getBoard().getWidth() || coord.y < 0 || coord.y >= game.getBoard().getHeight())) {
            WorkerTile neighbour = (WorkerTile) game.getBoard().getField(coord.x, coord.y);
            int activePlayerIndex = neighbour.getColour().getPlayerOrdinal()-1;
            Player activePlayer = game.getPlayerList().get(activePlayerIndex);

            for (int i=0; i<numberOfWorker; ++i){
                activePlayer.setTemplePoint(activePlayer.getTemplePoint() + 1);
            }
        }
    }


    @Override
    public Optional<Pair<Integer, Pair<Integer, Integer>>> processNeighbourForRobotEvaluation(Point coord, Game game, int numberOfWorker) {
        if (!(coord.x < 0 || coord.x >= game.getBoard().getWidth() || coord.y < 0 || coord.y >= game.getBoard().getHeight())) {
            WorkerTile neighbour = (WorkerTile) game.getBoard().getField(coord.x, coord.y);
            int activePlayerIndex = neighbour.getColour().getPlayerOrdinal()-1;
            Player activePlayer = game.getPlayerList().get(activePlayerIndex);

            int startingValue = activePlayer.getTemplePoint();

            for (int i=0; i<numberOfWorker; ++i){
                activePlayer.setTemplePoint(activePlayer.getTemplePoint() + 1);
            }
            int endingValue = activePlayer.getTemplePoint();
            int diffValue = endingValue - startingValue;
            activePlayer.setTemplePoint(startingValue);

            return Optional.of(new Pair<>(activePlayerIndex, new Pair<>(0, diffValue) ));
        }
        return Optional.empty();
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
