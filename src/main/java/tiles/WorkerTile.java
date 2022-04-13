package tiles;

import players.PlayerColour;

public class WorkerTile extends AbstractTile {

    private int rightWorker;
    private int upWorker;
    private int leftWorker;
    private int downWorker;

    private String rightNeighbour;
    private String upNeighbour;
    private String leftNeighbour;
    private String downNeighbour;

    private String type;
    private PlayerColour colour;

    public void turnRightWorkersNinetyDegrees(){
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

    public PlayerColour getColour() {
        return colour;
    }

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

    @Override
    public String toString() {
        return "WorkerTile{" +
                "rightWorker=" + rightWorker +
                ", upWorker=" + upWorker +
                ", leftWorker=" + leftWorker +
                ", downWorker=" + downWorker +
                ", type='" + type + '\'' +
                ", colour=" + colour +
                '}'+" hashCode= "+System.identityHashCode(this);
    }

    @Override
    public String toShortString() {
        String colour = (getColour().toString());
        String shortColour = colour.substring(0, Math.min(colour.length(), 1));
        //return "W_"+ shortColour;
        return shortColour + this.rightWorker + this.upWorker + this.leftWorker + this.downWorker;
    }


}
