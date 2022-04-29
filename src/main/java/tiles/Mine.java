package tiles;

import game.Game;
import players.Player;

import java.awt.*;

public class Mine extends JungleTile {
    private int goldValue;

    public Mine(int goldValue) {
        this.goldValue = goldValue;
        switch (this.goldValue){
            case 1: setTileEnum(TileEnum.MINE_1); break;
            case 2: setTileEnum(TileEnum.MINE_2); break;
        }
    }

    @Override
    protected void processNeighbour(Point coord, Game game, int numberOfWorker) {
        if (!(coord.x < 0 || coord.x >= game.getBoard().getWidth() || coord.y < 0 || coord.y >= game.getBoard().getHeight())) {
            WorkerTile neighbour = (WorkerTile) game.getBoard().getField(coord.x, coord.y);
            Player activePlayer = game.getPlayerList().get(neighbour.getColour().getPlayerOrdinal()-1);

            for (int i=0; i<numberOfWorker; ++i){
                activePlayer.setCoins(activePlayer.getCoins() + getGoldValue());
            }
        }
    }

    @Override
    public AbstractTile clone() {
        return (Mine) super.clone();
    }

    public int getGoldValue() {
        return goldValue;
    }

    public void setGoldValue(int goldValue) {
        this.goldValue = goldValue;
    }

    @Override
    public String toString() {
        return "Mine{" +
                "goldValue=" + goldValue +
                '}'+" hashCode= "+System.identityHashCode(this);
    }

    @Override
    public String toShortString() {
        return "MINE"+getGoldValue();
    }
}
