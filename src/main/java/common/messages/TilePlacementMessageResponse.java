package common.messages;

import common.game.Game;

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
        StringBuilder result = new StringBuilder();
        result.append(System.lineSeparator());
        result.append("----------------------START---");
        result.append(System.lineSeparator());
        result.append("common.game=" + game);
        result.append(System.lineSeparator());
        result.append("status=" + status);
        result.append(System.lineSeparator());
        result.append("textMessage=" + textMessage);
        result.append(System.lineSeparator());
        result.append("-----------------------END---");

        return result.toString();
    }
}
