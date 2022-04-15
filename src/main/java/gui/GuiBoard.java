package gui;

import board.Board;
import connection.ClientConnection;
import game.Game;
import players.Player;
import tiles.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;


public class GuiBoard extends JFrame {
    private ClientConnection connection;

    private static final int TILES_MAX_HEIGHT = 60;
    private static final int TILES_MAX_WIDTH = 100;

    private static final int PANEL_MAX_WIDTH = 1_500;
    private static final int PANEL_MAX_HEIGHT = 1_000;

    private static final int INFOPANEL_HEIGHT = 30;

    private static final int BOARD_HORIZONTAL_GAP = 20;
    private static final int BOARD_VERTICAL_GAP = 20;

    private Game game;
    private final int playerIndex;
    private WorkerTile selectedWorkerTile;
    private JungleTile selectedJungleTile;

    private boolean hasPlacedWorkerTile;
    private boolean hasPlacedJungleTile;

    private JLabel messagePanel;

    private JPanel infoPanel;
    private JPanel boardPanel;
    private JPanel cardsPanel;

    private Map<Player, Map<String, JLabel>>  playerPanelLink;
    private List<List<BoardTileButton>> boardTileButtonLink;




    public GuiBoard(ClientConnection connection, Game game, int playerIndex) {
        this.connection = connection;
        this.game = game;
        this.playerIndex = playerIndex;
        selectedJungleTile = null;
        selectedWorkerTile = null;

        hasPlacedWorkerTile = false;
        hasPlacedJungleTile = false;

        playerPanelLink = new HashMap<>();
        boardTileButtonLink = new ArrayList<>();

        this.setTitle("Cacao Board Game");
        this.setPreferredSize(new Dimension(PANEL_MAX_WIDTH, PANEL_MAX_HEIGHT));
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        String textMessage = "'s turn (Other players are inactive)";
        infoPanel = generateInfoPanel(game, textMessage);

        this.getContentPane().setLayout(new BorderLayout(BOARD_HORIZONTAL_GAP, BOARD_VERTICAL_GAP));
        this.getContentPane().add(infoPanel, BorderLayout.NORTH);

        boardPanel = createBoardPanel(game.getBoard());
        this.getContentPane().add(boardPanel, BorderLayout.CENTER);

        cardsPanel = generateTilesPanel(game, playerIndex);
        this.getContentPane().add(cardsPanel, BorderLayout.SOUTH);

        this.pack();    //ez rakja egybe
        this.setVisible(true);
        this.setFocusable(true);
        this.requestFocusInWindow();
    }

    public void updateGuiBoard(Game gameReceived, String textMessage) {
        this.game = gameReceived;

        //messagePanel update
        messagePanel.setText("Player " + ((int) game.getActivePlayer() + 1) + ": " + textMessage);

        //playerPanel
        for (Map.Entry<Player, Map<String, JLabel>> playerEntry : playerPanelLink.entrySet()) {
            for(Map.Entry<String, JLabel> labelEntry : playerEntry.getValue().entrySet()){
                Player actualPlayer = game.getPlayerList().get(playerEntry.getKey().getPlayerColour().getPlayerOrdinal()-1);
                if ("coinValue".equals(labelEntry.getKey())){
                    String coinValue = Integer.toString(actualPlayer.getCoins());
                    labelEntry.getValue().setText(coinValue);
                }else if ("beanValue".equals(labelEntry.getKey())){
                    String coinValue = Integer.toString(actualPlayer.getNumberOfCacaoBean());
                    labelEntry.getValue().setText(coinValue);
                }else if ("shrineValue".equals(labelEntry.getKey())){
                    String coinValue = Integer.toString(actualPlayer.getWorshipSymbol());
                    labelEntry.getValue().setText(coinValue);
                }else if ("waterValue".equals(labelEntry.getKey())){
                    String coinValue = Integer.toString(actualPlayer.getWaterPointIndex());
                    labelEntry.getValue().setText(coinValue);
                }
            }
        }


        //infoPanel = generateInfoPanel(game, textMessage);
        //this.getContentPane().setLayout(new BorderLayout(BOARD_HORIZONTAL_GAP, BOARD_VERTICAL_GAP));
        //this.getContentPane().add(infoPanel, BorderLayout.NORTH);

        //boardTile update
        for(int y=0; y<game.getBoard().getHeight(); ++y){
            for(int x=0; x<game.getBoard().getWidth(); ++x){
                String newText = game.getBoard().getField(x,y).toShortString() + " " + x + " " + y;
                boardTileButtonLink.get(y).get(x).setText(newText);
            }
        }



        //boardPanel = createBoardPanel(game.getBoard());
        //this.getContentPane().add(boardPanel, BorderLayout.CENTER);

        cardsPanel = generateTilesPanel(game, playerIndex);
        this.getContentPane().add(cardsPanel, BorderLayout.SOUTH);

        //SwingUtilities.updateComponentTreeUI(this);

        //this.validate();

        //this.revalidate();
        //this.repaint();

        /*
        this.pack();    //ez rakja egybe
        this.setVisible(true);
        this.setFocusable(true);
        this.requestFocusInWindow();

         */

    }

