package tiles;

public class Plantation extends JungleTile {
    private int numberOfCocoaBean;

    public Plantation(int numberOfCocoaBean) {
        this.numberOfCocoaBean = numberOfCocoaBean;
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
