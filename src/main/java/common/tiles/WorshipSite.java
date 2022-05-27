package common.tiles;

import common.game.Game;
import common.messages.Pair;
import common.players.Player;

import java.awt.*;
import java.util.Optional;

public class WorshipSite extends JungleTile {

    public WorshipSite() {
        setTileEnum(TileEnum.WORSHIP_SITE);
    }

    @Override
    protected void processNeighbour(Point coord, Game game, int numberOfWorker) {
        if (!(coord.x < 0 || coord.x >= game.getBoard().getWidth() || coord.y < 0 || coord.y >= game.getBoard().getHeight())) {
            WorkerTile neighbour = (WorkerTile) game.getBoard().getField(coord.x, coord.y);
            Player activePlayer = game.getPlayerList().get(neighbour.getColour().getPlayerOrdinal()-1);

            for (int i=0; i<numberOfWorker; ++i){
                activePlayer.setWorshipSymbol(Math.min(activePlayer.getWorshipSymbol() + 1, Game.getMaxNumberOfWorshipSites()));
            }
        }
    }

    @Override
    public Optional<Pair<Integer, Pair<Integer, Integer>>> processNeighbourForRobotEvaluation(Point coord, Game game, int numberOfWorker) {
        if (!(coord.x < 0 || coord.x >= game.getBoard().getWidth() || coord.y < 0 || coord.y >= game.getBoard().getHeight())) {
            WorkerTile neighbour = (WorkerTile) game.getBoard().getField(coord.x, coord.y);
            int activePlayerIndex = neighbour.getColour().getPlayerOrdinal()-1;
            Player activePlayer = game.getPlayerList().get(activePlayerIndex);

            int startingValue = activePlayer.getWorshipSymbol();

            for (int i=0; i<numberOfWorker; ++i){
                activePlayer.setWorshipSymbol(Math.min(activePlayer.getWorshipSymbol() + 1, Game.getMaxNumberOfWorshipSites()));
            }
            int endingValue = activePlayer.getWorshipSymbol();
            int diffValue = endingValue - startingValue;
            activePlayer.setWorshipSymbol(startingValue);

            return Optional.of(new Pair<>(activePlayerIndex, new Pair<>(0, diffValue) ));


        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return "WorshipSite{}"+" hashCode= "+System.identityHashCode(this);
    }

    @Override
    public String toShortString() {
        return "WORSH";
    }
}
