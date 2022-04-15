package tiles;

public class WorshipSite extends JungleTile {

    public WorshipSite() {
        setTileType(TileEnum.WORSHIP_SITE);
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
