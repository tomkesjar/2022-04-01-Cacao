package decorator;

import javax.swing.*;
import java.net.URL;
import java.util.HashMap;

public class Decorator implements Visual {
    private URL url;
    private ImageIcon aaa, bbb, ccc, ddd;
    private HashMap<String, ImageIcon> imageMap = new HashMap<>();
    private int width = 53, height = 50;


    @Override
    public ImageIcon returnImage(String imageName) {
        return null;
    }
}
