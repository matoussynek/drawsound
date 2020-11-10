package drawsoundfx.utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class EdgeDetection {

    private BufferedImage sourceImage;
    private BufferedImage outputImage;
    private double distance = 0;
    private int blacks = 0;
    private int whites = 0;

    public EdgeDetection() {
    }

    public EdgeDetection(BufferedImage source) {
        sourceImage = source;
        outputImage = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
    }

    public EdgeDetection(BufferedImage source, double distance) {
        this(source);
        this.distance = distance;
    }

    public BufferedImage getSourceImage() {
        return sourceImage;
    }

    public void setSourceImage(BufferedImage sourceImage) {
        this.sourceImage = sourceImage;
        outputImage = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
    }

    public BufferedImage getOutputImage() {
        return outputImage;
    }

    public void setOutputImage(BufferedImage outputImage) {
        this.outputImage = outputImage;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void detectEdges() {
        int leftPixel = 0;
        int rightPixel = 0;
        int downPixel = 0;
        blacks = 0;
        whites = 0;
        for (int row = 0; row < sourceImage.getHeight(); row++) {
            for (int col = 0; col < sourceImage.getWidth(); col++) {
                leftPixel = sourceImage.getRGB(col, row);
                rightPixel = sourceImage.getRGB((col + 1) % sourceImage.getWidth(), row);
                downPixel = sourceImage.getRGB(col, (row + 1) % sourceImage.getHeight());
                if (colorDistance(leftPixel, rightPixel) > distance ||
                        colorDistance(leftPixel, downPixel) > distance) {
                    outputImage.setRGB(col, row, Color.BLACK.getRGB());
                    blacks++;
                } else {
                    outputImage.setRGB(col, row, Color.WHITE.getRGB());
                    whites++;
                }

            }
        }
    }

    private double colorDistance(int leftPixel, int rightPixel) {
        int red1 = (leftPixel & 0x00ff0000) >> 16;
        int green1 = (leftPixel & 0x0000ff00) >> 8;
        int blue1 = leftPixel & 0x000000ff;
        int red2 = (rightPixel & 0x00ff0000) >> 16;
        int green2 = (rightPixel & 0x0000ff00) >> 8;
        int blue2 = rightPixel & 0x000000ff;
        double a = red2 - red1;
        double b = green2 - green1;
        double c = blue2 - blue1;
        return Math.sqrt(a * a + b * b + c * c);
    }

    public float getEdges() {
        return blacks;
    }
}
