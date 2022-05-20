package client.gui;

import common.board.Board;
import client.connection.ClientConnection;
import common.game.Game;
import common.game.GameHandler;
import common.messages.Pair;
import common.messages.ResponseStatus;
import common.messages.TilePlacementMessageResponse;
import common.players.Player;
import common.tiles.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class GuiBoard extends JFrame implements Runnable {
    public enum GameType{
        SINGLE,
        MULTI
    }

    private ClientConnection gameConnection;
    private ClientConnection chatConnection;
    private ImageLoader imageLoader;

    private Image backgroundImage = null;


    private static final String FILE_NAME = "/background/jungleBackground.png";
    private static final int OPACITY_LEVEL_HIGH = 85;
    private static final int OPACITY_LEVEL_LOW = 25;

    private static final int TILES_MAX_HEIGHT = 62;
    private static final int TILES_MAX_WIDTH = 62;

    private static int PANEL_MAX_WIDTH = 1_290;
    private static final int PANEL_MAX_HEIGHT = 832;

    private static GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();


    private static final int INFOPANEL_UNIT_HEIGHT = 16;

    private static final int BOARD_HORIZONTAL_GAP = 0;
    private static final int BOARD_VERTICAL_GAP = 0;

    private static final int FONT_SIZE = 10;
    private static final int MESSAGE_PANEL_FONT_SIZE = 18;

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
    private ChatBoxPanel chatBoxPanel;

    private JPanel contentPane;

    private final GameHandler gameHandlerForSinglePlayer;
    private GameType gameType;


    private Map<Player, Map<String, JLabel>> playerPanelLink;
    private List<List<AbstractBoardTileButton>> boardTileButtonLink;
    private List<ActionButtonJungleTile> jungleCardsPanelLink;
    private List<ActionButtonWorkerTile> workerCardsPanelLink;

    private Set<AbstractBoardTileButton> selectableJunglePanelLink;
    private Set<AbstractBoardTileButton> selectableWorkerPanelLink;

    //*****************************************************************************************
    public GuiBoard(GameHandler gameHandler){
        super("Cacao Board Game");
        this.gameHandlerForSinglePlayer = gameHandler;
        this.game = gameHandler.getGame();
        this.gameType = GameType.SINGLE;

        this.gameConnection = null;
        this.chatConnection = null;
        this.playerIndex = 0;
        PANEL_MAX_WIDTH = 1_000;

        loadImages();
        initializeCommonVariables();
        String textMessage = "'s turn, select and place worker tile";

        createInitialDesign(textMessage);
        collectJungleCardsPanelLink();
        collectWorkerCardsPanelLink();
        collectBoardTileButtonLink();

        gameHandler.getGame().getBoard().selectPossibleWorkerAndJungleTilesForPlacement();
        collectSelectableJunglePanelLink();
        collectSelectableWorkerPanelLink();

        this.pack();    //ez rakja egybe
        this.setVisible(true);
        this.requestFocusInWindow();
    }

    protected void collectBoardTileButtonLink() {

        boardTileButtonLink.forEach(tileColumn -> {
            tileColumn.forEach(tile -> {
                if (tile.getTile().getTileEnum() != TileEnum.EMPTY) {
                    ImageIcon icon = new ImageIcon(allocateImageToTile(tile.getTile().getNumberOfRotation(), tile.getTile().getTileEnum()));
                    tile.setIcon(icon);
                } else {
                    tile.setBackground(new Color(0, 0, 0, OPACITY_LEVEL_LOW));
                    tile.setOpaque(false);
                    tile.setContentAreaFilled(false);
                    //tile.setBorderPainted(false);

                }
            });
        });
    }

    protected void collectWorkerCardsPanelLink() {
        workerCardsPanelLink.forEach(tile -> {
            ImageIcon icon = new ImageIcon(allocateImageToTile(tile.getWorkerTile().getNumberOfRotation(), tile.getWorkerTile().getTileEnum()));
            tile.setIcon(icon);
        });
    }

    protected void collectJungleCardsPanelLink() {
        jungleCardsPanelLink.forEach(tile -> {
            ImageIcon icon = new ImageIcon(allocateImageToTile(tile.getJungleTile().getNumberOfRotation(), tile.getJungleTile().getTileEnum()));
            tile.setIcon(icon);
        });
    }

    protected void createInitialDesign(String textMessage) {

        loadBackgroundImage();

        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
            }
        };

        contentPane.setLayout(new GridBagLayout());
        this.setContentPane(contentPane);

        this.setPreferredSize(new Dimension(PANEL_MAX_WIDTH, PANEL_MAX_HEIGHT));
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        infoPanel = generateInfoPanel(game, textMessage);

        this.getContentPane().setLayout(new BorderLayout(BOARD_HORIZONTAL_GAP, BOARD_VERTICAL_GAP));
        this.getContentPane().add(infoPanel, BorderLayout.NORTH);

        boardPanel = createBoardPanel(game.getBoard());
        //this.getContentPane().add(boardPanel, BorderLayout.EAST);
        this.getContentPane().add(boardPanel, BorderLayout.CENTER);

        cardsPanel = generateTilesPanel(game, playerIndex);
        this.getContentPane().add(cardsPanel, BorderLayout.SOUTH);
    }

    private void addChatBoxPanel() {
        chatBoxPanel = new ChatBoxPanel(this.chatConnection, 14, OPACITY_LEVEL_HIGH);
        this.getContentPane().add(chatBoxPanel, BorderLayout.WEST);
        new Thread(chatBoxPanel).start();
    }

    public GuiBoard(ClientConnection gameConnection, ClientConnection chatConnection, Game game, int playerIndex) {
        super("Cacao Board Game - Player " + Objects.toString((int) (playerIndex + 1)));
        this.setResizable(false);
        this.gameType = GameType.MULTI;

        this.gameHandlerForSinglePlayer = null;
        this.gameConnection = gameConnection;
        this.chatConnection = chatConnection;
        this.game = game;
        this.playerIndex = playerIndex;
        loadImages();

        initializeCommonVariables();
        String textMessage = "'s turn, select and place worker tile (Other common.players are inactive)";
        createInitialDesign(textMessage);
        addChatBoxPanel();
        collectJungleCardsPanelLink();
        collectWorkerCardsPanelLink();
        collectBoardTileButtonLink();
        collectSelectableJunglePanelLink();
        collectSelectableWorkerPanelLink();

        this.pack();    //ez rakja egybe
        this.setVisible(true);
        //this.setFocusable(true);
        this.requestFocusInWindow();
    }

    protected void initializeCommonVariables() {
        this.selectableWorkerPanelLink = new HashSet<>();
        this.selectableJunglePanelLink = new HashSet<>();
        selectedJungleTile = null;
        selectedWorkerTile = null;
        hasPlacedWorkerTile = false;
        hasPlacedJungleTile = false;
        playerPanelLink = new HashMap<>();
        boardTileButtonLink = new ArrayList<>();
    }

    protected void loadImages() {
        this.imageLoader = new ImageLoader(TILES_MAX_WIDTH, TILES_MAX_HEIGHT);
        imageLoader.loadJungleImages();
        imageLoader.loadWorkerImages();
        imageLoader.loadIconImages();
    }

    public void updateGuiBoard(Game gameReceived, String textMessage) {
        System.out.println("[GuiBoard]: Before Update getSelectableWorkerPanelPositions=" + game.getBoard().getSelectableWorkerPanelPositions().toString() );
        System.out.println("[GuiBoard]: Before getSelectableWorkerPanelPositions=" + game.getBoard().getSelectableWorkerPanelPositions() );
        this.game = gameReceived;
        System.out.println("[GuiBoard]: After Update getSelectableWorkerPanelPositions=" + game.getBoard().getSelectableWorkerPanelPositions().toString() );


        this.hasPlacedWorkerTile = game.hasPlacedWorkerTile();
        this.hasPlacedJungleTile = game.hasPlacedJungleTile();

        messagePanel.setText(game.getPlayerList().get(game.getActivePlayer()).getName() + ": " + textMessage);

        this.getContentPane().remove(0);

        infoPanel = generateInfoPanel(game, textMessage);
        this.getContentPane().setLayout(new BorderLayout(BOARD_HORIZONTAL_GAP, BOARD_VERTICAL_GAP));
        this.getContentPane().add(infoPanel, BorderLayout.NORTH, 0);

        this.getContentPane().remove(1);
        boardPanel = createBoardPanel(game.getBoard());
        this.getContentPane().add(boardPanel, BorderLayout.EAST, 1);


        this.getContentPane().remove(2);
        cardsPanel = generateTilesPanel(game, playerIndex);
        this.getContentPane().add(cardsPanel, BorderLayout.SOUTH,2);

        //chatBox panel will not be removed! it is on index=3

        collectJungleCardsPanelLink();

        collectWorkerCardsPanelLink();

        collectBoardTileButtonLink();

        collectSelectableJunglePanelLink();
        collectSelectableWorkerPanelLink();

        this.invalidate();
        this.repaint();
    }

    private void collectSelectableWorkerPanelLink() {
        selectableWorkerPanelLink = new HashSet<>();

        /*
        boardTileButtonLink.forEach(tileRow -> tileRow.forEach(tile -> {
            common.game.getBoard().getSelectableWorkerPanelPositions().forEach(selectable ->{
                if (selectable.getKey() == tile.getCoord().x && selectable.getValue() == tile.getCoord().y){
                    selectableWorkerPanelLink.add(tile);
                }
            });
        }));
         */
        for (List<AbstractBoardTileButton> tileRow : boardTileButtonLink) {
            for (AbstractBoardTileButton tile : tileRow) {
                for (Pair<Integer, Integer> selectable : game.getBoard().getSelectableWorkerPanelPositions()) {
                    if (selectable.getKey() == tile.getCoord().x && selectable.getValue() == tile.getCoord().y) {
                        selectableWorkerPanelLink.add(tile);
                    }
                }
            }
        }
        System.out.println("[GuiBoard]: selectableWorkerPanelLink.size="+ selectableWorkerPanelLink.size());
    }

    private void collectSelectableJunglePanelLink() {
        selectableJunglePanelLink = new HashSet<>();
        boardTileButtonLink.forEach(tileRow -> tileRow.forEach(tile -> {
            game.getBoard().getSelectableJunglePanelPositions().forEach(selectable ->{
                if (selectable.getKey() == tile.getCoord().x && selectable.getValue() == tile.getCoord().y){
                    selectableJunglePanelLink.add(tile);
                }
            });
        }));
        System.out.println("[GuiBoard]: selectableJunglePanelLink.size="+ selectableJunglePanelLink.size());
    }

    private JPanel generatePlayerPanel(Player player) {
        JPanel panel = new JPanel();

        JLabel playerName = new JLabel(player.getName());
        playerName.setForeground(Color.WHITE);
        panel.add(playerName);
        playerPanelLink.get(player).put("name", playerName);

        JLabel beanIcon = new JLabel(new ImageIcon((BufferedImage) imageLoader.getBeanIcon())); //TODO: add coin icon
        JLabel beanValue = new JLabel(String.valueOf(player.getNumberOfCacaoBean()) + "/5");      //TODO: add boxes instead of number + colourify boxes based on number
        beanValue.setForeground(Color.WHITE);
        panel.add(beanIcon);
        panel.add(beanValue);
        playerPanelLink.get(player).put("beanIcon", beanIcon);
        playerPanelLink.get(player).put("beanValue", beanValue);

        JLabel coinIcon = new JLabel(new ImageIcon((BufferedImage) imageLoader.getCoinIcon())); //TODO: add coin icon
        JLabel coinValue = new JLabel(String.valueOf(player.getCoins()));      //TODO: add boxes instead of number + colourify boxes based on number
        coinValue.setForeground(Color.WHITE);
        panel.add(coinIcon);
        panel.add(coinValue);
        playerPanelLink.get(player).put("coinIcon", coinIcon);
        playerPanelLink.get(player).put("coinValue", coinValue);

        JLabel shrineIcon = new JLabel(new ImageIcon((BufferedImage) imageLoader.getShrineIcon())); //TODO: add coin icon
        JLabel shrineValue = new JLabel(String.valueOf(player.getWorshipSymbol()) + "/" + String.valueOf(Game.getMaxNumberOfWorshipSites()));
        shrineValue.setForeground(Color.WHITE);
        panel.add(shrineIcon);
        panel.add(shrineValue);
        playerPanelLink.get(player).put("shrineIcon", shrineIcon);
        playerPanelLink.get(player).put("shrineValue", shrineValue);

        JLabel templeIcon = new JLabel(new ImageIcon((BufferedImage) imageLoader.getTempleIcon())); //TODO: add coin icon
        JLabel templeValue = new JLabel(String.valueOf(player.getTemplePoint()));       //TODO: add boxes instead of number + colourify boxes based on number
        templeValue.setForeground(Color.WHITE);
        panel.add(templeIcon);
        panel.add(templeValue);
        playerPanelLink.get(player).put("templeIcon", templeIcon);
        playerPanelLink.get(player).put("templeValue", templeValue);

        JLabel waterIcon = new JLabel(new ImageIcon((BufferedImage) imageLoader.getWaterIcon())); //TODO: add coin icon
        String nextLevelMessage = (player.getWaterPointIndex() + 1) >= Game.getWaterPositionValueList().size() ? "Maxed" : Objects.toString(Game.getWaterPositionValue(player.getWaterPointIndex() + 1));
        JLabel waterValue = new JLabel(String.valueOf(player.getWaterPoint()) + " (next level: " + nextLevelMessage + ")");      //TODO: add boxes instead of number + colourify boxes based on number
        waterValue.setForeground(Color.WHITE);
        panel.add(waterIcon);
        panel.add(waterValue);
        playerPanelLink.get(player).put("waterIcon", waterIcon);
        playerPanelLink.get(player).put("waterValue", waterValue);

        JLabel pointIcon = new JLabel(new ImageIcon((BufferedImage) imageLoader.getPointIcon())); //TODO: add coin icon
        JLabel pointValue = new JLabel(String.valueOf(player.getPoint()-player.getTemplePointBonus()) + " + " + String.valueOf(player.getTemplePointBonus()));       //TODO: add boxes instead of number + colourify boxes based on number
        pointValue.setForeground(Color.WHITE);
        panel.add(pointIcon);
        panel.add(pointValue);
        playerPanelLink.get(player).put("pointIcon", pointIcon);
        playerPanelLink.get(player).put("pointValue", pointValue);

        JLabel rankIcon = new JLabel(new ImageIcon((BufferedImage) imageLoader.getRankIcon())); //TODO: add coin icon
        JLabel rankValue = new JLabel(String.valueOf(player.getRank()));       //TODO: add boxes instead of number + colourify boxes based on number
        rankValue.setForeground(Color.WHITE);
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
            //jungle common.tiles
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


            //worker common.tiles
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
        ResponseStatus messageStatus = null;

        while (!game.isGameEnded() && !(ResponseStatus.FINAL == messageStatus)) {
            while (game.getActivePlayer() != this.getPlayerIndex()) {
                try {
                    TilePlacementMessageResponse response = (TilePlacementMessageResponse) this.getGameConnection().getObjectInputStream().readUnshared();
                    messageStatus = response.getStatus();
                    this.game = response.getGame();
                    System.out.println("[GuiBoard]: void run: updated common.game.activePlayer=" + game.getActivePlayer());
                    this.updateGuiBoard(game, response.getTextMessage());

                    this.setVisible(true);
                    //this.setFocusable(true);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            //own turn, simple waiting
            this.setVisible(true);
            //this.setFocusable(true);
            try {
                System.out.println("[GuiBoard]: void Sleep: current common.game.activePlayer=" + game.getActivePlayer());
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            TilePlacementMessageResponse response = (TilePlacementMessageResponse) this.getGameConnection().getObjectInputStream().readUnshared();
            this.game = response.getGame();

            GuiEndGameResult guiEndGameResult = new GuiEndGameResult(game);
            System.out.println("[GuiBoard]: guiEndGameResult created");

            guiEndGameResult.setVisible(true);
            guiEndGameResult.setFocusable(true);
            guiEndGameResult.requestFocusInWindow();

        }catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void generateAllPlayersPanel(Game game, JPanel infoPanel, int index, GridBagConstraints c) {
        playerPanelLink.clear();
        for (Player player : game.getPlayerList()) {
            playerPanelLink.put(player, new HashMap<>());
            JPanel playerPanel = generatePlayerPanel(player);
            playerPanel.setPreferredSize(new Dimension(PANEL_MAX_WIDTH, INFOPANEL_UNIT_HEIGHT));

            //System.out.println("[GuiBoard]: playerPanel width: panel max=" + PANEL_MAX_WIDTH + "  screen width="+this.getSize().width);
            Color selectedColour;
            switch (game.getPlayerList().indexOf(player)) {
                case 0:
                    selectedColour = new Color(255,50,0);
                    break;
                case 1:
                    selectedColour = new Color(0,0,255);
                    break;
                case 2:
                    selectedColour = new Color(204,0,102);
                    break;
                case 3:
                    selectedColour = new Color(255,255,0);
                    break;
                default:
                    selectedColour = Color.GRAY;
            }

            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = game.getPlayerList().indexOf(player) % 2 == 1 ? 1 : 0;
            c.gridwidth = 1;
            c.gridy = game.getPlayerList().indexOf(player) > 1 ? 2 : 1;
            playerPanel.setBackground(new Color(0,0,0, OPACITY_LEVEL_LOW));
            if (game.getPlayerList().get(game.getActivePlayer()) == player){
                playerPanel.setBorder(BorderFactory.createLineBorder(selectedColour, 5));
            }else {
                playerPanel.setBorder(BorderFactory.createLineBorder(selectedColour, 1));
            }

            infoPanel.add(playerPanel,c);
        }
    }

    private JPanel generateInfoPanel(Game game, String textMessage) {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout( new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        String labelText = game.getPlayerList().get(game.getActivePlayer()).getName() + textMessage;

        infoPanel.setPreferredSize(new Dimension(PANEL_MAX_WIDTH, INFOPANEL_UNIT_HEIGHT*(game.getPlayerList().size()+2)));
        infoPanel.setBackground(new Color(0,0,0, OPACITY_LEVEL_HIGH));

        messagePanel = new JLabel(labelText);
        messagePanel.setFont(new java.awt.Font("Calibri", 1, MESSAGE_PANEL_FONT_SIZE));
        messagePanel.setForeground(Color.LIGHT_GRAY);
        messagePanel.setPreferredSize(new Dimension(PANEL_MAX_WIDTH, INFOPANEL_UNIT_HEIGHT));
        messagePanel.setHorizontalAlignment(SwingConstants.CENTER);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        infoPanel.add(messagePanel, c);

        generateAllPlayersPanel(game, infoPanel, 1, c);

        return infoPanel;
    }


    private JPanel createBoardPanel(Board board) {
        JPanel result = new JPanel();
        result.setLayout(new CustomGridLayout(board.getHeight(), board.getWidth()));
        result.setBackground(new Color(0,0,0,0));
        result.setOpaque(false);
        boardTileButtonLink = new ArrayList<>();

        for (int y = 0; y < board.getHeight(); ++y) {
            boardTileButtonLink.add(new ArrayList<AbstractBoardTileButton>());
            for (int x = 0; x < board.getWidth(); ++x) {
                AbstractBoardTileButton boardTileButton = null;
                if (GameType.MULTI == this.gameType) {
                    boardTileButton = new BoardTileButtonMulti(new Point(x, y), this);
                }else{
                    boardTileButton = new BoardTileButtonSingle(new Point(x, y), this, this.gameHandlerForSinglePlayer);
                }
                boardTileButton.setPreferredSize(new Dimension(TILES_MAX_WIDTH, TILES_MAX_HEIGHT));
                boardTileButtonLink.get(y).add(boardTileButton);
                result.add(boardTileButton);
            }
        }

        result.setBackground(new Color(0,0,0, OPACITY_LEVEL_HIGH));
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
            remainingJungleTileNumberLabel.setForeground(Color.LIGHT_GRAY);
        }

        TitledBorder jungleLabelTitle = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Deck");
        jungleLabelTitle.setTitleColor(Color.LIGHT_GRAY);
        jungleLabelTitle.setTitleJustification(TitledBorder.CENTER);
        remainingJungleTileNumberLabel.setBorder(jungleLabelTitle);

        jungleTilesPanel.add(remainingJungleTileNumberLabel);

        TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Jungle Tiles");
        title.setTitleColor(Color.LIGHT_GRAY);
        title.setTitleJustification(TitledBorder.LEFT);
        jungleTilesPanel.setBorder(title);
        jungleTilesPanel.setBackground(new Color(0,0,0, OPACITY_LEVEL_HIGH));

        //add workerTiles
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
            remainingWorkerTileNumberLabel.setForeground(Color.LIGHT_GRAY);
        }
        TitledBorder workerLabelTitle = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Deck");
        workerLabelTitle.setTitleColor(Color.LIGHT_GRAY);
        workerLabelTitle.setTitleJustification(TitledBorder.CENTER);
        remainingWorkerTileNumberLabel.setBorder(workerLabelTitle);
        workerTilesPanel.add(remainingWorkerTileNumberLabel);

        TitledBorder workerTitle = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Worker Tiles");
        workerTitle.setTitleColor(Color.LIGHT_GRAY);
        workerTitle.setTitleJustification(TitledBorder.LEFT);
        workerTilesPanel.setBorder(workerTitle);
        workerTilesPanel.setBackground(new Color(0,0,0, OPACITY_LEVEL_HIGH));

        tilesPanel.add(jungleTilesPanel);
        tilesPanel.add(workerTilesPanel);
        tilesPanel.setBackground(new Color(0,0,0, OPACITY_LEVEL_HIGH));

        return tilesPanel;
    }


    public ClientConnection getGameConnection() {
        return gameConnection;
    }

    public void setGameConnection(ClientConnection gameConnection) {
        this.gameConnection = gameConnection;
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

    public List<List<AbstractBoardTileButton>> getBoardTileButtonLink() {
        return boardTileButtonLink;
    }

    public Set<AbstractBoardTileButton> getSelectableJunglePanelLink() {
        return selectableJunglePanelLink;
    }

    public void setSelectableJunglePanelLink(Set<AbstractBoardTileButton> selectableJunglePanelLink) {
        this.selectableJunglePanelLink = selectableJunglePanelLink;
    }

    public Set<AbstractBoardTileButton> getSelectableWorkerPanelLink() {
        return selectableWorkerPanelLink;
    }

    public void setSelectableWorkerPanelLink(Set<AbstractBoardTileButton> selectableWorkerPanelLink) {
        this.selectableWorkerPanelLink = selectableWorkerPanelLink;
    }

    public GameHandler getGameHandlerForSinglePlayer() {
        return gameHandlerForSinglePlayer;
    }

    private void loadBackgroundImage() {
        URL url = this.getClass().getResource(FILE_NAME);

        try {
            backgroundImage = ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    class CustomGridLayout extends GridLayout {
        public CustomGridLayout(int rows, int cols) {
            super(rows, cols);
        }
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                Insets insets = parent.getInsets();
                int ncomponents = parent.getComponentCount();
                int nrows = getRows();
                int ncols = getColumns();
                boolean ltr = parent.getComponentOrientation().isLeftToRight();

                if (ncomponents == 0) {
                    return;
                }
                if (nrows > 0) {
                    ncols = (ncomponents + nrows - 1) / nrows;
                } else {
                    nrows = (ncomponents + ncols - 1) / ncols;
                }



                int totalGapsWidth = (ncols - 1) * getHgap();
                int widthWOInsets = parent.getWidth() - (insets.left + insets.right);
                int widthOnComponent = (widthWOInsets - totalGapsWidth) / ncols;
                int extraWidthAvailable = (widthWOInsets - (widthOnComponent * ncols + totalGapsWidth)) / 2;

                int totalGapsHeight = (nrows - 1) * getVgap();
                int heightWOInsets = parent.getHeight() - (insets.top + insets.bottom);
                int heightOnComponent = (heightWOInsets - totalGapsHeight) / nrows;
                int extraHeightAvailable = (heightWOInsets - (heightOnComponent * nrows + totalGapsHeight)) / 2;

                int size=Math.min(widthOnComponent, heightOnComponent);
                widthOnComponent=size;
                heightOnComponent=size;
                if (ltr) {
                    for (int c = 0, x = insets.left + extraWidthAvailable; c < ncols ; c++, x += widthOnComponent + getHgap()) {
                        for (int r = 0, y = insets.top + extraHeightAvailable; r < nrows ; r++, y += heightOnComponent + getVgap()) {
                            int i = r * ncols + c;
                            if (i < ncomponents) {
                                parent.getComponent(i).setBounds(x, y, widthOnComponent, heightOnComponent);
                            }
                        }
                    }
                } else {
                    for (int c = 0, x = (parent.getWidth() - insets.right - widthOnComponent) - extraWidthAvailable; c < ncols ; c++, x -= widthOnComponent + getHgap()) {
                        for (int r = 0, y = insets.top + extraHeightAvailable; r < nrows ; r++, y += heightOnComponent + getVgap()) {
                            int i = r * ncols + c;
                            if (i < ncomponents) {
                                parent.getComponent(i).setBounds(x, y, widthOnComponent, heightOnComponent);
                            }
                        }
                    }
                }
            }
        }
    }
}

