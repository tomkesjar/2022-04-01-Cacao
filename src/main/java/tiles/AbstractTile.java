package tiles;

import java.io.Serializable;

public abstract class AbstractTile implements Cloneable, Serializable {

    private TileEnum tileType;

    public TileEnum getTileType() {
        return tileType;
    }

    public void setTileType(TileEnum tileType) {
        this.tileType = tileType;
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
}
