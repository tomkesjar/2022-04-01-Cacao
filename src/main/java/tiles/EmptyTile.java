package tiles;

public class EmptyTile extends JungleTile {

    public EmptyTile() {
        setTileType(TileEnum.EMPTY);
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