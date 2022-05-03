package gui;

import connection.ClientConnection;
import game.Game;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;


public class GuiStartPage extends JFrame {
    private static final String FILE_NAME = "/background/cacaoFrontPage.jpg";
    private static final String TEXTBOX_PREFIX= "<html><p>";
    private static final String TEXTBOX_SUFFIX = "</p></html>";
    private static final int BUTTON_HEIGHT = 30;
    private static final int BUTTON_WIDTH = 200;
    private static final int TEXTBOX_HEIGHT = 30;
    private static final int TEXTBOX_WIDTH = 300;
    private static final int OPACITY_LEVEL = 50;
    private static final int BUTTON_FONT_SIZE = 20;

    private ClientConnection connection;
    private boolean isConnected = false;

    private JPanel messagePanel;
    private JPanel dummyPanel;
    private JPanel newGamePanel;
    private JPanel exitPanel;
    private JPanel contentPane;

    private JLabel messageLabel;

    private JPanel namePanel;
    private JTextField nameInput;
    String defaultNameInputText = "Add Your Name";


    private Image backgroundImage = null;


    public GuiStartPage() {
        this.setTitle("Cacao Board Game");
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        loadBackgroundImage();

        dummyPanel = new JPanel();
        dummyPanel.setBackground(new Color(0,0,0,0));
        dummyPanel.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT*10));  //set vertical alignment


        messagePanel = new JPanel();
        messagePanel.setBackground(new Color(0,0,0,0));
        messagePanel.setOpaque(false);
        messageLabel = new JLabel();
        messageLabel.setPreferredSize(new Dimension(TEXTBOX_WIDTH, TEXTBOX_HEIGHT));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setVerticalAlignment(SwingConstants.CENTER);
        messageLabel.setFont(new Font("Serif", Font.BOLD, 20));
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setBackground(new Color(0,0,0,0));
        messageLabel.setOpaque(false);
        messagePanel.add(messageLabel);

        newGamePanel = new JPanel();
        newGamePanel.setBackground(new Color(0,0,0,OPACITY_LEVEL));
        JButton connectBtn = new JButton("Connect to Game");
        connectBtn.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        connectBtn.setFont(new Font("Serif", Font.BOLD, BUTTON_FONT_SIZE));
        connectBtn.setBackground(Color.GREEN);
        connectBtn.addActionListener((ActionEvent ae) -> {
            updateMessagePanelState();
            requestConnection();
        });
        newGamePanel.add(connectBtn);

        exitPanel = new JPanel();
        exitPanel.setBackground(new Color(0,0,0,OPACITY_LEVEL));
        JButton exitBtn = new JButton("Exit");
        exitBtn.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        exitBtn.setFont(new Font("Serif", Font.BOLD, BUTTON_FONT_SIZE));
        exitBtn.setBackground(Color.RED);
        exitBtn.addActionListener((ActionEvent ae) -> System.exit(0));
        exitPanel.add(exitBtn);

        namePanel = new JPanel();
        namePanel.setBackground(new Color(0,0,0,OPACITY_LEVEL));
        nameInput = new JTextField(defaultNameInputText, 16);
        nameInput.setFont(new Font("Serif", Font.BOLD, BUTTON_FONT_SIZE-4));
        namePanel.add(nameInput);

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
        contentPane.add(dummyPanel, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        contentPane.add(messagePanel, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        contentPane.add(namePanel, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 3;
        contentPane.add(newGamePanel, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 4;
        contentPane.add(exitPanel, c);

        setContentPane(contentPane);

        this.setPreferredSize(new Dimension(backgroundImage.getWidth(this), backgroundImage.getHeight(this)));

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setFocusable(true);
        this.requestFocusInWindow();
    }

    private void updateMessagePanelState() {
        messageLabel.setText(TEXTBOX_PREFIX + "Connected, waiting for others... " + TEXTBOX_SUFFIX);
        messageLabel.setBackground(new Color(0,0,0,0));
        messageLabel.paintImmediately(messageLabel.getVisibleRect());
        //System.out.println("[GuiStartPage]: messageLabel updated, state="  + messageLabel.getText());
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
        System.out.println("[GuiStartPage]: attempting to connect, isConnected state=" + isConnected);

        Game newGame = null;
        Integer playerIndex = null;
        if (!isConnected) {
            try {
                connection = new ClientConnection();
                isConnected = true;

                String playerName = null;

                if (!("".equals(nameInput.getText()) || defaultNameInputText.equals(nameInput.getText()))) {
                    playerName = nameInput.getText();
                }

                connection.getObjectOutputStream().writeUnshared(playerName);

                newGame = (Game) connection.getObjectInputStream().readUnshared();
                playerIndex = (Integer) connection.getObjectInputStream().readUnshared();
                System.out.println("[GuiStartPage]: Game object and player index received");

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            System.out.println("[GuiStartPage]: Launching GuiBoard");
            launchGuiBoard(newGame, playerIndex);
        }


    }

    private void launchGuiBoard(Game newGame, Integer playerIndex) {
        dispose();
        GuiBoard guiBoard = new GuiBoard(connection, newGame, playerIndex);
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
