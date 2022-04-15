package tiles;

import game.Game;
import players.Player;
import players.PlayerColour;

import java.awt.*;

public class WorkerTile extends AbstractTile {

    private int rightWorker;
    private int upWorker;
    private int leftWorker;
    private int downWorker;


    private String type;
    private PlayerColour colour;

    public void turnRightWorkersNinetyDegrees() {
        int temporaryValue = rightWorker;
        rightWorker = downWorker;
        downWorker = leftWorker;
        leftWorker = upWorker;
        upWorker = temporaryValue;
    }

    public WorkerTile(int rightWorker, int upWorker, int leftWorker, int downWorker, PlayerColour colour) {
        this.rightWorker = rightWorker;
        this.upWorker = upWorker;
        this.leftWorker = leftWorker;
        this.downWorker = downWorker;
        this.type = "Worker";
        this.colour = colour;
    }

    /*
        public String getRightNeighbour() {
            return rightNeighbour;
        }

        public void setRightNeighbour(String rightNeighbour) {
            this.rightNeighbour = rightNeighbour;
        }

        public String getUpNeighbour() {
            return upNeighbour;
        }

        public void setUpNeighbour(String upNeighbour) {
            this.upNeighbour = upNeighbour;
        }

        public String getLeftNeighbour() {
            return leftNeighbour;
        }

        public void setLeftNeighbour(String leftNeighbour) {
            this.leftNeighbour = leftNeighbour;
        }

        public String getDownNeighbour() {
            return downNeighbour;
        }

        public void setDownNeighbour(String downNeighbour) {
            this.downNeighbour = downNeighbour;
        }
    */
    public PlayerColour getColour() {
        return colour;
    }

    /*
    public boolean isWorkerNumbersMatchOnEachSide(WorkerTile otherTile){
        if (this.getColour() != otherTile.getColour()) {
            return false;
        }
        boolean original = this.getRightNeighbour() == otherTile.getRightNeighbour() && this.getUpNeighbour() == otherTile.getUpNeighbour() && this.getLeftNeighbour() == otherTile.getLeftNeighbour() && this.getDownNeighbour() == otherTile.getDownNeighbour();
        otherTile.turnRightWorkersNinetyDegrees();
        boolean rotateOne = this.getRightNeighbour() == otherTile.getRightNeighbour() && this.getUpNeighbour() == otherTile.getUpNeighbour() && this.getLeftNeighbour() == otherTile.getLeftNeighbour() && this.getDownNeighbour() == otherTile.getDownNeighbour();
        otherTile.turnRightWorkersNinetyDegrees();
        boolean rotateTwo = this.getRightNeighbour() == otherTile.getRightNeighbour() && this.getUpNeighbour() == otherTile.getUpNeighbour() && this.getLeftNeighbour() == otherTile.getLeftNeighbour() && this.getDownNeighbour() == otherTile.getDownNeighbour();
        otherTile.turnRightWorkersNinetyDegrees();
        boolean rotateThree = this.getRightNeighbour() == otherTile.getRightNeighbour() && this.getUpNeighbour() == otherTile.getUpNeighbour() && this.getLeftNeighbour() == otherTile.getLeftNeighbour() && this.getDownNeighbour() == otherTile.getDownNeighbour();

        return original || rotateOne || rotateTwo || rotateThree;
    }

     */

    @Override
    public String toString() {
        return "WorkerTile{" +
                "rightWorker=" + rightWorker +
                ", upWorker=" + upWorker +
                ", leftWorker=" + leftWorker +
                ", downWorker=" + downWorker +
                ", type='" + type + '\'' +
                ", colour=" + colour +
                '}' + " hashCode= " + System.identityHashCode(this);
    }

    @Override
    public String toShortString() {
        String colour = (getColour().toString());
        String shortColour = colour.substring(0, Math.min(colour.length(), 1));
        //return "W_"+ shortColour;
        return shortColour + this.rightWorker + this.upWorker + this.leftWorker + this.downWorker;
    }


    public void processRightNeighbourOfWorker(Point coord, Game game) {
        Player activePlayer = game.getPlayerList().get(game.getActivePlayer());

        processNeighbour(new Point(coord.x + 1, coord.y), game, leftWorker);

    }

    public void processLeftNeighbourOfWorker(Point coord, Game game) {
        Player activePlayer = game.getPlayerList().get(game.getActivePlayer());

        processNeighbour(new Point(coord.x - 1, coord.y), game, leftWorker);

    }

