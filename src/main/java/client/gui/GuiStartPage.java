package client.gui;

import client.connection.ClientConnection;
import common.game.Game;
import common.game.GameHandler;
import common.players.Player;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;


public class GuiStartPage extends JFrame {
    private static final String FILE_NAME = "/background/cacaoFrontPage.jpg";
    private static final String TEXTBOX_PREFIX = "<html><p>";
    private static final String TEXTBOX_SUFFIX = "</p></html>";
    private static final int BUTTON_HEIGHT = 25;
    private static final int BUTTON_WIDTH = 200;
    private static final int TEXTBOX_HEIGHT = 25;
    private static final int TEXTBOX_WIDTH = 400;
    private static final int OPACITY_LEVEL = 50;
    private static final int BUTTON_FONT_SIZE = 16;

    private ClientConnection gameConnection;
    private ClientConnection chatConnection;
    private boolean isGameConnected = false;
    private boolean isChatConnected = false;


    private JPanel messagePanel;
    private JPanel dummyPanel;
    private JPanel newGamePanel;
    private JPanel newGameWithBasicBotsPanel;
    private JPanel newGameWithSmarterBotsPanel;
    private JPanel exitPanel;
    private JPanel contentPane;

    private JLabel messageLabel;

    private JPanel namePanel;
    private JPanel hostNamePanel;
    private JTextField nameInput;
    private JTextField hostNameInput;
    String defaultNameInputText = "Add Your Name";
    String defaultHostNameInputText = "Add Host Name";

    private int verticalAdjuster = 8;
    private Image backgroundImage = null;

    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem menuItem;


