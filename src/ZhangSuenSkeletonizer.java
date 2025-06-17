import java.awt.image.BufferedImage;

public class ZhangSuenSkeletonizer {
    public BufferedImage skeletonize(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] binary = new int[width][height];
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                binary[x][y] = ((image.getRGB(x, y) & 0xFF) > 128) ? 1 : 0;

        boolean changed;
        do {
            changed = false;
            boolean[][] toRemove = new boolean[width][height];
            // Step 1
            for (int x = 1; x < width - 1; x++)
                for (int y = 1; y < height - 1; y++) {
                    int p2 = binary[x][y - 1], p3 = binary[x + 1][y - 1], p4 = binary[x + 1][y],
                        p5 = binary[x + 1][y + 1], p6 = binary[x][y + 1], p7 = binary[x - 1][y + 1],
                        p8 = binary[x - 1][y], p9 = binary[x - 1][y - 1];
                    int A = 0;
                    if (p2 == 0 && p3 == 1) A++;
                    if (p3 == 0 && p4 == 1) A++;
                    if (p4 == 0 && p5 == 1) A++;
                    if (p5 == 0 && p6 == 1) A++;
                    if (p6 == 0 && p7 == 1) A++;
                    if (p7 == 0 && p8 == 1) A++;
                    if (p8 == 0 && p9 == 1) A++;
                    if (p9 == 0 && p2 == 1) A++;
                    int B = p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9;
                    if (binary[x][y] == 1 && B >= 2 && B <= 6 && A == 1 &&
                        (p2 * p4 * p6 == 0) && (p4 * p6 * p8 == 0)) {
                        toRemove[x][y] = true;
                        changed = true;
                    }
                }
            for (int x = 1; x < width - 1; x++)
                for (int y = 1; y < height - 1; y++)
                    if (toRemove[x][y]) binary[x][y] = 0;
            // Step 2
            toRemove = new boolean[width][height];
            for (int x = 1; x < width - 1; x++)
                for (int y = 1; y < height - 1; y++) {
                    int p2 = binary[x][y - 1], p3 = binary[x + 1][y - 1], p4 = binary[x + 1][y],
                        p5 = binary[x + 1][y + 1], p6 = binary[x][y + 1], p7 = binary[x - 1][y + 1],
                        p8 = binary[x - 1][y], p9 = binary[x - 1][y - 1];
                    int A = 0;
                    if (p2 == 0 && p3 == 1) A++;
                    if (p3 == 0 && p4 == 1) A++;
                    if (p4 == 0 && p5 == 1) A++;
                    if (p5 == 0 && p6 == 1) A++;
                    if (p6 == 0 && p7 == 1) A++;
                    if (p7 == 0 && p8 == 1) A++;
                    if (p8 == 0 && p9 == 1) A++;
                    if (p9 == 0 && p2 == 1) A++;
                    int B = p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9;
                    if (binary[x][y] == 1 && B >= 2 && B <= 6 && A == 1 &&
                        (p2 * p4 * p8 == 0) && (p2 * p6 * p8 == 0)) {
                        toRemove[x][y] = true;
                        changed = true;
                    }
                }
            for (int x = 1; x < width - 1; x++)
                for (int y = 1; y < height - 1; y++)
                    if (toRemove[x][y]) binary[x][y] = 0;
        } while (changed);

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++) {
                int v = binary[x][y] * 255;
                int rgb = (v << 16) | (v << 8) | v;
                result.setRGB(x, y, rgb);
            }
        return result;
    }
}
