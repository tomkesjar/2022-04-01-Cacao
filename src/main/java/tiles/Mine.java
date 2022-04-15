package tiles;

public class Mine extends JungleTile {
    private int goldValue;

    public Mine(int goldValue) {
        this.goldValue = goldValue;
        switch (this.goldValue){
            case 1: setTileType(TileEnum.MINE_1); break;
            case 2: setTileType(TileEnum.MINE_2); break;
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