    public GuiStartPage() {
        this.setTitle("Cacao Board Game");
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        loadBackgroundImage();

        menuBar = new JMenuBar();
        menu = new JMenu("Menu");
        menuItem = new JMenuItem(new AbstractAction("Rules - English") {
            public void actionPerformed(ActionEvent e) {
                try {
                    String fileName = "/rule/rulesEng.pdf";
                    URL url = getClass().getResource(fileName);

                    File myFile = new File(url.toURI());

                    Desktop.getDesktop().open(myFile);
                } catch (URISyntaxException | IOException ex) {
                    ex.printStackTrace();
                }

            }
        });

        menu.add(menuItem);
        menuBar.add(menu);


        dummyPanel = new JPanel();
        dummyPanel.setBackground(new Color(0, 0, 0, 0));
        dummyPanel.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT * verticalAdjuster));  //set vertical alignment


        messagePanel = new JPanel();
        messagePanel.setBackground(new Color(0, 0, 0, 0));
        messagePanel.setOpaque(false);
        messageLabel = new JLabel();
        messageLabel.setPreferredSize(new Dimension(TEXTBOX_WIDTH, TEXTBOX_HEIGHT));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setVerticalAlignment(SwingConstants.CENTER);
        messageLabel.setFont(new Font("Serif", Font.BOLD, 20));
        messageLabel.setForeground(new Color(255, 255, 255));
        messageLabel.setBackground(new Color(0, 0, 0, 0));
        messageLabel.setOpaque(false);
        messagePanel.add(messageLabel);

        newGamePanel = new JPanel();
        newGamePanel.setBackground(new Color(0, 0, 0, OPACITY_LEVEL));
        JButton connectBtn = new JButton("Connect to Game");
        connectBtn.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        connectBtn.setFont(new Font("Serif", Font.BOLD, BUTTON_FONT_SIZE));
        connectBtn.setBackground(Color.GREEN);
        connectBtn.addActionListener((ActionEvent ae) -> {
            updateMessagePanelState();
            requestConnection();
        });
        newGamePanel.add(connectBtn);

        newGameWithBasicBotsPanel = new JPanel();
        newGameWithBasicBotsPanel.setBackground(new Color(0, 0, 0, OPACITY_LEVEL));
        JButton playWithBasicBots = new JButton("Play with basic bots");
        playWithBasicBots.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        playWithBasicBots.setFont(new Font("Serif", Font.BOLD, BUTTON_FONT_SIZE));
        playWithBasicBots.setBackground(new Color(255, 244, 0));
        playWithBasicBots.addActionListener((ActionEvent ae) -> {
            try {
                startSinglePlayerMode(Player.PlayerType.BASIC_AI);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        newGameWithBasicBotsPanel.add(playWithBasicBots);

        newGameWithSmarterBotsPanel = new JPanel();
        newGameWithSmarterBotsPanel.setBackground(new Color(0, 0, 0, OPACITY_LEVEL));
        JButton playWithSmarterBots = new JButton("Play with smart bots");
        playWithSmarterBots.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        playWithSmarterBots.setFont(new Font("Serif", Font.BOLD, BUTTON_FONT_SIZE));
        playWithSmarterBots.setBackground(new Color(255, 204, 0));
        playWithSmarterBots.addActionListener((ActionEvent ae) -> {
            try {
                startSinglePlayerMode(Player.PlayerType.SMARTER_AI);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        newGameWithSmarterBotsPanel.add(playWithSmarterBots);



        exitPanel = new JPanel();
        exitPanel.setBackground(new Color(0, 0, 0, OPACITY_LEVEL));
        JButton exitBtn = new JButton("Exit");
        exitBtn.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        exitBtn.setFont(new Font("Serif", Font.BOLD, BUTTON_FONT_SIZE));
        exitBtn.setBackground(Color.RED);
        exitBtn.addActionListener((ActionEvent ae) -> System.exit(0));
        exitPanel.add(exitBtn);

        hostNamePanel = new JPanel();
        hostNamePanel.setLayout(new FlowLayout());
        hostNamePanel.setBackground(new Color(0, 0, 0, OPACITY_LEVEL));
        JLabel hostNameText = new JLabel(defaultHostNameInputText);
        hostNameText.setForeground(Color.WHITE);
        hostNameText.setFont(new Font("Serif", Font.BOLD, BUTTON_FONT_SIZE - 2));
        hostNameInput = new JTextField("127.0.0.1", 9);
        hostNameInput.setFont(new Font("Serif", Font.BOLD, BUTTON_FONT_SIZE - 4));
        hostNamePanel.add(hostNameText);
        hostNamePanel.add(hostNameInput);

        namePanel = new JPanel();
        namePanel.setLayout(new FlowLayout());
        namePanel.setBackground(new Color(0, 0, 0, OPACITY_LEVEL));
        JLabel namePanelText = new JLabel(defaultNameInputText);
        namePanelText.setForeground(Color.WHITE);
        namePanelText.setFont(new Font("Serif", Font.BOLD, BUTTON_FONT_SIZE - 2));
        nameInput = new JTextField("Player", 9);
        nameInput.setFont(new Font("Serif", Font.BOLD, BUTTON_FONT_SIZE - 4));
        namePanel.add(namePanelText);
        namePanel.add(nameInput);

        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
            }
        };

        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        this.setJMenuBar(menuBar);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        contentPane.add(dummyPanel, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        contentPane.add(messagePanel, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        contentPane.add(hostNamePanel, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 3;
        contentPane.add(namePanel, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 4;
        contentPane.add(newGamePanel, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 5;
        contentPane.add(newGameWithBasicBotsPanel, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 6;
        contentPane.add(newGameWithSmarterBotsPanel, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 7;
        contentPane.add(exitPanel, c);

        setContentPane(contentPane);

        this.setPreferredSize(new Dimension(backgroundImage.getWidth(this), backgroundImage.getHeight(this)));

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setFocusable(true);
        this.requestFocusInWindow();
    }

    private void startSinglePlayerMode(Player.PlayerType playerType) throws IOException, ClassNotFoundException {

        String playerName = defineSinglePlayerName();

        int numberOfBot = 3;

        dispose();
        GameHandler gameHandler = new GameHandler(playerName, numberOfBot, playerType);

        GuiBoard guiBoard = new GuiBoard(gameHandler);
    }

    private String defineSinglePlayerName() {
        String playerName;
        if (!("".equals(nameInput.getText()) || defaultNameInputText.equals(nameInput.getText()))) {
            playerName = nameInput.getText();
        } else {
            playerName = "You";
        }
        return playerName;
    }

    private void updateMessagePanelState() {
        messageLabel.setText(TEXTBOX_PREFIX + "Connected, waiting for others... " + TEXTBOX_SUFFIX);
        Color usedColor = Color.LIGHT_GRAY;
        messageLabel.setBackground(new Color(usedColor.getRed(), usedColor.getGreen(), usedColor.getBlue(), usedColor.getAlpha() - 25));
        messageLabel.setOpaque(true);
        messageLabel.paintImmediately(messageLabel.getVisibleRect());
    }

    private void loadBackgroundImage() {
        URL url = this.getClass().getResource(FILE_NAME);

        try {
            backgroundImage = ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void requestConnection() {
        System.out.println("[GuiStartPage]: attempting to connect, isGameConnected state=" + isGameConnected + ", isChatConnected state=" + isChatConnected);

        Game newGame = null;
        Integer playerIndex = null;

        String hostName = null;
        if (!("".equals(hostNameInput.getText()))) {
            hostName = hostNameInput.getText();
        } else {
            try {
                hostName = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                JOptionPane.showMessageDialog(new JFrame(), "An error occured during client.connection.", "Connection Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                dispose();
                System.exit(-1);
            }
        }

        if (!isGameConnected) {
            try {
                System.out.println("[GuiStartPage]: attempting to create common.game clientConnection");
                gameConnection = new ClientConnection(hostName, ClientConnection.ConnectionType.OBJECT);
                isGameConnected = true;
                System.out.println("[GuiStartPage]: isGameConnected=" + isGameConnected);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (!isChatConnected) {
            try {

                System.out.println("[GuiStartPage]: attempting to create chat clientConnection");
                chatConnection = new ClientConnection(hostName, ClientConnection.ConnectionType.TEXT);
                isChatConnected = true;

                System.out.println("[GuiStartPage]: chat clientConnection creation successful");


                String playerName = null;
                if (!("".equals(nameInput.getText()) || defaultNameInputText.equals(nameInput.getText()))) {
                    playerName = nameInput.getText();
                }

                System.out.println("[GuiStartPage]: attempts to write in oos");
                gameConnection.getObjectOutputStream().writeUnshared(playerName);

                System.out.println("[GuiStartPage]: attempts to read in common.game from ois");
                newGame = (Game) gameConnection.getObjectInputStream().readUnshared();
                System.out.println("[GuiStartPage]: successful read, common.game=" + newGame);

                System.out.println("[GuiStartPage]: attempts to read in playerIndex from ois");
                playerIndex = (Integer) gameConnection.getObjectInputStream().readUnshared();
                System.out.println("[GuiStartPage]: successful read, playerIndex=" + playerIndex);
                System.out.println("[GuiStartPage]: Game object and player index received");

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (newGame.getPlayerList().size() == 1) {
            JOptionPane.showMessageDialog(this, "You are the only one who joined. If you would like to play please restart the common.game and invite your friends", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.out.println("[GuiStartPage]: Launching GuiBoard");
        launchGuiBoard(newGame, playerIndex);


    }

    private void launchGuiBoard(Game newGame, Integer playerIndex) {
        dispose();
        GuiBoard guiBoard = new GuiBoard(gameConnection, chatConnection, newGame, playerIndex);
        guiBoard.setVisible(true);
        guiBoard.setFocusable(true);
        guiBoard.requestFocusInWindow();

        Thread thread = new Thread(guiBoard);
        thread.start();
    }

    public static void main(String[] args) {
        GuiStartPage start = new GuiStartPage();
    }
}
