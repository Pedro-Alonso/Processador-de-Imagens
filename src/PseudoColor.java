import java.awt.image.BufferedImage;

public class PseudoColor {
    public BufferedImage applyPseudoColor(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int gray = (image.getRGB(x, y) >> 16) & 0xFF;
                int r = 0, g = 0, b = 0;
                if (gray < 64) {
                    r = 0; g = 0; b = gray * 4;
                } else if (gray < 128) {
                    r = 0; g = (gray - 64) * 4; b = 255;
                } else if (gray < 192) {
                    r = 0; g = 255; b = 255 - (gray - 128) * 4;
                } else {
                    r = (gray - 192) * 4; g = 255; b = 0;
                }
                int rgb = (r << 16) | (g << 8) | b;
                result.setRGB(x, y, rgb);
            }
        }
        return result;
    }
}
