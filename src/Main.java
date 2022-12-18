import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

public class Main {
    private int iterations;
    public Main(String confFile) {
//        Create a new Properties object;
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(confFile)) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        Read the image file;
        BufferedImage input;
        try {
            input = ImageIO.read(new File(properties.getProperty("Image.Input")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        Create the output & middle images;
        BufferedImage output = new BufferedImage(input.getWidth(), input.getHeight(), input.getType());
        BufferedImage middle = new BufferedImage(input.getWidth(), input.getHeight(), input.getType());

        this.iterations = 1;
        shape[] shapes = new shape[Integer.parseInt(properties.getProperty("Process.CountOfShapes"))];

        for (int x = 0; x < middle.getWidth(); x++) {
            for (int y = 0; y < middle.getHeight(); y++) {
                middle.setRGB(x, y, new Color(0, 0, 0).getRGB());
            }
        }

//        Working with image
        Graphics2D g2d;
        int score;
        int bestScore = 0;
        shape bestShape = new shape(0, 0);
        while (iterations < Integer.parseInt(properties.getProperty("Process.Iterations")) + 1) {
            System.out.print("Iteration: " + iterations + " - ");
            for (int i = 0; i < shapes.length; i++) {
                System.out.print(i + " ");
                for (int x = 0; x < output.getWidth(); x++) {
                    for (int y = 0; y < output.getHeight(); y++) {
                        middle.setRGB(x, y, output.getRGB(x, y));
                    }
                }

                shapes[i] = new shape(
                        Integer.parseInt(properties.getProperty("Generation.Seed")),
                        Math.round(Integer.parseInt(properties.getProperty("Generation.Seed")) / 2) * i
                );
                shapes[i].random(input.getWidth(), input.getHeight());

                g2d = middle.createGraphics();
                g2d.setColor(shapes[i].color);
                g2d.fillPolygon(shapes[i].getXPoints(), shapes[i].getYPoints(), shapes[i].getNPoints());
                g2d.dispose();

                score = 0;
                for (int x = 0; x < input.getWidth(); x++) {
                    for (int y = 0; y < input.getHeight(); y++) {
                        score += 255 - Math.abs(new Color(input.getRGB(x, y)).getRed() - new Color(middle.getRGB(x, y)).getRed());
                        score += 255 - Math.abs(new Color(input.getRGB(x, y)).getGreen() - new Color(middle.getRGB(x, y)).getGreen());
                        score += 255 - Math.abs(new Color(input.getRGB(x, y)).getBlue() - new Color(middle.getRGB(x, y)).getBlue());
//                        output.setRGB(x, y, middle.getRGB(x, y));
                    }
                }
//                System.out.println(score);
                if (score > bestScore) {
                    bestScore = score;
                    bestShape = shapes[i];
                }
            }
            System.out.println();

            g2d = output.createGraphics();
            g2d.setColor(bestShape.color);
            g2d.fillPolygon(bestShape.getXPoints(), bestShape.getYPoints(), bestShape.getNPoints());
            g2d.dispose();

            iterations++;
//            Graphics2D g2d = output.createGraphics();
//
//            g2d.setColor(Color.RED);
//
//            int[] xPoints = {100, 20, 30, 400};
//            int[] yPoints = {10, 200, 10, 1200};
//            int nPoints = 4;
//
//            g2d.fillPolygon(xPoints, yPoints, nPoints);
//
//            g2d.dispose();
//
//            iterations++;
        }

//        Write the output image;
        try {
            ImageIO.write(output, "png", new File(properties.getProperty("Image.Output")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        new Main(args[0]);
    }

    class shape {
        private Color color;
        private int[] xPoints;
        private int[] yPoints;
        private Random rand;

        public Color getColor() {
            return color;
        }
        public void setColor(Color color) {
            this.color = color;
        }
        public int[] getXPoints() {
            return xPoints;
        }
        public void setXPoints(int[] xPoints) {
            this.xPoints = xPoints;
        }
        public int getNPoints() {
            return xPoints.length;
        }
        public int[] getYPoints() {
            return yPoints;
        }
        public void setYPoints(int[] yPoints) {
            this.yPoints = yPoints;
        }

        public shape(int seed, int plusNum) {
            rand = new Random(seed + plusNum);
//            rand = new Random();
        }
        public void random(int sizeX, int sizeY) {
            xPoints = new int[4];
            yPoints = new int[4];
            for (int i = 0; i < xPoints.length; i++) {
                xPoints[i] = rand.nextInt(sizeX);
            }
            for (int i = 0; i < yPoints.length; i++) {
                yPoints[i] = rand.nextInt(sizeY);
            }
            color = new Color(
                    rand.nextInt(3) * (255 / 2),
                    rand.nextInt(3) * (255 / 2),
                    rand.nextInt(3) * (255 / 2)
            );
        }
    }
}