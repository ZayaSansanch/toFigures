import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import java.util.List;

public class Main {
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

        int iterations = 0;
        int recreateIterations = 0;
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
        int seed;

        Random random = new Random();
        if (Integer.parseInt(properties.getProperty("Generation.Seed")) != 0) {
            seed = Integer.parseInt(properties.getProperty("Generation.Seed"));
        } else {
            seed = random.nextInt(1000000000);
        }
        random.setSeed(seed);

        long startTime = System.currentTimeMillis();

        shape[] bestShapes = new shape[shapes.length];
        for (int i = 0; i < bestShapes.length; i++) {
            for (int j = 0; j < bestShapes.length * Integer.parseInt(properties.getProperty("Process.ReCreateCountOfShapes")); j++) {
                bestShapes[j] = new shape(input.getWidth(), input.getHeight(), random);
            }
        }
        
        while (recreateIterations < Integer.parseInt(properties.getProperty("Process.ReCreateIterations"))) {
//            shape bestShape = new shape();
//            while (iterations < Integer.parseInt(properties.getProperty("Process.Iterations"))) {
//                System.out.print("Iteration " + iterations + " - ");
//                for (int i = 0; i < shapes.length; i++) {
//                    System.out.print(i + " ");
//                    for (int x = 0; x < output.getWidth(); x++) {
//                        for (int y = 0; y < output.getHeight(); y++) {
//                            middle.setRGB(x, y, output.getRGB(x, y));
//                        }
//                    }
//                    shapes[i] = new shape();
//                    random = shapes[i].random(input.getWidth(), input.getHeight(), random);
//
//                    List<Point> polygon = new ArrayList<>();
//                    polygon.add(new Point(shapes[i].getXPoints()[0], shapes[i].getYPoints()[0]));
//                    polygon.add(new Point(shapes[i].getXPoints()[1], shapes[i].getYPoints()[1]));
//                    polygon.add(new Point(shapes[i].getXPoints()[2], shapes[i].getYPoints()[2]));
//                    polygon.add(new Point(shapes[i].getXPoints()[3], shapes[i].getYPoints()[3]));
//
//                    score = 0;
//                    for (int x = 0; x < input.getWidth(); x++) {
//                        for (int y = 0; y < input.getHeight(); y++) {
//                            Point point = new Point(x, y);
//                            if (PolygonUtils.isPointInsidePolygon(point, polygon)) {
//                                score += 255 + Math.abs(new Color(input.getRGB(x, y)).getRed() - shapes[i].getColor().getRed());
//                                score += 255 + Math.abs(new Color(input.getRGB(x, y)).getGreen() - shapes[i].getColor().getGreen());
//                                score += 255 + Math.abs(new Color(input.getRGB(x, y)).getBlue() - shapes[i].getColor().getBlue());
//                            }
//                        }
//                    }
//                    if (score > bestScore) {
//                        bestScore = score;
//                        bestShape = shapes[i];
//                    }
//                }
//                System.out.println();
//
//                g2d = middle.createGraphics();
//                g2d.setColor(bestShape.color);
//                g2d.fillPolygon(bestShape.getXPoints(), bestShape.getYPoints(), bestShape.getNPoints());
//                g2d.dispose();
//
//                for (int x = 0; x < output.getWidth(); x++) {
//                    for (int y = 0; y < output.getHeight(); y++) {
//                        output.setRGB(x, y, middle.getRGB(x, y));
//                    }
//                }
//
//                iterations++;
//            }
        }
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        int shapesGenerated = Integer.parseInt(properties.getProperty("Process.CountOfShapes")) * iterations;
        System.out.println(
            "Shapes generated: " + shapesGenerated + ", Time taken: " + timeElapsed + "ms, " +
            timeElapsed / shapesGenerated + "ms/shape" + " (" +
            shapesGenerated / (timeElapsed / 1000) + " shapes/s)" +
            ", Seed: " + seed
        );

        try {
            String folderPath = properties.getProperty("Log.fullLogsFolder");
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            FileWriter writer = new FileWriter(properties.getProperty("Log.Short"), true);

            writer.write(
            "Start time: " + startTime + ", End time: " + endTime + ", Time taken: " + timeElapsed + " ms, \n" +
                    timeElapsed / shapesGenerated + " ms/shape" + " (" + shapesGenerated / (timeElapsed / 1000) + " shapes/s), " +
                    "Seed: " + seed + ", \nIterations: " + iterations + ", Shapes in iteration: " + shapes.length +
                    ", Shapes generated: " + shapesGenerated + "\n\n"
            );

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileWriter writer = new FileWriter(properties.getProperty("Log.Full"), true);

            writer.write("    Input image data: \n" + input.getData());
            writer.write("\n");
            writer.write(
            "    Start time: " + startTime + ", End time: " + endTime + ", Time taken: " + timeElapsed + " ms, \n" +
                timeElapsed / shapesGenerated + " ms/shape" + " (" + shapesGenerated / (timeElapsed / 1000) + " shapes/s), " +
                "Seed: " + seed + ", \nIterations: " + iterations + ", Shapes in iteration: " + shapes.length +
                ", Shapes generated: " + shapesGenerated + "\n"
            );
            writer.write("    Output image data: \n" + output.getData() + "\n\n");

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ImageIO.write(output, "png", new File(properties.getProperty("Image.Output")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ProcessBuilder builder = new ProcessBuilder("open", properties.getProperty("Image.Output"));
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        new Main(args[0]);
    }

    static class shape {
        private Color color;
        private int[] xPoints;
        private int[] yPoints;

        public Color getColor() {
            return color;
        }
        public int[] getXPoints() {
            return xPoints;
        }
        public int getNPoints() {
            return xPoints.length;
        }
        public int[] getYPoints() {
            return yPoints;
        }

        public Random random(int sizeX, int sizeY, Random rand) {
            xPoints = new int[4];
            yPoints = new int[4];
            for (int i = 0; i < xPoints.length; i++) {
                xPoints[i] = rand.nextInt(sizeX);
            }
            for (int i = 0; i < yPoints.length; i++) {
                yPoints[i] = rand.nextInt(sizeY);
            }
            color = new Color(
                    rand.nextInt(5) * (255 / 4),
                    rand.nextInt(5) * (255 / 4),
                    rand.nextInt(5) * (255 / 4)
            );
            return rand;
        }
    }

    public static class PolygonUtils {
        public static boolean isPointInsidePolygon(Point point, List<Point> polygon) {
            int intersections = 0;

            for (int i = 0; i < polygon.size(); i++) {
                Point p1 = polygon.get(i);
                Point p2 = polygon.get((i + 1) % polygon.size());

                if (isIntersecting(point, p1, p2)) {
                    intersections++;
                }
            }

            return intersections % 2 == 1;
        }

        private static boolean isIntersecting(Point p, Point p1, Point p2) {
            return (p.y > Math.min(p1.y, p2.y)) && (p.y <= Math.max(p1.y, p2.y)) &&
                    (p.x <= Math.max(p1.x, p2.x)) && (p1.y != p2.y) ?
                    (p.y - p1.y) * (p2.x - p1.x) / (p2.y - p1.y) + p1.x > p.x : false;
        }
    }
}