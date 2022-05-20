package client.gui;

import java.awt.*;
import javax.swing.JComponent;

public class AlphaContainer extends JComponent {
    private JComponent component;

    public AlphaContainer(JComponent component) {
        this.component = component;
        setLayout(new BorderLayout());
        setOpaque(false);
        component.setOpaque(false);
        add(component);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(component.getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}
