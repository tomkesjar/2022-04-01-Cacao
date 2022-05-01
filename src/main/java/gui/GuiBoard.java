package gui;

import board.Board;
import connection.ClientConnection;
import game.Game;
import messages.TilePlacementMessageResponse;
import players.Player;
import tiles.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class GuiBoard extends JFrame implements Runnable {
    private ClientConnection connection;
    private ImageLoader imageLoader;

    private static final int TILES_MAX_HEIGHT = 60;
    private static final int TILES_MAX_WIDTH = 60;

    private static final int PANEL_MAX_WIDTH = 860;
    private static final int PANEL_MAX_HEIGHT = 900;

    private static final int INFOPANEL_HEIGHT = 30;

    private static final int BOARD_HORIZONTAL_GAP = 20;
    private static final int BOARD_VERTICAL_GAP = 20;

    private static final int FONT_SIZE = 10;

    private static final String TEXTBOX_PREFIX = "<html><p>";
    private static final String TEXTBOX_SUFFIX = "</p></html>";

    private volatile Game game;
    private final int playerIndex;
    private volatile WorkerTile selectedWorkerTile;
    private volatile JungleTile selectedJungleTile;

    private boolean hasPlacedWorkerTile;
    private boolean hasPlacedJungleTile;

    private JLabel messagePanel;

    private JPanel infoPanel;
    private JPanel boardPanel;
    private JPanel cardsPanel;


    private Map<Player, Map<String, JLabel>> playerPanelLink;
    private List<List<BoardTileButton>> boardTileButtonLink;
    private List<ActionButtonJungleTile> jungleCardsPanelLink;
    private List<ActionButtonWorkerTile> workerCardsPanelLink;

    private List<BoardTileButton> selectableJunglePanelLink;
    private List<BoardTileButton> selectableWorkerPanelLink;

    public GuiBoard(ClientConnection connection, Game game, int playerIndex) {
        super("Cacao Board Game - Player " + Objects.toString((int) (playerIndex + 1)));
        this.connection = connection;
        this.game = game;
        this.playerIndex = playerIndex;
        this.imageLoader = new ImageLoader(TILES_MAX_WIDTH, TILES_MAX_HEIGHT);
        this.selectableWorkerPanelLink = new ArrayList<>();
        this.selectableJunglePanelLink = new ArrayList<>();
        imageLoader.loadJungleImages();
        imageLoader.loadWorkerImages();

        selectedJungleTile = null;
        selectedWorkerTile = null;
        hasPlacedWorkerTile = false;
        hasPlacedJungleTile = false;

        playerPanelLink = new HashMap<>();
        boardTileButtonLink = new ArrayList<>();

        //this.setTitle("Cacao Board Game");
        this.setPreferredSize(new Dimension(PANEL_MAX_WIDTH, PANEL_MAX_HEIGHT));
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        String textMessage = "'s turn, select and place worker tile (Other players are inactive)";
        infoPanel = generateInfoPanel(game, textMessage);

        this.getContentPane().setLayout(new BorderLayout(BOARD_HORIZONTAL_GAP, BOARD_VERTICAL_GAP));
        this.getContentPane().add(infoPanel, BorderLayout.NORTH);

        boardPanel = createBoardPanel(game.getBoard());
        this.getContentPane().add(boardPanel, BorderLayout.EAST);

        cardsPanel = generateTilesPanel(game, playerIndex);
        this.getContentPane().add(cardsPanel, BorderLayout.SOUTH);


        jungleCardsPanelLink.forEach(tile -> {
            ImageIcon icon = new ImageIcon(allocateImageToTile(tile.getJungleTile().getNumberOfRotation(), tile.getJungleTile().getTileEnum()));
            tile.setIcon(icon);
        });

        workerCardsPanelLink.forEach(tile -> {
            ImageIcon icon = new ImageIcon(allocateImageToTile(tile.getWorkerTile().getNumberOfRotation(), tile.getWorkerTile().getTileEnum()));
            tile.setIcon(icon);
        });

        boardTileButtonLink.forEach(tileColumn -> {
            tileColumn.forEach(tile -> {
                ImageIcon icon = new ImageIcon(allocateImageToTile(tile.getTile().getNumberOfRotation(), tile.getTile().getTileEnum()));
                tile.setIcon(icon);
            });
        });

        collectSelectableJunglePanelLink();
        collectSelectableWorkerPanelLink();

        this.pack();    //ez rakja egybe
        this.setVisible(true);
        this.setFocusable(true);
        this.requestFocusInWindow();
    }

    public void updateGuiBoard(Game gameReceived, String textMessage) {
        System.out.println("[GuiBoard]: Before Update getSelectableWorkerPanelPositions=" + game.getBoard().getSelectableWorkerPanelPositions().toString() );
        this.game = gameReceived;
        System.out.println("[GuiBoard]: After Update getSelectableWorkerPanelPositions=" + game.getBoard().getSelectableWorkerPanelPositions().toString() );


        this.hasPlacedWorkerTile = game.hasPlacedWorkerTile();
        this.hasPlacedJungleTile = game.hasPlacedJungleTile();

        messagePanel.setText(game.getPlayerList().get(game.getActivePlayer()).getName() + ": " + textMessage);

        this.getContentPane().remove(0);
        infoPanel = generateInfoPanel(game, textMessage);
        this.getContentPane().setLayout(new BorderLayout(BOARD_HORIZONTAL_GAP, BOARD_VERTICAL_GAP));
        this.getContentPane().add(infoPanel, BorderLayout.NORTH);

        this.getContentPane().remove(0);
        boardPanel = createBoardPanel(game.getBoard());
        this.getContentPane().add(boardPanel, BorderLayout.CENTER);

        this.getContentPane().remove(0);
        cardsPanel = generateTilesPanel(game, playerIndex);
        this.getContentPane().add(cardsPanel, BorderLayout.SOUTH);

        jungleCardsPanelLink.forEach(tile -> {
            ImageIcon icon = new ImageIcon(allocateImageToTile(tile.getJungleTile().getNumberOfRotation(), tile.getJungleTile().getTileEnum()));
            tile.setIcon(icon);
        });

        workerCardsPanelLink.forEach(tile -> {
            ImageIcon icon = new ImageIcon(allocateImageToTile(tile.getWorkerTile().getNumberOfRotation(), tile.getWorkerTile().getTileEnum()));
            tile.setIcon(icon);
        });

        boardTileButtonLink.forEach(tileColumn -> {
            tileColumn.forEach(tile -> {
                ImageIcon icon = new ImageIcon(allocateImageToTile(tile.getTile().getNumberOfRotation(), tile.getTile().getTileEnum()));
                tile.setIcon(icon);
            });
        });

        collectSelectableJunglePanelLink();

        collectSelectableWorkerPanelLink();

        this.invalidate();
        this.repaint();
    }

    private void collectSelectableWorkerPanelLink() {
        selectableWorkerPanelLink = new ArrayList<>();
        boardTileButtonLink.forEach(tileRow -> tileRow.forEach(tile -> {
            game.getBoard().getSelectableWorkerPanelPositions().forEach(selectable ->{
                if (selectable.getKey() == tile.getCoord().x && selectable.getValue() == tile.getCoord().y){
                    selectableWorkerPanelLink.add(tile);
                }
            });
        }));
        System.out.println("[GuiBoard]: selectableWorkerPanelLink.size="+ selectableWorkerPanelLink.size());
    }

    private void collectSelectableJunglePanelLink() {
        selectableJunglePanelLink = new ArrayList<>();
        boardTileButtonLink.forEach(tileRow -> tileRow.forEach(tile -> {
            game.getBoard().getSelectableJunglePanelPositions().forEach(selectable ->{
                if (selectable.getKey() == tile.getCoord().x && selectable.getValue() == tile.getCoord().y){
                    selectableJunglePanelLink.add(tile);
                }
            });
        }));
    }

    private JPanel generatePlayerPanel(Player player) {
        JPanel panel = new JPanel();

        JLabel playerName = new JLabel(player.getName());
        panel.add(playerName);
        playerPanelLink.get(player).put("name", playerName);

        JLabel coinIcon = new JLabel("coin: "); //TODO: add coin icon
        JLabel coinValue = new JLabel(String.valueOf(player.getCoins()));
        panel.add(coinIcon);
        panel.add(coinValue);
        playerPanelLink.get(player).put("coinIcon", coinIcon);
        playerPanelLink.get(player).put("coinValue", coinValue);

        JLabel beanIcon = new JLabel("bean: "); //TODO: add coin icon
        JLabel beanValue = new JLabel(String.valueOf(player.getNumberOfCacaoBean()) + "/5");      //TODO: add boxes instead of number + colourify boxes based on number
        panel.add(beanIcon);
        panel.add(beanValue);
        playerPanelLink.get(player).put("beanIcon", beanIcon);
        playerPanelLink.get(player).put("beanValue", beanValue);

        JLabel shrineIcon = new JLabel("shrine: "); //TODO: add coin icon
        JLabel shrineValue = new JLabel(String.valueOf(player.getWorshipSymbol()) + "/" + String.valueOf(Game.getMaxNumberOfWorshipSites()));
        panel.add(shrineIcon);
        panel.add(shrineValue);
        playerPanelLink.get(player).put("shrineIcon", shrineIcon);
        playerPanelLink.get(player).put("shrineValue", shrineValue);

        JLabel templeIcon = new JLabel("temple: "); //TODO: add coin icon
        JLabel templeValue = new JLabel(String.valueOf(player.getTemplePoint()));       //TODO: add boxes instead of number + colourify boxes based on number
        panel.add(templeIcon);
        panel.add(templeValue);
        playerPanelLink.get(player).put("templeIcon", templeIcon);
        playerPanelLink.get(player).put("templeValue", templeValue);

        JLabel waterIcon = new JLabel("water: "); //TODO: add coin icon
        String nextLevelMessage = (player.getWaterPointIndex() + 1) >= Game.getWaterPositionValueList().size() ? "Maxed" : Objects.toString(Game.getWaterPositionValue(player.getWaterPointIndex() + 1));
        JLabel waterValue = new JLabel(String.valueOf(player.getWaterPoint()) + " (next level: " + nextLevelMessage + ")");      //TODO: add boxes instead of number + colourify boxes based on number
        panel.add(waterIcon);
        panel.add(waterValue);
        playerPanelLink.get(player).put("waterIcon", waterIcon);
        playerPanelLink.get(player).put("waterValue", waterValue);

        JLabel pointIcon = new JLabel("point: "); //TODO: add coin icon
        JLabel pointValue = new JLabel(String.valueOf(player.getPoint()) + " + " + String.valueOf(player.getTemplePointBonus()));       //TODO: add boxes instead of number + colourify boxes based on number
        panel.add(pointIcon);
        panel.add(pointValue);
        playerPanelLink.get(player).put("pointIcon", pointIcon);
        playerPanelLink.get(player).put("pointValue", pointValue);

        JLabel rankIcon = new JLabel("rank: "); //TODO: add coin icon
        JLabel rankValue = new JLabel(String.valueOf(player.getRank()));       //TODO: add boxes instead of number + colourify boxes based on number
        panel.add(rankIcon);
        panel.add(rankValue);
        playerPanelLink.get(player).put("rankIcon", rankIcon);
        playerPanelLink.get(player).put("rankValue", rankValue);

        panel.setLayout(new FlowLayout());

        return panel;
    }

    public BufferedImage allocateImageToTile(int numberOfRotation, TileEnum tileEnum) {
        BufferedImage image = null;

        switch (tileEnum) {
            //jungle tiles
            case MARKET_LOW:
                image = imageLoader.getMarket1();
                break;
            case MARKET_MID:
                image = imageLoader.getMarket2();
                break;
            case MARKET_HIGH:
                image = imageLoader.getMarket3();
                break;
            case MINE_1:
                image = imageLoader.getMine1();
                break;
            case MINE_2:
                image = imageLoader.getMine2();
                break;
            case PLANTATION_1:
                image = imageLoader.getPlantation1();
                break;
            case PLANTATION_2:
                image = imageLoader.getPlantation2();
                break;
            case WORSHIP_SITE:
                image = imageLoader.getSun();
                break;
            case TEMPLE:
                image = imageLoader.getTemple();
                break;
            case WATER:
                image = imageLoader.getWater();
                break;
            case EMPTY:
                image = imageLoader.getEmpty();
                break;


            //worker tiles
            case R1111:
                image = imageLoader.getR1111();
                break;
            case R2101:
            case R1012:
            case R0121:
            case R1210:
                image = imageLoader.getR2101();
                break;
            case R3001:
            case R0013:
            case R0130:
            case R1300:
                image = imageLoader.getR3001();
                break;
            case R3100:
            case R1003:
            case R0031:
            case R0310:
                image = imageLoader.getR3100();
                break;

            case B1111:
                image = imageLoader.getB1111();
                break;
            case B2101:
            case B1012:
            case B0121:
            case B1210:
                image = imageLoader.getB2101();
                break;
            case B3001:
            case B0013:
            case B0130:
            case B1300:
                image = imageLoader.getB3001();
                break;
            case B3100:
            case B1003:
            case B0031:
            case B0310:
                image = imageLoader.getB3100();
                break;

            case G1111:
                image = imageLoader.getG1111();
                break;
            case G2101:
            case G1012:
            case G0121:
            case G1210:
                image = imageLoader.getG2101();
                break;
            case G3001:
            case G0013:
            case G0130:
            case G1300:
                image = imageLoader.getG3001();
                break;
            case G3100:
            case G1003:
            case G0031:
            case G0310:
                image = imageLoader.getG3100();
                break;

            case Y1111:
                image = imageLoader.getY1111();
                break;
            case Y2101:
            case Y1012:
            case Y0121:
            case Y1210:
                image = imageLoader.getY2101();
                break;
            case Y3001:
            case Y0013:
            case Y0130:
            case Y1300:
                image = imageLoader.getY3001();
                break;
            case Y3100:
            case Y1003:
            case Y0031:
            case Y0310:
                image = imageLoader.getY3100();
                break;

            default:

        }
        BufferedImage rotatedImage = ImageLoader.rotateClockwise90(numberOfRotation, image);
        return rotatedImage;
    }

    @Override
    public void run() {
        while (!game.checkIfIsGameEnd()) {
            while (game.getActivePlayer() != this.getPlayerIndex()) {
                try {
                    TilePlacementMessageResponse response = (TilePlacementMessageResponse) this.getConnection().getObjectInputStream().readUnshared();
                    this.game = response.getGame();
                    this.updateGuiBoard(game, response.getTextMessage());

                    this.setVisible(true);
                    this.setFocusable(true);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            this.setVisible(true);
            this.setFocusable(true);
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private JPanel generateAllPlayersPanel(Game game) {
        JPanel allPlayersPanel = new JPanel();
        allPlayersPanel.setLayout(new BoxLayout(allPlayersPanel, BoxLayout.Y_AXIS));

        for (Player player : game.getPlayerList()) {
            playerPanelLink.put(player, new HashMap<>());
            JPanel playerPanel = generatePlayerPanel(player);
            playerPanel.setPreferredSize(new Dimension(PANEL_MAX_WIDTH, INFOPANEL_HEIGHT));

            Color selectedColour;
            switch (game.getPlayerList().indexOf(player)) {
                case 0:
                    selectedColour = Color.RED;
                    break;
                case 1:
                    selectedColour = Color.CYAN;
                    break;
                case 2:
                    selectedColour = Color.GREEN;
                    break;
                case 3:
                    selectedColour = Color.YELLOW;
                    break;
                default:
                    selectedColour = Color.GRAY;
            }

            playerPanel.setBackground(selectedColour);
            allPlayersPanel.add(playerPanel);
        }
        return allPlayersPanel;
    }

    private JPanel generateInfoPanel(Game game, String textMessage) {
        //if (Objects.nonNull(infoPanel) && infoPanel.getComponents().length >0) infoPanel.removeAll();
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BorderLayout(BOARD_HORIZONTAL_GAP, BOARD_VERTICAL_GAP));
        String labelText = game.getPlayerList().get(game.getActivePlayer()).getName() + ": " + textMessage;

        messagePanel = new JLabel(labelText);
        messagePanel.setPreferredSize(new Dimension(PANEL_MAX_WIDTH, INFOPANEL_HEIGHT));
        messagePanel.setHorizontalAlignment(SwingConstants.CENTER);
        infoPanel.add(messagePanel, BorderLayout.NORTH);

        JPanel allPlayersPanel = generateAllPlayersPanel(game);

        infoPanel.add(allPlayersPanel, BorderLayout.CENTER);
        return infoPanel;
    }


    private JPanel createBoardPanel(Board board) {
        JPanel result = new JPanel();
        result.setLayout(new GridLayout(board.getHeight(), board.getWidth()));

        for (int y = 0; y < board.getHeight(); ++y) {
            boardTileButtonLink.add(new ArrayList<BoardTileButton>());
            for (int x = 0; x < board.getWidth(); ++x) {
                BoardTileButton boardTileButton = new BoardTileButton(new Point(x, y), this);
                boardTileButton.setPreferredSize(new Dimension(TILES_MAX_WIDTH, TILES_MAX_HEIGHT));
                boardTileButton.setFont(new java.awt.Font("Calibri", 1, FONT_SIZE));
                //boardTileButton.setMaximumSize(new Dimension(TILES_MAX_WIDTH, TILES_MAX_HEIGHT));
                boardTileButtonLink.get(y).add(boardTileButton);
                result.add(boardTileButton);
            }
        }

        return result;
    }


    private JPanel generateTilesPanel(Game game, int playerIndex) {
        JPanel tilesPanel = new JPanel();
        tilesPanel.setLayout(new FlowLayout());

        jungleCardsPanelLink = new ArrayList<>();
        //add jungleTiles
        JPanel jungleTilesPanel = new JPanel();
        for (JungleTile jungleTile : game.getJungleTilesAvailable()) {
            ActionButtonJungleTile tileButton = new ActionButtonJungleTile(this, jungleTile);
            //tileButton.setText(jungleTile.toShortString());
            tileButton.setFont(new java.awt.Font("Calibri", 1, FONT_SIZE));
            tileButton.setPreferredSize(new Dimension(TILES_MAX_WIDTH, TILES_MAX_HEIGHT));
            jungleTilesPanel.add(tileButton);
            jungleCardsPanelLink.add(tileButton);
        }
        int remainingJungleDeckSize = game.getJungleTileDeck().getDeck().size();
        JLabel remainingJungleTileNumberLabel = new JLabel(String.valueOf(remainingJungleDeckSize));
        remainingJungleTileNumberLabel.setHorizontalAlignment(SwingConstants.CENTER);
        remainingJungleTileNumberLabel.setPreferredSize(new Dimension(TILES_MAX_WIDTH, TILES_MAX_HEIGHT));
        remainingJungleTileNumberLabel.setFont(new java.awt.Font("Georgia", 3, FONT_SIZE * 2));
        if (remainingJungleDeckSize < 2) {
            remainingJungleTileNumberLabel.setForeground(Color.RED);
        } else {
            remainingJungleTileNumberLabel.setForeground(Color.GRAY);
        }

        TitledBorder jungleLabelTitle = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Deck");
        jungleLabelTitle.setTitleJustification(TitledBorder.CENTER);
        remainingJungleTileNumberLabel.setBorder(jungleLabelTitle);

        jungleTilesPanel.add(remainingJungleTileNumberLabel);

        TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Jungle Tiles");
        title.setTitleJustification(TitledBorder.LEFT);
        jungleTilesPanel.setBorder(title);

        //add workerTiles
        //TODO: SOS selection based on guiBoard status?
        workerCardsPanelLink = new ArrayList<>();
        JPanel workerTilesPanel = new JPanel();
        Player currentPlayer = game.getPlayerList().get(playerIndex);

        for (WorkerTile workerTile : currentPlayer.getCardsAtHand()) {
            ActionButtonWorkerTile tileButton = new ActionButtonWorkerTile(this, workerTile);
            tileButton.setPreferredSize(new Dimension(TILES_MAX_WIDTH, TILES_MAX_HEIGHT));
            workerTilesPanel.add(tileButton);
            workerCardsPanelLink.add(tileButton);
        }
        int remainingWorkerDeckSize = currentPlayer.getWorkerTileDeck().getDeck().size();
        JLabel remainingWorkerTileNumberLabel = new JLabel(String.valueOf(remainingWorkerDeckSize));
        remainingWorkerTileNumberLabel.setHorizontalAlignment(SwingConstants.CENTER);
        remainingWorkerTileNumberLabel.setPreferredSize(new Dimension(TILES_MAX_WIDTH, TILES_MAX_HEIGHT));
        remainingWorkerTileNumberLabel.setFont(new java.awt.Font("Georgia", 3, FONT_SIZE * 2));
        if (remainingWorkerDeckSize < 2) {
            remainingWorkerTileNumberLabel.setForeground(Color.RED);
        } else {
            remainingWorkerTileNumberLabel.setForeground(Color.GRAY);
        }
        TitledBorder workerLabelTitle = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Deck");
        workerLabelTitle.setTitleJustification(TitledBorder.CENTER);
        remainingWorkerTileNumberLabel.setBorder(workerLabelTitle);
        workerTilesPanel.add(remainingWorkerTileNumberLabel);

        TitledBorder workerTitle = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Worker Tiles");
        workerTitle.setTitleJustification(TitledBorder.LEFT);
        workerTilesPanel.setBorder(workerTitle);

        tilesPanel.add(jungleTilesPanel);
        tilesPanel.add(workerTilesPanel);

        return tilesPanel;
    }


    public ClientConnection getConnection() {
        return connection;
    }

    public void setConnection(ClientConnection connection) {
        this.connection = connection;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    public WorkerTile getSelectedWorkerTile() {
        return selectedWorkerTile;
    }

    public void setSelectedWorkerTile(WorkerTile selectedWorkerTile) {
        this.selectedWorkerTile = selectedWorkerTile;
    }

    public JungleTile getSelectedJungleTile() {
        return selectedJungleTile;
    }

    public void setSelectedJungleTile(JungleTile selectedJungleTile) {
        this.selectedJungleTile = selectedJungleTile;
    }

    public JLabel getMessagePanel() {
        return messagePanel;
    }

    public void setMessagePanel(JLabel messagePanel) {
        this.messagePanel = messagePanel;
    }

    public boolean hasPlacedWorkerTile() {
        return hasPlacedWorkerTile;
    }

    public boolean hasPlacedJungleTile() {
        return hasPlacedJungleTile;
    }

    public void setHasPlacedWorkerTile(boolean hasPlacedWorkerTile) {
        this.hasPlacedWorkerTile = hasPlacedWorkerTile;
    }

    public void setHasPlacedJungleTile(boolean hasPlacedJungleTile) {
        this.hasPlacedJungleTile = hasPlacedJungleTile;
    }

    public JPanel getCardsPanel() {
        return cardsPanel;
    }

    public List<ActionButtonJungleTile> getJungleCardsPanelLink() {
        return jungleCardsPanelLink;
    }

    public List<ActionButtonWorkerTile> getWorkerCardsPanelLink() {
        return workerCardsPanelLink;
    }

    public List<List<BoardTileButton>> getBoardTileButtonLink() {
        return boardTileButtonLink;
    }

    public List<BoardTileButton> getSelectableJunglePanelLink() {
        return selectableJunglePanelLink;
    }

    public void setSelectableJunglePanelLink(List<BoardTileButton> selectableJunglePanelLink) {
        this.selectableJunglePanelLink = selectableJunglePanelLink;
    }

    public List<BoardTileButton> getSelectableWorkerPanelLink() {
        return selectableWorkerPanelLink;
    }

    public void setSelectableWorkerPanelLink(List<BoardTileButton> selectableWorkerPanelLink) {
        this.selectableWorkerPanelLink = selectableWorkerPanelLink;
    }
}
