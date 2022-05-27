package common.tiles;

import common.game.Game;
import common.messages.Pair;
import common.players.Player;

import java.awt.*;
import java.util.Optional;

public class Market extends JungleTile {
    public enum MarketPrice{
        LOW(2),
        MID(3),
        HIGH(4);

        private int value;

        MarketPrice(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private MarketPrice marketPrice;

    public Market(MarketPrice marketPrice) {
        this.marketPrice = marketPrice;
        switch (this.marketPrice){
            case LOW: setTileEnum(TileEnum.MARKET_LOW); break;
            case MID: setTileEnum(TileEnum.MARKET_MID); break;
            case HIGH: setTileEnum(TileEnum.MARKET_HIGH); break;
        }

    }

    @Override
    protected void processNeighbour(Point coord, Game game, int numberOfWorker) {
        if (!(coord.x < 0 || coord.x >= game.getBoard().getWidth() || coord.y < 0 || coord.y >= game.getBoard().getHeight())) {
            WorkerTile neighbour = (WorkerTile) game.getBoard().getField(coord.x, coord.y);
            int activePlayerIndex = neighbour.getColour().getPlayerOrdinal()-1;
            Player activePlayer = game.getPlayerList().get(activePlayerIndex);

            for (int i=0; i<numberOfWorker; ++i){
                if (activePlayer.getNumberOfCacaoBean() > 0) {
                    activePlayer.setNumberOfCacaoBean(activePlayer.getNumberOfCacaoBean() - 1);
                    activePlayer.setCoins(activePlayer.getCoins() + getMarketPrice().getValue());
                }
            }
        }
    }

    @Override
    public Optional<Pair<Integer,Pair<Integer, Integer>>> processNeighbourForRobotEvaluation(Point coord, Game game, int numberOfWorker) {
        if (!(coord.x < 0 || coord.x >= game.getBoard().getWidth() || coord.y < 0 || coord.y >= game.getBoard().getHeight())) {
            WorkerTile neighbour = (WorkerTile) game.getBoard().getField(coord.x, coord.y);
            int activePlayerIndex = neighbour.getColour().getPlayerOrdinal()-1;
            Player activePlayer = game.getPlayerList().get(activePlayerIndex);

            int startingNumberOfCocaBean = activePlayer.getNumberOfCacaoBean();
            int startingValue = activePlayer.getCoins();

            for (int i=0; i<numberOfWorker; ++i){
                if (activePlayer.getNumberOfCacaoBean() > 0) {
                    activePlayer.setNumberOfCacaoBean(activePlayer.getNumberOfCacaoBean() - 1);
                    activePlayer.setCoins(activePlayer.getCoins() + getMarketPrice().getValue());
                }
            }

            int endingNumberOfCocaBean = activePlayer.getNumberOfCacaoBean();
            int endingValue = activePlayer.getCoins();

            int diffNumberOfCocaBean = endingNumberOfCocaBean - startingNumberOfCocaBean;
            int diffValue = endingValue - startingValue;
            activePlayer.setNumberOfCacaoBean(startingNumberOfCocaBean);
            activePlayer.setCoins(startingValue);

            return Optional.of(new Pair<>(activePlayerIndex, new Pair<>(diffNumberOfCocaBean,diffValue) ));
        }
        return Optional.empty();
    }

    @Override
    public AbstractTile clone() {
        Market cloned = (Market) super.clone();
        MarketPrice clonedMarketPrice= this.getMarketPrice();
        cloned.setMarketPrice((MarketPrice)cloned.getMarketPrice());
        return cloned;
    }

    public MarketPrice getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(MarketPrice marketPrice) {
        this.marketPrice = marketPrice;
    }

    @Override
    public String toString() {
        return "Market{" +
                "marketPrice=" + marketPrice +
                '}'+" hashCode= "+System.identityHashCode(this);
    }

    @Override
    public String toShortString() {
        return "MARK"+getMarketPrice().value;
    }
}
