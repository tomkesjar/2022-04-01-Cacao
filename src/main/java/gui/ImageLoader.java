package gui;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class ImageLoader {

    enum ImageTypes {
        TILE,
        ICON
    }

    public ImageLoader(int TILES_MAX_WIDTH, int TILES_MAX_HEIGHT) {
        this.TILES_MAX_WIDTH = TILES_MAX_WIDTH;
        this.TILES_MAX_HEIGHT = TILES_MAX_HEIGHT;
        this.ICON_MAX_WIDTH = 20;
        this.ICON_MAX_HEIGHT = 20;

    }

    private int TILES_MAX_WIDTH;
    private int TILES_MAX_HEIGHT;
    private int ICON_MAX_WIDTH;
    private int ICON_MAX_HEIGHT;
    private BufferedImage market1, market2, market3,
            mine1, mine2,
            plantation1, plantation2,
            sun, temple, water,
            empty;

    private BufferedImage coinIcon, waterIcon, beanIcon, shrineIcon, templeIcon, pointIcon, rankIcon;

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
            market1 = getScaledImage(urlForMarket1, ImageTypes.TILE);

            URL urlForMarket2 = this.getClass().getResource("/icons/jungle/market2.jpg");
            market2 = getScaledImage(urlForMarket2, ImageTypes.TILE);

            URL urlForMarket3 = this.getClass().getResource("/icons/jungle/market3.jpg");
            market3 = getScaledImage(urlForMarket3, ImageTypes.TILE);

            URL urlForMine1 = this.getClass().getResource("/icons/jungle/mine1.jpg");
            mine1 = getScaledImage(urlForMine1, ImageTypes.TILE);

            URL urlForMine2 = this.getClass().getResource("/icons/jungle/mine2.jpg");
            mine2 = getScaledImage(urlForMine2, ImageTypes.TILE);


            URL urlForPlantation1 = this.getClass().getResource("/icons/jungle/plantation1.jpg");
            plantation1 = getScaledImage(urlForPlantation1, ImageTypes.TILE);

            URL urlForPlantation2 = this.getClass().getResource("/icons/jungle/plantation2.jpg");
            plantation2 = getScaledImage(urlForPlantation2, ImageTypes.TILE);

            URL urlForSun = this.getClass().getResource("/icons/jungle/sun.jpg");
            sun = getScaledImage(urlForSun, ImageTypes.TILE);

            URL urlForTileTemple = this.getClass().getResource("/icons/jungle/tileTemple.jpg");
            temple = getScaledImage(urlForTileTemple, ImageTypes.TILE);

            URL urlForWater = this.getClass().getResource("/icons/jungle/water.jpg");
            water = getScaledImage(urlForWater, ImageTypes.TILE);

            URL urlForEmpty = this.getClass().getResource("/icons/jungle/dummy_empty.png");
            empty = getScaledImage(urlForEmpty, ImageTypes.TILE);


        } catch (IOException e) {
            System.out.println("[ImageLoader]: Error with loading the images icons");
            e.printStackTrace();
        }
    }

    public void loadWorkerImages() {
        try {
            URL urlForR111 = this.getClass().getResource("/icons/worker/dummy_r1111.png");
            r1111 = getScaledImage(urlForR111, ImageTypes.TILE);

            URL urlForR2101 = this.getClass().getResource("/icons/worker/dummy_r2101.png");
            r2101 = getScaledImage(urlForR2101, ImageTypes.TILE);

            URL urlForR3100 = this.getClass().getResource("/icons/worker/dummy_r3100.png");
            r3100 = getScaledImage(urlForR3100, ImageTypes.TILE);

            URL urlForR3001 = this.getClass().getResource("/icons/worker/dummy_r3001.png");
            r3001 = getScaledImage(urlForR3001, ImageTypes.TILE);


            URL urlForB111 = this.getClass().getResource("/icons/worker/dummy_b1111.png");
            b1111 = getScaledImage(urlForB111, ImageTypes.TILE);

            URL urlForB2101 = this.getClass().getResource("/icons/worker/dummy_b2101.png");
            b2101 = getScaledImage(urlForB2101, ImageTypes.TILE);

            URL urlForB3100 = this.getClass().getResource("/icons/worker/dummy_b3100.png");
            b3100 = getScaledImage(urlForB3100, ImageTypes.TILE);

            URL urlForB3001 = this.getClass().getResource("/icons/worker/dummy_b3001.png");
            b3001 = getScaledImage(urlForB3001, ImageTypes.TILE);



            URL urlForG111 = this.getClass().getResource("/icons/worker/dummy_g1111.png");
            g1111 = getScaledImage(urlForG111, ImageTypes.TILE);

            URL urlForG2101 = this.getClass().getResource("/icons/worker/dummy_g2101.png");
            g2101 = getScaledImage(urlForG2101, ImageTypes.TILE);

            URL urlForG3100 = this.getClass().getResource("/icons/worker/dummy_g3100.png");
            g3100 = getScaledImage(urlForG3100, ImageTypes.TILE);

            URL urlForG3001 = this.getClass().getResource("/icons/worker/dummy_g3001.png");
            g3001 = getScaledImage(urlForG3001, ImageTypes.TILE);



            URL urlForY111 = this.getClass().getResource("/icons/worker/dummy_y1111.png");
            y1111 = getScaledImage(urlForY111, ImageTypes.TILE);

            URL urlForY2101 = this.getClass().getResource("/icons/worker/dummy_y2101.png");
            y2101 = getScaledImage(urlForY2101, ImageTypes.TILE);

            URL urlForY3100 = this.getClass().getResource("/icons/worker/dummy_y3100.png");
            y3100 = getScaledImage(urlForY3100, ImageTypes.TILE);

            URL urlForY3001 = this.getClass().getResource("/icons/worker/dummy_y3001.png");
            y3001 = getScaledImage(urlForY3001, ImageTypes.TILE);

            //TODO SOS add remaining

        } catch (IOException e) {
            System.out.println("[ImageLoader]: Error with loading the images icons");
            e.printStackTrace();
        }
    }

    public void loadIconImages() {
        // coinIcon, waterIcon, beanIcon, shrineIcon, templeIcon, pointIcon, rankIcon;
        try {
            URL urlForCoinIcon = this.getClass().getResource("/icons/playerPanel/coin.png");
            coinIcon = getScaledImage(urlForCoinIcon, ImageTypes.ICON);

            URL urlForWaterIcon = this.getClass().getResource("/icons/playerPanel/water.png");
            waterIcon = getScaledImage(urlForWaterIcon, ImageTypes.ICON);

            URL urlForBeanIcon = this.getClass().getResource("/icons/playerPanel/bean.png");
            beanIcon = getScaledImage(urlForBeanIcon, ImageTypes.ICON);

            URL urlForShrineIcon = this.getClass().getResource("/icons/playerPanel/shrine.png");
            shrineIcon = getScaledImage(urlForShrineIcon, ImageTypes.ICON);

            URL urlForTempleIcon = this.getClass().getResource("/icons/playerPanel/temple.jpg");
            templeIcon = getScaledImage(urlForTempleIcon, ImageTypes.ICON);


            URL urlForPointIcon = this.getClass().getResource("/icons/playerPanel/sum.png");
            pointIcon = getScaledImage(urlForPointIcon, ImageTypes.ICON);

            URL urlForRankIcon = this.getClass().getResource("/icons/playerPanel/rank.png");
            rankIcon = getScaledImage(urlForRankIcon, ImageTypes.ICON);

        } catch (IOException e) {
            System.out.println("[ImageLoader]: Error with loading the images icons");
            e.printStackTrace();
        }
    }



    private BufferedImage getScaledImage(URL urlForTileIcon, ImageTypes type) throws IOException {
        //return (BufferedImage) ImageIO.read(urlForMarket1).getScaledInstance(TILES_MAX_WIDTH, TILES_MAX_HEIGHT, java.awt.Image.SCALE_SMOOTH);
        switch (type){
            case TILE: return resizeImage(ImageIO.read(urlForTileIcon), TILES_MAX_WIDTH, TILES_MAX_HEIGHT);
            case ICON: return resizeImage(ImageIO.read(urlForTileIcon), ICON_MAX_WIDTH, ICON_MAX_HEIGHT);
        }
        return null;
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

    public BufferedImage getCoinIcon() {
        return coinIcon;
    }

    public BufferedImage getWaterIcon() {
        return waterIcon;
    }

    public BufferedImage getBeanIcon() {
        return beanIcon;
    }

    public BufferedImage getShrineIcon() {
        return shrineIcon;
    }

    public BufferedImage getTempleIcon() {
        return templeIcon;
    }

    public BufferedImage getPointIcon() {
        return pointIcon;
    }

    public BufferedImage getRankIcon() {
        return rankIcon;
    }
}
