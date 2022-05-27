package common.tiles;

import common.game.Game;
import common.messages.Pair;
import common.players.Player;

import java.awt.*;
import java.util.Optional;

public class Plantation extends JungleTile {
    private int numberOfCocoaBean;

    public Plantation(int numberOfCocoaBean) {
        this.numberOfCocoaBean = numberOfCocoaBean;
        switch (this.numberOfCocoaBean){
            case 1: setTileEnum(TileEnum.PLANTATION_1); break;
            case 2: setTileEnum(TileEnum.PLANTATION_2); break;
        }
    }

    @Override
    protected void processNeighbour(Point coord, Game game, int numberOfWorker) {
        if (!(coord.x < 0 || coord.x >= game.getBoard().getWidth() || coord.y < 0 || coord.y >= game.getBoard().getHeight())) {
            WorkerTile neighbour = (WorkerTile) game.getBoard().getField(coord.x, coord.y);
            int activePlayerIndex = neighbour.getColour().getPlayerOrdinal()-1;
            Player activePlayer = game.getPlayerList().get(activePlayerIndex);

            for (int i=0; i<numberOfWorker; ++i){
                activePlayer.setNumberOfCacaoBean(Math.min(activePlayer.getNumberOfCacaoBean() + getNumberOfCocoaBean(), Game.getMaxNumberOfCacaoBeans()));
            }
        }
    }

    @Override
    public Optional<Pair<Integer, Pair<Integer, Integer>>> processNeighbourForRobotEvaluation(Point coord, Game game, int numberOfWorker) {
        if (!(coord.x < 0 || coord.x >= game.getBoard().getWidth() || coord.y < 0 || coord.y >= game.getBoard().getHeight())) {
            WorkerTile neighbour = (WorkerTile) game.getBoard().getField(coord.x, coord.y);
            int activePlayerIndex = neighbour.getColour().getPlayerOrdinal()-1;
            Player activePlayer = game.getPlayerList().get(activePlayerIndex);

            int startingNumberOfCocaBean = activePlayer.getNumberOfCacaoBean();

            for (int i=0; i<numberOfWorker; ++i){
                activePlayer.setNumberOfCacaoBean(Math.min(activePlayer.getNumberOfCacaoBean() + getNumberOfCocoaBean(), Game.getMaxNumberOfCacaoBeans()));
            }
            int endingNumberOfCocaBean = activePlayer.getNumberOfCacaoBean();
            int diffNumberOfCocaBean = endingNumberOfCocaBean - startingNumberOfCocaBean;
            activePlayer.setNumberOfCacaoBean(startingNumberOfCocaBean);

            return Optional.of(new Pair<>(activePlayerIndex, new Pair<>(diffNumberOfCocaBean, 0) ));
        }
        return Optional.empty();
    }

    public int getNumberOfCocoaBean() {
        return numberOfCocoaBean;
    }

    public void setNumberOfCocoaBean(int numberOfCocoaBean) {
        this.numberOfCocoaBean = numberOfCocoaBean;
    }

    @Override
    public String toString() {
        return "Plantation{" +
                "numberOfCocoaBean=" + numberOfCocoaBean +
                '}'+" hashCode= "+System.identityHashCode(this);
    }

    @Override
    public String toShortString() {
        return "PLAN"+getNumberOfCocoaBean();
    }
}
