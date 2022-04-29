package gui;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class ImageLoader {

    public ImageLoader(int TILES_MAX_WIDTH, int TILES_MAX_HEIGHT) {
        this.TILES_MAX_WIDTH = TILES_MAX_WIDTH;
        this.TILES_MAX_HEIGHT = TILES_MAX_HEIGHT;
    }

    private int TILES_MAX_WIDTH;
    private int TILES_MAX_HEIGHT;
    private BufferedImage market1, market2, market3,
            mine1, mine2,
            plantation1, plantation2,
            sun, temple, water,
            empty;

    private BufferedImage
            r1111,
            r2101,
            r3100,
            r3001,
            b1111,
            b2101,
            b3100,
            b3001,
            g1111,
            g2101,
            g3100,
            g3001,
            y1111,
            y2101,
            y3100,
            y3001;



    public void loadJungleImages() {
        try {
            URL urlForMarket1 = this.getClass().getResource("/icons/jungle/market1.jpg");
            market1 = getScaledImage(urlForMarket1);

            URL urlForMarket2 = this.getClass().getResource("/icons/jungle/market2.jpg");
            market2 = getScaledImage(urlForMarket2);

            URL urlForMarket3 = this.getClass().getResource("/icons/jungle/market3.jpg");
            market3 = getScaledImage(urlForMarket3);

            URL urlForMine1 = this.getClass().getResource("/icons/jungle/mine1.jpg");
            mine1 = getScaledImage(urlForMine1);

            URL urlForMine2 = this.getClass().getResource("/icons/jungle/mine2.jpg");
            mine2 = getScaledImage(urlForMine2);


            URL urlForPlantation1 = this.getClass().getResource("/icons/jungle/plantation1.jpg");
            plantation1 = getScaledImage(urlForPlantation1);

            URL urlForPlantation2 = this.getClass().getResource("/icons/jungle/plantation2.jpg");
            plantation2 = getScaledImage(urlForPlantation2);

            URL urlForSun = this.getClass().getResource("/icons/jungle/sun.jpg");
            sun = getScaledImage(urlForSun);

            URL urlForTileTemple = this.getClass().getResource("/icons/jungle/tileTemple.jpg");
            temple = getScaledImage(urlForTileTemple);

            URL urlForWater = this.getClass().getResource("/icons/jungle/water.jpg");
            water = getScaledImage(urlForWater);

            URL urlForEmpty = this.getClass().getResource("/icons/jungle/dummy_empty.png");
            empty = getScaledImage(urlForEmpty);


        } catch (IOException e) {
            System.out.println("[ImageLoader]: Error with loading the images icons");
            e.printStackTrace();
        }
    }

    public void loadWorkerImages() {
        try {
            URL urlForR111 = this.getClass().getResource("/icons/worker/dummy_r1111.png");
            r1111 = getScaledImage(urlForR111);

            URL urlForR2101 = this.getClass().getResource("/icons/worker/dummy_r2101.png");
            r2101 = getScaledImage(urlForR2101);

            URL urlForR3100 = this.getClass().getResource("/icons/worker/dummy_r3100.png");
            r3100 = getScaledImage(urlForR3100);

            URL urlForR3001 = this.getClass().getResource("/icons/worker/dummy_r3001.png");
            r3001 = getScaledImage(urlForR3001);


            URL urlForB111 = this.getClass().getResource("/icons/worker/dummy_b1111.png");
            b1111 = getScaledImage(urlForB111);

            URL urlForB2101 = this.getClass().getResource("/icons/worker/dummy_b2101.png");
            b2101 = getScaledImage(urlForB2101);

            URL urlForB3100 = this.getClass().getResource("/icons/worker/dummy_b3100.png");
            b3100 = getScaledImage(urlForB3100);

            URL urlForB3001 = this.getClass().getResource("/icons/worker/dummy_b3001.png");
            b3001 = getScaledImage(urlForB3001);


/*
            URL urlForG111 = this.getClass().getResource("/icons/worker/dummy_g1111.png");
            g1111 = getScaledImage(urlForG111);

            URL urlForG2101 = this.getClass().getResource("/icons/worker/dummy_g2101.png");
            g2101 = getScaledImage(urlForG2101);

            URL urlForG3100 = this.getClass().getResource("/icons/worker/dummy_g3100.png");
            g3100 = getScaledImage(urlForG3100);

            URL urlForG3001 = this.getClass().getResource("/icons/worker/dummy_g3001.png");
            g3001 = getScaledImage(urlForG3001);



            URL urlForY111 = this.getClass().getResource("/icons/worker/dummy_y1111.png");
            y1111 = getScaledImage(urlForY111);

            URL urlForY2101 = this.getClass().getResource("/icons/worker/dummy_y2101.png");
            y2101 = getScaledImage(urlForY2101);

            URL urlForY3100 = this.getClass().getResource("/icons/worker/dummy_y3100.png");
            y3100 = getScaledImage(urlForY3100);

            URL urlForY3001 = this.getClass().getResource("/icons/worker/dummy_y3001.png");
            y3001 = getScaledImage(urlForY3001);
*/
            //TODO SOS add remaining

        } catch (IOException e) {
            System.out.println("[ImageLoader]: Error with loading the images icons");
            e.printStackTrace();
        }
    }

    private BufferedImage getScaledImage(URL urlForTileIcon) throws IOException {
        //return (BufferedImage) ImageIO.read(urlForMarket1).getScaledInstance(TILES_MAX_WIDTH, TILES_MAX_HEIGHT, java.awt.Image.SCALE_SMOOTH);
        return resizeImage(ImageIO.read(urlForTileIcon), TILES_MAX_WIDTH, TILES_MAX_HEIGHT);
    }

    private  BufferedImage resizeImage(BufferedImage image, int width, int height) {
        int type=0;
        type = image.getType() == 0? BufferedImage.TYPE_INT_ARGB : image.getType();
        BufferedImage resizedImage = new BufferedImage(width, height,type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    public static BufferedImage rotateClockwise90(int numberOfRotation, BufferedImage originalImage) {
        if (numberOfRotation % 4 == 0) {
            return originalImage;
        }
        double rotationLevelInRadian = (Math.PI * 2) / 4 * numberOfRotation;
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        BufferedImage rotatedImage;
        if (numberOfRotation % 2 == 1) {
            rotatedImage = new BufferedImage(originalImage.getHeight(), originalImage.getWidth(), originalImage.getType());
        } else {
            rotatedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), originalImage.getType());
        }

        Graphics2D graphics2D = rotatedImage.createGraphics();

        if (numberOfRotation == 1) {
            graphics2D.translate((height - width) / 2, (height - width) / 2);
            graphics2D.rotate(rotationLevelInRadian, height / 2, width / 2);
        } else if (numberOfRotation == 3) {
            graphics2D.translate((width - height) / 2, (width - height) / 2);
            graphics2D.rotate(rotationLevelInRadian, height / 2, width / 2);
        } else {
            graphics2D.translate(0, 0);
            graphics2D.rotate(rotationLevelInRadian, width / 2, height / 2);
        }
        graphics2D.drawRenderedImage(originalImage, null);
        return rotatedImage;
    }


    public BufferedImage getMarket1() {
        return market1;
    }

    public BufferedImage getMarket2() {
        return market2;
    }

    public BufferedImage getMarket3() {
        return market3;
    }

    public BufferedImage getMine1() {
        return mine1;
    }

    public BufferedImage getMine2() {
        return mine2;
    }

    public BufferedImage getPlantation1() {
        return plantation1;
    }

    public BufferedImage getPlantation2() {
        return plantation2;
    }

    public BufferedImage getSun() {
        return sun;
    }

    public BufferedImage getTemple() {
        return temple;
    }

    public BufferedImage getWater() {
        return water;
    }

    public BufferedImage getEmpty() {
        return empty;
    }

    public BufferedImage getR1111() {
        return r1111;
    }

    public BufferedImage getR2101() {
        return r2101;
    }

    public BufferedImage getR3100() {
        return r3100;
    }

    public BufferedImage getR3001() {
        return r3001;
    }

    public BufferedImage getB1111() {
        return b1111;
    }

    public BufferedImage getB2101() {
        return b2101;
    }

    public BufferedImage getB3100() {
        return b3100;
    }

    public BufferedImage getB3001() {
        return b3001;
    }

    public BufferedImage getG1111() {
        return g1111;
    }

    public BufferedImage getG2101() {
        return g2101;
    }

    public BufferedImage getG3100() {
        return g3100;
    }

    public BufferedImage getG3001() {
        return g3001;
    }

    public BufferedImage getY1111() {
        return y1111;
    }

    public BufferedImage getY2101() {
        return y2101;
    }

    public BufferedImage getY3100() {
        return y3100;
    }

    public BufferedImage getY3001() {
        return y3001;
    }
}
