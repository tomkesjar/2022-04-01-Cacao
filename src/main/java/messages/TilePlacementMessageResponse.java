package messages;

import game.Game;

import java.io.Serializable;

public class TilePlacementMessageResponse implements Serializable {

    private Game game;
    private ResponseStatus status;
    private String textMessage;

    public TilePlacementMessageResponse(Game game, ResponseStatus status, String textMessage) {
        this.game = game;
        this.status = status;
        this.textMessage = textMessage;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    @Override
    public String toString() {
        return "TilePlacementMessageResponse{" +
                "game=" + game +
                ", status=" + status +
                ", textMessage='" + textMessage + '\'' +
                '}';
    }
}
