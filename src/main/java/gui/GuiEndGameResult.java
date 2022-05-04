package gui;

import deck.JungleTileDeck;
import game.Game;
import players.Player;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GuiEndGameResult extends JFrame {
    private static final String FILE_NAME = "/background/cacaoFrontPage.jpg";

    private static final int PLAYER_PANEL_HEIGHT = 30;
    private static final int PLAYER_PANEL_WIDTH = 40;

    private static final int RANK_AND_POINT_HEIGHT = 30;
    private static final int RANK_AND_POINT_WIDTH = 40;

    private static final int PLAYER_NAME_HEIGHT = 30;
    private static final int PLAYER_NAME_WIDTH = 180;

    private static final int HEADER_HEIGHT = 30;
    private static final int HEADER_WIDTH = 300;

    private static final int OPACITY_LEVEL = 85;

    private static final int HEADER_FONT_SIZE = 30;
    private static final int PLAYER_PANEL_FONT_SIZE = 20;

    JPanel headerPanel;
    JPanel playerRankPanel;
    private Game game;
    private JPanel contentPane;

    private Image backgroundImage = null;


    public GuiEndGameResult (Game game){
        this.game = game;
        this.setTitle("Cacao Board Game - Game Results");
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        loadBackgroundImage();

        headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0,0,0,0));
        JLabel headerLabel = new JLabel("Game Results");
        headerLabel.setPreferredSize(new Dimension(HEADER_WIDTH, HEADER_HEIGHT));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerLabel.setVerticalAlignment(SwingConstants.CENTER);
        headerLabel.setFont(new Font("Serif", Font.BOLD, HEADER_FONT_SIZE));
        headerLabel.setForeground(Color.BLACK);
        headerPanel.setBackground(new Color(223, 218, 0));
        headerPanel.add(headerLabel);

        playerRankPanel = new JPanel();
        playerRankPanel.setBackground(new Color(0,0,0,OPACITY_LEVEL));

        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
            }
        };

        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        contentPane.add(headerPanel, c);


        List<Player> sortedPlayerListByRank = new ArrayList<>(game.getPlayerList());
        sortedPlayerListByRank.sort(Comparator.comparing(Player::getRank));

        for(int i=0; i<sortedPlayerListByRank.size(); ++i){
            c.gridx = 0;
            c.gridy = i+1;
            contentPane.add(createPlayerPanel(sortedPlayerListByRank.get(i)), c);
        }

       /* sortedPlayerListByRank.forEach(player -> {
            playerRankPanel.add(createPlayerPanel(player));
        });*/
        setContentPane(contentPane);

        this.setPreferredSize(new Dimension(backgroundImage.getWidth(this), backgroundImage.getHeight(this)));

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setFocusable(true);
        this.requestFocusInWindow();
    }

    private JPanel createPlayerPanel(Player player){
        JPanel playerPanel = new JPanel();
        playerPanel.setLayout(new FlowLayout());
        JLabel rankLabel = createLabel(String.valueOf(player.getRank()), SwingConstants.CENTER, RANK_AND_POINT_WIDTH);
        JLabel playerNameLabel = createLabel(String.valueOf(player.getName()), SwingConstants.CENTER, PLAYER_NAME_WIDTH);
        JLabel pointLabel = createLabel(String.valueOf(player.getPoint()), SwingConstants.CENTER, RANK_AND_POINT_WIDTH);

        playerPanel.add(rankLabel);
        playerPanel.add(playerNameLabel);
        playerPanel.add(pointLabel);
        playerPanel.setBackground(new Color(Color.DARK_GRAY.getRed(),Color.DARK_GRAY.getGreen(),Color.DARK_GRAY.getBlue(),Color.DARK_GRAY.getAlpha()-50));
        //playerPanel.setBackground(Color.DARK_GRAY);

        return playerPanel;
    }

    private JLabel createLabel(String text, int orientation, int width){
        JLabel result = new JLabel(text);
        result.setBackground(new Color(0,0,0,0));
        result.setOpaque(false);
        result.setPreferredSize(new Dimension(width, RANK_AND_POINT_HEIGHT));
        result.setHorizontalAlignment(orientation);
        result.setVerticalAlignment(SwingConstants.CENTER);
        result.setFont(new Font("Serif", Font.BOLD, PLAYER_PANEL_FONT_SIZE));
        result.setForeground(Color.WHITE);
        return result;
    }


    private void loadBackgroundImage() {
        URL url = this.getClass().getResource(FILE_NAME);

        try {
            backgroundImage = ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Player player1 = new Player.PlayerBuilder().setName("AAA").setRank(1).setPoint(20).build();
        Player player2 = new Player.PlayerBuilder().setName("BBB").setRank(2).setPoint(15).build();
        Player player3 = new Player.PlayerBuilder().setName("CCC").setRank(3).setPoint(10).build();
        Player player4 = new Player.PlayerBuilder().setName("DDD").setRank(4).setPoint(5).build();

        List<Player> playerList = new ArrayList<>();
        playerList.add(player1);
        playerList.add(player2);
        playerList.add(player3);
        playerList.add(player4);


        Game game = new Game(playerList, null, new JungleTileDeck(4));

        GuiEndGameResult gui = new GuiEndGameResult(game);
    }
}
