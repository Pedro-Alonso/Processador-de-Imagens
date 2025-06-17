import java.awt.image.BufferedImage;

public class HSLUtils {
    public static int[] rgbToHsl(int r, int g, int b) {
        float rf = r / 255f, gf = g / 255f, bf = b / 255f;
        float max = Math.max(rf, Math.max(gf, bf));
        float min = Math.min(rf, Math.min(gf, bf));
        float h = 0, s, l = (max + min) / 2;
        float d = max - min;
        if (d == 0) h = 0;
        else if (max == rf) h = ((gf - bf) / d) % 6;
        else if (max == gf) h = ((bf - rf) / d) + 2;
        else h = ((rf - gf) / d) + 4;
        h = (h * 60);
        if (h < 0) h += 360;
        s = (d == 0) ? 0 : d / (1 - Math.abs(2 * l - 1));
        return new int[] { Math.round(h / 360 * 255), Math.round(s * 255), Math.round(l * 255) };
    }

    public static int[] hslToRgb(int h, int s, int l) {
        float hf = h / 255f * 360f, sf = s / 255f, lf = l / 255f;
        float c = (1 - Math.abs(2 * lf - 1)) * sf;
        float x = c * (1 - Math.abs((hf / 60) % 2 - 1));
        float m = lf - c / 2;
        float rf = 0, gf = 0, bf = 0;
        if (hf < 60) { rf = c; gf = x; }
        else if (hf < 120) { rf = x; gf = c; }
        else if (hf < 180) { gf = c; bf = x; }
        else if (hf < 240) { gf = x; bf = c; }
        else if (hf < 300) { rf = x; bf = c; }
        else { rf = c; bf = x; }
        int r = Math.round((rf + m) * 255);
        int g = Math.round((gf + m) * 255);
        int b = Math.round((bf + m) * 255);
        return new int[] { r, g, b };
    }

    public BufferedImage equalizeHslLuminance(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][][] hsl = new int[width][height][3];
        int[] hist = new int[256];
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
                hsl[x][y] = rgbToHsl(r, g, b);
                hist[hsl[x][y][2]]++;
            }
        int[] cdf = new int[256];
        cdf[0] = hist[0];
        for (int i = 1; i < 256; i++) cdf[i] = cdf[i - 1] + hist[i];
        int total = width * height;
        int[] newL = new int[256];
        for (int i = 0; i < 256; i++) newL[i] = Math.min(255, Math.round(255f * cdf[i] / total));
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++) {
                int[] hslPix = hsl[x][y];
                int[] rgbPix = hslToRgb(hslPix[0], hslPix[1], newL[hslPix[2]]);
                int rgb = (rgbPix[0] << 16) | (rgbPix[1] << 8) | rgbPix[2];
                result.setRGB(x, y, rgb);
            }
        return result;
    }
}
