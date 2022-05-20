package common.tiles;

import java.io.Serializable;

public abstract class AbstractTile implements Cloneable, Serializable {

    private TileEnum tileEnum;
    private int numberOfRotation = 0;

    public TileEnum getTileEnum() {
        return tileEnum;
    }

    public void setTileEnum(TileEnum tileEnum) {
        this.tileEnum = tileEnum;
    }

    public int getNumberOfRotation() {
        return numberOfRotation;
    }

    @Override
    public AbstractTile clone() {
        try {
            return (AbstractTile) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Clone not supported", e);
        }
    }

    public abstract String toShortString();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractTile that = (AbstractTile) o;

        return tileEnum == that.tileEnum;
    }

    @Override
    public int hashCode() {
        return tileEnum.hashCode();
    }
}