    public void processDownNeighbourOfWorker(Point coord, Game game) {
        Player activePlayer = game.getPlayerList().get(game.getActivePlayer());

        processNeighbour(new Point(coord.x, coord.y+1), game, leftWorker);

    }

    public void processUpNeighbourOfWorker(Point coord, Game game) {
        Player activePlayer = game.getPlayerList().get(game.getActivePlayer());

        processNeighbour(new Point(coord.x, coord.y-1), game, leftWorker);

    }

    private void processNeighbour(Point coord, Game game, int numberOfLeftWorker) {
        if (!(coord.x < 0 || coord.x >= game.getBoard().getWidth() || coord.y < 0 || coord.y >= game.getBoard().getHeight())) {
            Player activePlayer = game.getPlayerList().get(game.getActivePlayer());

            JungleTile neighbourJungleTile = (JungleTile) game.getBoard().getField(coord.x, coord.y);

            switch (neighbourJungleTile.getTileType()) {
                case WATER:
                    for(int i=0; i< numberOfLeftWorker; ++i){
                        if(activePlayer.getWaterPointIndex() < Game.getWaterPositionValueList().size()) {
                            activePlayer.setWaterPointIndex(activePlayer.getWaterPointIndex() + 1);       //TODO: meet actual game rule
                            activePlayer.setWaterPoint(Game.getWaterPositionValue(activePlayer.getWaterPointIndex()));
                        }
                    }
                    break;
                case TEMPLE:
                    for(int i=0; i< numberOfLeftWorker; ++i) {
                        activePlayer.setTemplePoint(activePlayer.getTemplePoint() + 1);
                    }
                    break;
                case WORSHIP_SITE:
                    for(int i=0; i< numberOfLeftWorker; ++i) {
                        activePlayer.setWorshipSymbol(Math.min(activePlayer.getWorshipSymbol() + 1, Game.getMaxNumberOfWorshipSites()));
                    }
                    break;
                case MINE_1:
                    for(int i=0; i< numberOfLeftWorker; ++i) {
                        activePlayer.setCoins(activePlayer.getCoins() + 1);
                    }
                    break;
                case MINE_2:
                    for(int i=0; i< numberOfLeftWorker; ++i) {
                        activePlayer.setCoins(activePlayer.getCoins() + 2);
                    }
                    break;
                case PLANTATION_1:
                    for(int i=0; i< numberOfLeftWorker; ++i) {
                        activePlayer.setNumberOfCacaoBean(Math.min(activePlayer.getNumberOfCacaoBean() + 1, Game.getMaxNumberOfCacaoBeans()));
                    }
                    break;
                case PLANTATION_2:
                    for(int i=0; i< numberOfLeftWorker; ++i) {
                        activePlayer.setNumberOfCacaoBean(Math.min(activePlayer.getNumberOfCacaoBean() + 2, Game.getMaxNumberOfCacaoBeans()));
                    }
                    break;

                case MARKET_LOW:
                    for(int i=0; i< numberOfLeftWorker; ++i) {
                        if (activePlayer.getNumberOfCacaoBean() > 0) {
                            activePlayer.setNumberOfCacaoBean(activePlayer.getNumberOfCacaoBean() - 1);
                            activePlayer.setCoins(activePlayer.getCoins() + Market.MarketPrice.LOW.getValue());
                        }
                    }
                    break;
                case MARKET_MID:
                    for(int i=0; i< numberOfLeftWorker; ++i) {
                        if (activePlayer.getNumberOfCacaoBean() > 0) {
                            activePlayer.setNumberOfCacaoBean(activePlayer.getNumberOfCacaoBean() - 1);
                            activePlayer.setCoins(activePlayer.getCoins() + Market.MarketPrice.MID.getValue());
                        }
                    }
                    break;
                case MARKET_HIGH:
                    for(int i=0; i< numberOfLeftWorker; ++i) {
                        if (activePlayer.getNumberOfCacaoBean() > 0) {
                            activePlayer.setNumberOfCacaoBean(activePlayer.getNumberOfCacaoBean() - 1);
                            activePlayer.setCoins(activePlayer.getCoins() + Market.MarketPrice.HIGH.getValue());
                        }
                    }
                    break;
            }


        }
    }
}
