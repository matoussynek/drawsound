package drawsoundfx.utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.awt.image.BufferedImage;

public class ImageProcessing {

    public static BufferedImage getGreyScaleImage(BufferedImage image, int greyscaleFilter) {
        if (image == null) {
            System.err.println("Image not selected...");
            return null;
        }
        BufferedImage greyImage = new BufferedImage(image.getWidth(),
                image.getHeight(), image.getType());
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int pixel = applyFilters(image.getRGB(x, y), greyscaleFilter);
                greyImage.setRGB(x, y, pixel);
            }
        }

        return greyImage;
    }

    public static BufferedImage createColorImage(BufferedImage originalImage, int redFilter, int greenFilter, int blueFilter) {
        BufferedImage colorImage = new BufferedImage(originalImage.getWidth(),
                originalImage.getHeight(), originalImage.getType());

        for (int x = 0; x < originalImage.getWidth(); x++) {
            for (int y = 0; y < originalImage.getHeight(); y++) {
                int pixel = applyFilters(originalImage.getRGB(x, y), redFilter, greenFilter, blueFilter);
                colorImage.setRGB(x, y, pixel);
            }
        }

        return colorImage;
    }

    private static int applyFilters(int pixel, float redFilter, float greenFilter, float blueFilter) {
        float red = ((pixel & 0xFF0000) >> 16) * (redFilter / 100);
        float green = ((pixel & 0xFF00) >> 8) * (greenFilter / 100);
        float blue = (pixel & 0xFF) * (blueFilter / 100);
        return 0xFF000000 + ((int)red << 16) + ((int)green << 8) + (int)blue;
    }

    private static int applyFilters(int pixel, float greyscaleFilter){
        int r = (pixel >> 16) & 0xFF;
        int g = (pixel >> 8) & 0xFF;
        int b = (pixel & 0xFF);

        // Normalize and gamma correct:
        double rr = Math.pow(r / 255.0, 2.2);
        double gg = Math.pow(g / 255.0, 2.2);
        double bb = Math.pow(b / 255.0, 2.2);

        // Calculate luminance:
        double lum = 0.2126 * rr + 0.7152 * gg + 0.0722 * bb;

        // Gamma compand and rescale to byte range:
        int greyLevel = (int) (255.0 * Math.pow(lum, 1.0 / 2.2));

        float newRed = lerp(r, greyLevel, (greyscaleFilter / 100));
        float newGreen = lerp(g, greyLevel, (greyscaleFilter / 100));
        float newBlue = lerp(b, greyLevel, (greyscaleFilter / 100));

        return 0xFF000000 + ((int)newRed << 16) + ((int)newGreen << 8) + (int)newBlue;
    }

    private static float lerp(float a, float b, float f)
    {
        return a + f * (b - a);
    }

    public static Image convertToFxImage(BufferedImage image) {
        WritableImage wr = null;
        if (image != null) {
            wr = new WritableImage(image.getWidth(), image.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    pw.setArgb(x, y, image.getRGB(x, y));
                }
            }
        }

        return new ImageView(wr).getImage();
    }
}
