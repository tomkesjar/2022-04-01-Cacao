package tiles;

public class WorshipSite extends JungleTile {

    @Override
    public String toString() {
        return "WorshipSite{}"+" hashCode= "+System.identityHashCode(this);
    }

    @Override
    public String toShortString() {
        return "WORSH";
    }
}
