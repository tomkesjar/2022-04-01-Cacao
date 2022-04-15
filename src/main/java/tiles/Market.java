package tiles;

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
            case LOW: setTileType(TileEnum.MARKET_LOW); break;
            case MID: setTileType(TileEnum.MARKET_MID); break;
            case HIGH: setTileType(TileEnum.MARKET_HIGH); break;
        }

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
