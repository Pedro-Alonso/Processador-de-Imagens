import java.awt.image.BufferedImage;

public class OtsuThreshold {
    public int computeOtsuThreshold(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] histogram = new int[256];
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                histogram[(image.getRGB(x, y) >> 16) & 0xFF]++;

        int total = width * height;
        float sum = 0;
        for (int t = 0; t < 256; t++) sum += t * histogram[t];

        float sumB = 0, wB = 0, wF = 0, varMax = 0;
        int threshold = 0;
        for (int t = 0; t < 256; t++) {
            wB += histogram[t];
            if (wB == 0) continue;
            wF = total - wB;
            if (wF == 0) break;
            sumB += t * histogram[t];
            float mB = sumB / wB;
            float mF = (sum - sumB) / wF;
            float varBetween = wB * wF * (mB - mF) * (mB - mF);
            if (varBetween > varMax) {
                varMax = varBetween;
                threshold = t;
            }
        }
        return threshold;
    }

    public BufferedImage binarize(BufferedImage image) {
        int threshold = computeOtsuThreshold(image);
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                int gray = (image.getRGB(x, y) >> 16) & 0xFF;
                int v = (gray >= threshold) ? 255 : 0;
                int rgb = (v << 16) | (v << 8) | v;
                result.setRGB(x, y, rgb);
            }
        return result;
    }

    public BufferedImage limiarize(BufferedImage image) {
        int threshold = computeOtsuThreshold(image);
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                int gray = (image.getRGB(x, y) >> 16) & 0xFF;
                int rgb = (gray >= threshold) ? image.getRGB(x, y) : 0;
                result.setRGB(x, y, rgb);
            }
        return result;
    }
}
