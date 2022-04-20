package board;

import tiles.AbstractTile;
import tiles.EmptyTile;
import tiles.Market;
import tiles.Plantation;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Board implements Serializable {
    private final int INITIAL_HEIGHT = 12;//20;
    private final int INITIAL_WIDTH = 14;//30;
    private List<List<AbstractTile>> board;

    private AbstractTile freshWorkerTile;
    private AbstractTile freshJungleTile;

    private Point freshWorkerTilePoint;
    private Point freshJungleTilePoint;

    public Board() {
        board = new ArrayList<>();
        for (int y = 0; y < INITIAL_HEIGHT; ++y) {
            List<AbstractTile> tempList = new ArrayList<>();
            for (int x = 0; x < INITIAL_WIDTH; ++x) {
                tempList.add(new EmptyTile());
            }
            board.add(tempList);
        }

        int midXCoord = (int) Math.floor(INITIAL_WIDTH / 2) -1;
        int midYCoord = (int) Math.floor(INITIAL_HEIGHT / 2) -1;

        setField(midXCoord, midYCoord, new Plantation(1));
        setField(midXCoord+1, midYCoord+1, new Market(Market.MarketPrice.LOW));
    }

    public Board(Board originalBoard){
        board = new ArrayList<>();
        for (int y = 0; y < INITIAL_HEIGHT; ++y) {
            List<AbstractTile> tempList = new ArrayList<>();
            for (int x = 0; x < INITIAL_WIDTH; ++x) {
                //AbstractTile newTile = originalBoard.getField(x,y);
                tempList.add(originalBoard.getField(x,y).clone());
            }
            board.add(tempList);
        }
    }





    public AbstractTile getField(int xCoord, int yCoord) {
        if (xCoord >= INITIAL_WIDTH || xCoord < 0 || yCoord >= INITIAL_HEIGHT || yCoord < 0) {
            throw new IllegalArgumentException("Coordinates are not in range, xCoord=" + xCoord + " yCoord=" + yCoord);
        }
        return board.get(yCoord).get(xCoord);
    }

    public void setField (int xCoord, int yCoord, AbstractTile newTile) {
        if (xCoord >= INITIAL_WIDTH || xCoord < 0 || yCoord >= INITIAL_HEIGHT || yCoord < 0) {
            throw new IllegalArgumentException("Coordinates are not in range, xCoord=" + xCoord + " yCoord=" + yCoord);
        }
        board.get(yCoord).set(xCoord, newTile);
    }

    public int getHeight() {
        return INITIAL_HEIGHT;
    }

    public int getWidth() {
        return INITIAL_WIDTH;
    }

    public AbstractTile getFreshWorkerTile() {
        return freshWorkerTile;
    }

    public void setFreshWorkerTile(AbstractTile freshWorkerTile) {
        this.freshWorkerTile = freshWorkerTile;
    }

    public AbstractTile getFreshJungleTile() {
        return freshJungleTile;
    }

    public void setFreshJungleTile(AbstractTile freshJungleTile) {
        this.freshJungleTile = freshJungleTile;
    }

    public Point getFreshWorkerTilePoint() {
        return freshWorkerTilePoint;
    }

    public void setFreshWorkerTilePoint(Point freshWorkerTilePoint) {
        this.freshWorkerTilePoint = freshWorkerTilePoint;
    }

    public Point getFreshJungleTilePoint() {
        return freshJungleTilePoint;
    }

    public void setFreshJungleTilePoint(Point freshJungleTilePoint) {
        this.freshJungleTilePoint = freshJungleTilePoint;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for(int y=0; y<INITIAL_HEIGHT; ++y){
            result.append(System.lineSeparator());
            result.append("----- row " + (int)(y+1) + " -----");
            result.append(System.lineSeparator());
            for(int x=0; x<INITIAL_WIDTH; ++x){
                result.append(this.getField(x,y).toShortString() + " |");
            }
        }
        return result.toString();
    }

    public String toShortString() {
        StringBuilder result = new StringBuilder();
        result.append("freshWorkerTile=" + freshWorkerTile.toShortString() + " , " + freshWorkerTilePoint.x + " : " + freshWorkerTilePoint.y);
        result.append(System.lineSeparator());

        if (Objects.nonNull(freshJungleTile)) {
            result.append("freshJungleTile=" + freshJungleTile.toShortString() + " , " + freshJungleTilePoint.x + " : " + freshJungleTilePoint.y);
        }else{
            result.append("freshJungleTile= null");
        }

        return result.toString();
    }
}
