import board.Board;
import players.PlayerColour;
import tiles.*;

import java.util.ArrayList;
import java.util.List;

public class DummyClass {
    public static void main(String[] args) {
        //checkMethodOne();
        checkMethodTwo();
    }

    public static void checkMethodOne() {

        List<AbstractTile> originalList = new ArrayList<>();

        originalList.add(new EmptyTile());
        originalList.add(new Market(Market.MarketPrice.LOW));
        originalList.add(new Mine(2));
        originalList.add(new Plantation(2));
        originalList.add(new Temple());
        originalList.add(new Water());
        originalList.add(new WorkerTile(1,2,3,4, PlayerColour.BLUE));
        originalList.add(new WorkerTile(1,2,3,4, PlayerColour.RED));
        originalList.add(new WorkerTile(1,2,3,4, PlayerColour.GREEN));
        originalList.add(new WorkerTile(1,2,3,4, PlayerColour.YELLOW));
        originalList.add(new WorshipSite());

        List<AbstractTile> newList = new ArrayList<>();
        for (AbstractTile tile : originalList){
            newList.add(tile.clone());
        }

        originalList.set(1, new Market(Market.MarketPrice.HIGH));
        originalList.set(2, new Mine(3));
        originalList.set(3, new Plantation(3));
        originalList.set(6, new WorkerTile(10,20,30,40, PlayerColour.YELLOW));
        originalList.set(7, new WorkerTile(10,20,30,40, PlayerColour.YELLOW));
        originalList.set(8, new WorkerTile(10,20,30,40, PlayerColour.YELLOW));
        originalList.set(9, new WorkerTile(10,20,30,40, PlayerColour.GREEN));

        originalList.forEach(tile -> System.out.println(tile.toString()));

        System.out.println("======================");
        newList.forEach(tile -> System.out.println(tile.toString()));
    }



    public static void checkMethodTwo() {
        Board originalBoard = new Board();
        originalBoard.setField(0,0, new Market(Market.MarketPrice.LOW));
        originalBoard.setField(0,1, new Mine(2));
        originalBoard.setField(1,0, new Plantation(2));
        originalBoard.setField(1,1, new Temple());
        originalBoard.setField(2,0, new Water());
        originalBoard.setField(2,1, new EmptyTile());
        originalBoard.setField(3,0, new WorkerTile(1,2,3,4, PlayerColour.BLUE));
        originalBoard.setField(3,1, new WorkerTile(1,2,3,4, PlayerColour.RED));
        originalBoard.setField(4,0, new WorkerTile(1,2,3,4, PlayerColour.GREEN));
        originalBoard.setField(4,1, new WorkerTile(1,2,3,4, PlayerColour.YELLOW));
        originalBoard.setField(5,0, new WorshipSite());


        Board copyBoard = new Board(originalBoard);

        originalBoard.setField(0,0, new Market(Market.MarketPrice.HIGH));
        originalBoard.setField(0,1, new Mine(3));
        originalBoard.setField(1,0, new Plantation(3));
        originalBoard.setField(1,1, new Water());
        originalBoard.setField(2,0, new EmptyTile());
        originalBoard.setField(2,1, new Temple());
        originalBoard.setField(3,0, new WorkerTile(10,21,3,4, PlayerColour.RED));
        originalBoard.setField(3,1, new WorkerTile(10,21,3,4, PlayerColour.GREEN));
        originalBoard.setField(4,0, new WorkerTile(10,21,3,4, PlayerColour.YELLOW));
        originalBoard.setField(4,1, new WorkerTile(10,21,3,4, PlayerColour.BLUE));
        originalBoard.setField(5,0, new Market(Market.MarketPrice.MID));

        System.out.println(originalBoard.toString());
        System.out.println("==========================");
        System.out.println(copyBoard.toString());

    }



}