package gui;

import connection.ClientConnection;


import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;


public class ChatBoxPanel extends JPanel implements ActionListener, Runnable {
    private static final int PANEL_WIDTH = 275;

    private static final int TEXT_AREA_PANEL_HEIGHT = 450;
    private static final int TEXT_INPUT_HEIGHT = 30;
    private static final int DUMMY_PANEL_HEIGHT = 100;

    private int fontSize;
    private int opacity;
    private final String INITIAL_INPUT_TEXT = "Write something!";

    private JTextArea textArea;
    private JScrollPane scrollPane;
    private JTextField inputTextField;
    private JPanel dummyPanel;

    private ClientConnection connection;

    public ChatBoxPanel(ClientConnection connection, int fontSize, int opacity) {
        this.connection = connection;
        this.fontSize = fontSize;
        this.opacity = opacity;


        textArea = new JTextArea();
        scrollPane = new JScrollPane(textArea);
        //scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        inputTextField = new JTextField();


        ///**** DESIGN
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        this.setBackground(new Color(0,0,0,this.opacity));
        this.setOpaque(false);


        scrollPane.setBackground(new Color(0,0  ,0,0));
        scrollPane.setOpaque(false);

        textArea.setEditable(false);
        textArea.setBackground(new Color(0,0  ,0,0));
        textArea.setOpaque(false);
        textArea.setFont(new Font( "Calibri", Font.BOLD, this.fontSize));
        textArea.setForeground(Color.MAGENTA);
        Border textAreaBorder = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1);
        textArea.setBorder(textAreaBorder);
        textArea.setPreferredSize(new Dimension(PANEL_WIDTH, TEXT_AREA_PANEL_HEIGHT));

        //scrollPane.setBackground(new Color(0,0  ,0,this.opacity));
        //scrollPane.setOpaque(false);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        this.add(textArea, c);




        inputTextField.setText(INITIAL_INPUT_TEXT);
        inputTextField.setFont(new Font( "Calibri", Font.BOLD, this.fontSize));
        inputTextField.setForeground(Color.MAGENTA);
        inputTextField.setBackground(new Color(0,0,0, this.opacity));
        inputTextField.setOpaque(false);
        inputTextField.setPreferredSize(new Dimension(PANEL_WIDTH, TEXT_INPUT_HEIGHT));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        this.add(inputTextField, c);


        //placeholder dummy panel
        dummyPanel = new JPanel();
        dummyPanel.setPreferredSize(new Dimension(PANEL_WIDTH,DUMMY_PANEL_HEIGHT));
        dummyPanel.setBackground(new Color(0,0,0, 0));
        dummyPanel.setOpaque(false);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        this.add(dummyPanel, c);


        ///**** DESIGN

        inputTextField.addActionListener(this);



    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String inputText = inputTextField.getText();
        connection.getPrintWriter().println(inputText);
        System.out.println("[ChatBoxPanel]: ActionEvent triggered and sent, value=" + inputTextField.getText());
        inputTextField.setText("");
    }

    @Override
    public void run() {
        System.out.println("[ChatBoxPanel]: start listening to bufferedReader");
        String inputMessage = null;
        try {
            while ((inputMessage = connection.getBufferedReader().readLine()) != null) {
                textArea.append(inputMessage);
                System.out.println("[ChatBoxPanel]: message received, message=" + inputMessage);
                textArea.append(System.lineSeparator());
                textArea.setCaretPosition(textArea.getText().length());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
