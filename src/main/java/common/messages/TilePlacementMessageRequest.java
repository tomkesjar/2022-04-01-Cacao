package common.messages;

import common.tiles.AbstractTile;

import java.awt.*;
import java.io.Serializable;

public class TilePlacementMessageRequest implements Serializable {
    private Point coord;
    private AbstractTile tile;

    public TilePlacementMessageRequest(Point coord, AbstractTile tile) {
        this.coord = coord;
        this.tile = tile;
    }

    public Point getCoord() {
        return coord;
    }

    public void setCoord(Point coord) {
        this.coord = coord;
    }

    public AbstractTile getTile() {
        return tile;
    }

    public void setTile(AbstractTile tile) {
        this.tile = tile;
    }

    @Override
    public String toString() {
        return "TilePlacementMessageRequest{" +
                "coord=" + coord +
                ", tile=" + tile +
                '}';
    }
}
