package common.tiles;

import common.game.Game;
import common.messages.Pair;
import common.players.Player;

import java.awt.*;
import java.util.Optional;

public class Water extends JungleTile {

    public Water() {
        setTileEnum(TileEnum.WATER);
    }

    @Override
    protected void processNeighbour(Point coord, Game game, int numberOfWorker) {
        if (!(coord.x < 0 || coord.x >= game.getBoard().getWidth() || coord.y < 0 || coord.y >= game.getBoard().getHeight())) {
            WorkerTile neighbour = (WorkerTile) game.getBoard().getField(coord.x, coord.y);
            int activePlayerIndex = neighbour.getColour().getPlayerOrdinal()-1;
            Player activePlayer = game.getPlayerList().get(activePlayerIndex);

            for (int i=0; i<numberOfWorker; ++i){
                if (activePlayer.getWaterPointIndex() < Game.getWaterPositionValueList().size()) {
                    activePlayer.setWaterPointIndex(activePlayer.getWaterPointIndex() + 1);
                    activePlayer.setWaterPoint(Game.getWaterPositionValue(activePlayer.getWaterPointIndex()));
                }
            }
        }
    }

    @Override
    public Optional<Pair<Integer, Pair<Integer, Integer>>> processNeighbourForRobotEvaluation(Point coord, Game game, int numberOfWorker) {
        if (!(coord.x < 0 || coord.x >= game.getBoard().getWidth() || coord.y < 0 || coord.y >= game.getBoard().getHeight())) {
            WorkerTile neighbour = (WorkerTile) game.getBoard().getField(coord.x, coord.y);
            int activePlayerIndex = neighbour.getColour().getPlayerOrdinal()-1;
            Player activePlayer = game.getPlayerList().get(activePlayerIndex);

            int startingValue = activePlayer.getWaterPoint();
            int startingValueIndex = activePlayer.getWaterPointIndex();

            for (int i=0; i<numberOfWorker; ++i){
                if (activePlayer.getWaterPointIndex() < Game.getWaterPositionValueList().size()) {
                    activePlayer.setWaterPointIndex(activePlayer.getWaterPointIndex() + 1);
                    activePlayer.setWaterPoint(Game.getWaterPositionValue(activePlayer.getWaterPointIndex()));
                }
            }
            int endingValue = activePlayer.getWaterPoint();
            int endingValueIndex = activePlayer.getWaterPointIndex();
            int diffValue = endingValue - startingValue;
            activePlayer.setWaterPointIndex(startingValueIndex);
            activePlayer.setWaterPoint(startingValue);

            return Optional.of(new Pair<>(activePlayerIndex, new Pair<>(0, diffValue) ));
        }
        return Optional.empty();
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