    private JPanel generatePlayerPanel(Player player) {
        JPanel panel = new JPanel();

        JLabel playerName = new JLabel("Player " + player.getPlayerColour().getPlayerOrdinal());
        panel.add(playerName);
        playerPanelLink.get(player).put("name", playerName);

        JLabel coinIcon = new JLabel("coin: "); //TODO: add coin icon
        JLabel coinValue = new JLabel(String.valueOf(player.getCoins()));
        panel.add(coinIcon);
        panel.add(coinValue);
        playerPanelLink.get(player).put("coinIcon", coinIcon);
        playerPanelLink.get(player).put("coinValue", coinValue);

        JLabel beanIcon = new JLabel("bean: "); //TODO: add coin icon
        JLabel beanValue = new JLabel(String.valueOf(player.getNumberOfCacaoBean()));      //TODO: add boxes instead of number + colourify boxes based on number
        panel.add(beanIcon);
        panel.add(beanValue);
        playerPanelLink.get(player).put("beanIcon", beanIcon);
        playerPanelLink.get(player).put("beanValue", beanValue);

        JLabel shrineIcon = new JLabel("shrine: "); //TODO: add coin icon
        JLabel shrineValue = new JLabel(String.valueOf(player.getWorshipSymbol()));       //TODO: add boxes instead of number + colourify boxes based on number
        panel.add(shrineIcon);
        panel.add(shrineValue);
        playerPanelLink.get(player).put("shrineIcon", shrineIcon);
        playerPanelLink.get(player).put("shrineValue", shrineValue);

        JLabel waterIcon = new JLabel("water: "); //TODO: add coin icon
        JLabel waterValue = new JLabel(String.valueOf(player.getWaterPointIndex()));      //TODO: add boxes instead of number + colourify boxes based on number
        panel.add(waterIcon);
        panel.add(waterValue);
        playerPanelLink.get(player).put("waterIcon", waterIcon);
        playerPanelLink.get(player).put("waterValue", waterValue);

        //TODO: add pointIcon + pointValue ; add rankIcon + rankValue

        panel.setLayout(new FlowLayout());

        return panel;
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
        String labelText = "Player " + ((int) game.getActivePlayer() + 1) + ": " + textMessage;

        messagePanel = new JLabel(labelText);
        messagePanel.setPreferredSize(new Dimension(PANEL_MAX_WIDTH, INFOPANEL_HEIGHT));
        messagePanel.setHorizontalAlignment(SwingConstants.CENTER);
        infoPanel.add(messagePanel, BorderLayout.NORTH);

        JPanel allPlayersPanel = generateAllPlayersPanel(game);

        infoPanel.add(allPlayersPanel, BorderLayout.CENTER);
        return infoPanel;
    }


    private JPanel createBoardPanel(Board board) {
        //if (Objects.nonNull(boardPanel) && boardPanel.getComponents().length >0) boardPanel.removeAll();
        JPanel result = new JPanel();
        result.setBackground(Color.BLUE);
        result.setLayout(new GridLayout(board.getHeight(), board.getWidth()));

        for (int y = 0; y < board.getHeight(); ++y) {
            boardTileButtonLink.add(new ArrayList<BoardTileButton>());
            for (int x = 0; x < board.getWidth(); ++x) {
                BoardTileButton boardTileButton = new BoardTileButton(new Point(x,y),this);
                boardTileButton.setPreferredSize(new Dimension(TILES_MAX_WIDTH, TILES_MAX_HEIGHT));
                boardTileButton.setMaximumSize(new Dimension(TILES_MAX_WIDTH, TILES_MAX_HEIGHT));
                boardTileButtonLink.get(y).add(boardTileButton);
                result.add(boardTileButton);
            }
        }

        return result;
    }




    private JPanel generateTilesPanel(Game game, int playerIndex) {
        JPanel tilesPanel = new JPanel();
        tilesPanel.setLayout(new FlowLayout());

        //add jungleTiles
        JPanel jungleTilesPanel = new JPanel();
        for (JungleTile jungleTile : game.getJungleTilesAvailable()) {
            ActionButtonJungleTile tileButton = new ActionButtonJungleTile(this, jungleTile);
            tileButton.setText(jungleTile.toShortString());
            tileButton.setPreferredSize(new Dimension(TILES_MAX_WIDTH, TILES_MAX_HEIGHT));
            jungleTilesPanel.add(tileButton);
        }
        jungleTilesPanel.add(new JButton(String.valueOf(game.getJungleTileDeck().getDeck().size())));
        jungleTilesPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        //add workerTiles
        JPanel workerTilesPanel = new JPanel();
        Player currentPlayer = game.getPlayerList().get(playerIndex);
        for (WorkerTile workerTile : currentPlayer.getCardsAtHand()) {
            ActionButtonWorkerTile tileButton = new ActionButtonWorkerTile(this, workerTile);
            tileButton.setPreferredSize(new Dimension(TILES_MAX_WIDTH, TILES_MAX_HEIGHT));
            workerTilesPanel.add(tileButton);
        }
        workerTilesPanel.add(new JButton(String.valueOf(currentPlayer.getWorkerTileDeck().getDeck().size())));
        workerTilesPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

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
}
