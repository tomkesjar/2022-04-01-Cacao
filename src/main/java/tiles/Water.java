package tiles;

public class Water extends JungleTile {

    public Water() {
        setTileType(TileEnum.WATER);
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
