import java.awt.image.BufferedImage;

public class MaxFilter {
    public int[][][] applyMaxFilter(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][][] result = new int[height][width][3];

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int maxR = 0, maxG = 0, maxB = 0;
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        int rgb = image.getRGB(x + dx, y + dy);
                        int r = (rgb >> 16) & 0xFF;
                        int g = (rgb >> 8) & 0xFF;
                        int b = rgb & 0xFF;
                        if (r > maxR) maxR = r;
                        if (g > maxG) maxG = g;
                        if (b > maxB) maxB = b;
                    }
                }
                result[y][x][0] = maxR;
                result[y][x][1] = maxG;
                result[y][x][2] = maxB;
            }
        }
        return result;
    }

    public BufferedImage buildImageFromRGBMatrix(int[][][] rgbMatrix) {
        int height = rgbMatrix.length;
        int width = rgbMatrix[0].length;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int r = rgbMatrix[y][x][0];
                int g = rgbMatrix[y][x][1];
                int b = rgbMatrix[y][x][2];
                int rgb = (r << 16) | (g << 8) | b;
                image.setRGB(x, y, rgb);
            }
        }
        return image;
    }
}
