package board;

//import javafx.util.messages.Pair;
import messages.Pair;
import messages.TilePlacementMessageRequest;
import tiles.*;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Board implements Serializable {
    private final int INITIAL_HEIGHT = 10;//20;
    private final int INITIAL_WIDTH = 16;//30;
    private List<List<AbstractTile>> board;

    private AbstractTile freshWorkerTile;
    private AbstractTile freshJungleTile;

    private Point freshWorkerTilePoint;
    private Point freshJungleTilePoint;

    private List<Pair<Integer, Integer>> selectableJunglePanelPositions;
    private List<Pair<Integer, Integer>> selectableWorkerPanelPositions;

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
                tempList.add(originalBoard.getField(x,y).clone());
            }
            board.add(tempList);
        }
    }


    public void selectPossibleWorkerAndJungleTilesForPlacement(){
        selectableJunglePanelPositions = new ArrayList<>();
        selectableWorkerPanelPositions = new ArrayList<>();

        for (int y = 0; y < INITIAL_HEIGHT; ++y) {
            for (int x = 0; x < INITIAL_WIDTH; ++x) {
                if (isValidPlacementAsWorkerTile(x,y)){
                    selectableWorkerPanelPositions.add(new Pair(x,y));
                }
                if (isValidPlacementAsJungleTile(x,y)){
                    selectableJunglePanelPositions.add(new Pair(x,y));
                }
            }
        }
    }


    public boolean isValidPlacementAsWorkerTile(TilePlacementMessageRequest tilePlacementMessageRequest) {
        Point coord = new Point(tilePlacementMessageRequest.getCoord());

        return isValidPlacementAsWorkerTile(coord.x, coord.y);
    }

    public boolean isValidPlacementAsWorkerTile(int xCoord, int yCoord) {
        boolean positionCheck = (xCoord + yCoord) % 2 == 0;
        boolean emptyCheck = getField(xCoord, yCoord) instanceof EmptyTile;
        boolean isAdjacentToJungleTile = isAdjacentToJungleTile(xCoord, yCoord);

        return positionCheck && emptyCheck && isAdjacentToJungleTile;
    }

    private boolean isAdjacentToJungleTile(int xCoord, int yCoord) {
        boolean upNeighbour = isJungleTile(xCoord, yCoord - 1);
        boolean downNeighbour = isJungleTile(xCoord, yCoord + 1);
        boolean leftNeighbour = isJungleTile(xCoord - 1, yCoord);
        boolean rightNeighbour = isJungleTile(xCoord + 1, yCoord);

        return upNeighbour || downNeighbour || leftNeighbour || rightNeighbour;
    }

    private boolean isJungleTile(int xCoord, int yCoord) {
        if (xCoord < 0 || xCoord >= getWidth() || yCoord < 0 || yCoord >= getHeight()) {
            return false;
        }
        boolean notEmptyTile = getField(xCoord, yCoord).getTileEnum() != TileEnum.EMPTY;
        return getField(xCoord, yCoord) instanceof JungleTile && notEmptyTile;
    }


    public boolean isValidPlacementAsJungleTile(TilePlacementMessageRequest tilePlacementMessageRequest) {
        Point coord = new Point(tilePlacementMessageRequest.getCoord());
        return isValidPlacementAsJungleTile(coord.x, coord.y);
    }

    public boolean isValidPlacementAsJungleTile(int xCoord, int yCoord) {
        boolean positionCheck = (xCoord + yCoord) % 2 == 1;
        boolean emptyCheck = getField(xCoord, yCoord) instanceof EmptyTile;
        boolean isAdjacenttoWorkerTile = isAdjacentToWorkerTile(xCoord, yCoord);

        return positionCheck && emptyCheck && isAdjacenttoWorkerTile;
    }

    private boolean isAdjacentToWorkerTile(int xCoord, int yCoord) {
        boolean upNeighbour = isWorkerTile(xCoord, yCoord - 1);
        boolean downNeighbour = isWorkerTile(xCoord, yCoord + 1);
        boolean leftNeighbour = isWorkerTile(xCoord - 1, yCoord);
        boolean rightNeighbour = isWorkerTile(xCoord + 1, yCoord);

        return upNeighbour || downNeighbour || leftNeighbour || rightNeighbour;
    }

    private boolean isWorkerTile(int xCoord, int yCoord) {
        if (xCoord < 0 || xCoord >= getWidth() || yCoord < 0 || yCoord >= getHeight()) {
            return false;
        }
        return getField(xCoord, yCoord) instanceof WorkerTile;
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

    public List<Pair<Integer, Integer>> getSelectableJunglePanelPositions() {
        return selectableJunglePanelPositions;
    }

    public List<Pair<Integer, Integer>> getSelectableWorkerPanelPositions() {
        return selectableWorkerPanelPositions;
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
