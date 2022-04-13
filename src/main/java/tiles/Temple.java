package tiles;

public class Temple extends JungleTile {

    @Override
    public String toString() {
        return "Temple{}"+" hashCode= "+System.identityHashCode(this);
    }

    @Override
    public String toShortString() {
        return "TEMPL";
    }
}
