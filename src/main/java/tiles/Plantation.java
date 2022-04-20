package tiles;

import game.Game;
import players.Player;

import java.awt.*;

public class Plantation extends JungleTile {
    private int numberOfCocoaBean;

    public Plantation(int numberOfCocoaBean) {
        this.numberOfCocoaBean = numberOfCocoaBean;
        switch (this.numberOfCocoaBean){
            case 1: setTileType(TileEnum.PLANTATION_1); break;
            case 2: setTileType(TileEnum.PLANTATION_2); break;
        }
    }

    @Override
    protected void processNeighbour(Point coord, Game game, int numberOfWorker) {
        if (!(coord.x < 0 || coord.x >= game.getBoard().getWidth() || coord.y < 0 || coord.y >= game.getBoard().getHeight())) {
            WorkerTile neighbour = (WorkerTile) game.getBoard().getField(coord.x, coord.y);
            Player activePlayer = game.getPlayerList().get(neighbour.getColour().getPlayerOrdinal()-1);

            for (int i=0; i<numberOfWorker; ++i){
                activePlayer.setNumberOfCacaoBean(Math.min(activePlayer.getNumberOfCacaoBean() + getNumberOfCocoaBean(), Game.getMaxNumberOfCacaoBeans()));
            }
        }
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
