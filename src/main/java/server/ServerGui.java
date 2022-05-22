package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalTime;

public class ServerGui extends JFrame {
    private JPanel inputPanel;
    private JTextArea textArea = new JTextArea();
    private JScrollPane scrollPane = new JScrollPane(textArea);

    private JLabel numberOfPlayersText;
    private JLabel waitTimeText;
    private JLabel serverNameText;
    private JLabel serverName;
    private JTextField numberOfPlayersInput = new JTextField();
    private JTextField waitTimeInput = new JTextField();
    private JMenuBar menuBar = new JMenuBar();

    private JButton sendButton;

    private static Logger logger;

    private volatile boolean hasSentInputToServer = false;

    public ServerGui() {
        super("ServerGui");
        setFont(new Font("Times New Roman", Font.PLAIN, 12));

        logger = (Logger) LogManager.getLogger(ServerGui.class);

        textArea.setForeground(Color.GREEN);

        textArea.setBackground(Color.GRAY);
        textArea.setEditable(false);
        textArea.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        //textArea.setBackground(new Color(0, 0, 0));

        numberOfPlayersText = new JLabel("Number of Players:");
        numberOfPlayersText.setPreferredSize(new Dimension(200, 20));

        serverNameText = new JLabel("Server name:");
        serverNameText.setPreferredSize(new Dimension(200, 20));

        String hostName = "no data";
        try {
            hostName = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        serverName = new JLabel(hostName);
        serverName.setPreferredSize(new Dimension(200, 20));

        numberOfPlayersInput.setText("4");
        numberOfPlayersInput.setPreferredSize(new Dimension(200, 20));
        numberOfPlayersInput.setToolTipText("Number of Players");
        numberOfPlayersInput.setForeground(new Color(0, 0, 0));
        numberOfPlayersInput.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        numberOfPlayersInput.setBackground(new Color(200, 200, 200));

        waitTimeText = new JLabel("Waiting Time (in sec):");
        waitTimeText.setPreferredSize(new Dimension(200, 20));
        waitTimeInput.setText("90");
        waitTimeInput.setPreferredSize(new Dimension(200, 20));
        waitTimeInput.setToolTipText("Waiting Time (in sec)");
        waitTimeInput.setForeground(new Color(0, 0, 0));
        waitTimeInput.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        waitTimeInput.setBackground(new Color(200, 200, 200));

        sendButton = new JButton("Send and Connect");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!hasSentInputToServer()) {
                    if ((Integer.parseInt(getNumberOfPlayersInput().getText()) > 0
                            && Integer.parseInt(getNumberOfPlayersInput().getText()) < 5)
                            && (Integer.parseInt(getWaitTimeInput().getText()) >= 20
                            && Integer.parseInt(getWaitTimeInput().getText()) <= 300)) {
                        hasSentInputToServer = true;
                        appendToTextArea("Server started at " + LocalTime.now());
                        logger.info("Server started at " + LocalTime.now());
                        synchronized (sendButton) {
                            sendButton.notify();
                        }
                        System.out.println("[ServerGui]: successful click");
                    } else if (!(Integer.parseInt(getNumberOfPlayersInput().getText()) > 0
                            && Integer.parseInt(getNumberOfPlayersInput().getText()) < 5)) {
                        appendToTextArea("Number of Players should be between 1-4");
                        System.out.println("[ServerGui]: Number of Players should be between 1-4");
                        logger.info("[ServerGui]: Number of Players should be between 1-4");
                    } else if (!(Integer.parseInt(getWaitTimeInput().getText()) >= 20
                            && Integer.parseInt(getWaitTimeInput().getText()) <= 300)) {
                        appendToTextArea("Wait time should be between 20 sec and 300 sec");
                        logger.info("Wait time should be between 20 sec and 300 sec");
                        System.out.println("[ServerGui]: Wait time should be between 20 sec and 300 sec");
                    }
                }
            }
        });

        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(400,400));

        setSize(400, 600);

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        this.add(numberOfPlayersText, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        this.add(numberOfPlayersInput, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        this.add(waitTimeText, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 1;
        this.add(waitTimeInput, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        this.add(serverNameText, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 1;
        this.add(serverName, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        this.add(sendButton, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        this.add(scrollPane, c);


        this.pack();
        this.setVisible(true);
        this.requestFocusInWindow();
    }

    public JTextArea getTextArea() {
        return textArea;
    }

    public JTextField getNumberOfPlayersInput() {
        return numberOfPlayersInput;
    }

    public JTextField getWaitTimeInput() {
        return waitTimeInput;
    }

    public boolean hasSentInputToServer() {
        return hasSentInputToServer;
    }

    public void setTextArea(JTextArea textArea) {
        this.textArea = textArea;
    }

    public void appendToTextArea(String text) {
        textArea.append(System.lineSeparator());
        textArea.append(text);
        textArea.setCaretPosition(textArea.getText().length());
    }

    public JButton getSendButton() {
        return sendButton;
    }
}
