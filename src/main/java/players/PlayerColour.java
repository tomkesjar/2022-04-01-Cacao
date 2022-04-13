package players;

import java.io.Serializable;

// not used
public enum PlayerColour implements Serializable {
    RED(1),
    BLUE(2),
    GREEN(3),
    YELLOW(4);

    int playerOrdinal = 0;

    private PlayerColour(int playerOrdinal){
        this.playerOrdinal = playerOrdinal;
    }

    public static PlayerColour getColourByOrdinal(int ordinal){
        for(PlayerColour colour : PlayerColour.values()){
            if (colour.playerOrdinal == ordinal) {
                return colour;
            }
        }
        return null;
    }

    public int getPlayerOrdinal() {
        return playerOrdinal;
    }
}

