import java.awt.image.BufferedImage;

public class DCTUtils {

    private static double calculateAlpha(int k, int N) {
        if (k == 0) {
            return Math.sqrt(1.0 / N);
        } else {
            return Math.sqrt(2.0 / N);
        }
    }

    public double[][] dct2D(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        int N = Math.min(width, height);
        if (N > 128) { 
            N = 128;
        }

        double[][] gray = new double[N][N];
        for (int y = 0; y < N; y++) {
            for (int x = 0; x < N; x++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                gray[y][x] = Math.round(0.299 * r + 0.587 * g + 0.114 * b);
            }
        }

        double[][] C = new double[N][N];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                double alphaU = calculateAlpha(i, N);
                double alphaV = calculateAlpha(j, N);

                double sum = 0.0;
                for (int u = 0; u < N; u++) {
                    for (int v = 0; v < N; v++) {
                        sum += gray[u][v] * Math.cos(Math.PI * (2 * u + 1) * i / (2.0 * N)) *
                               Math.cos(Math.PI * (2 * v + 1) * j / (2.0 * N));
                    }
                }
                C[i][j] = alphaU * alphaV * sum;
            }
        }
        return C;
    }

    public BufferedImage idct2D(double[][] dctMatrix) {
        int N = dctMatrix.length; 

        double[][] F = new double[N][N];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                double sum = 0.0;
                for (int u = 0; u < N; u++) {
                    for (int v = 0; v < N; v++) {
                        double alphaU = calculateAlpha(u, N);
                        double alphaV = calculateAlpha(v, N);
                        sum += alphaU * alphaV * dctMatrix[u][v] *
                               Math.cos(Math.PI * (2 * i + 1) * u / (2.0 * N)) *
                               Math.cos(Math.PI * (2 * j + 1) * v / (2.0 * N));
                    }
                }
                F[i][j] = sum;
            }
        }

        BufferedImage image = new BufferedImage(N, N, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < N; y++) {
            for (int x = 0; x < N; x++) {
                int val = (int) Math.round(F[y][x]);
                val = Math.max(0, Math.min(255, val));
                int rgb = (val << 16) | (val << 8) | val;
                image.setRGB(x, y, rgb);
            }
        }
        return image;
    }

    public BufferedImage idct2DLowPass(BufferedImage originalImage, double[][] dctMatrix, double cutoffFrequency) {
        int N = dctMatrix.length;
        double[][] dctCopy = new double[N][N];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (Math.sqrt(i * i + j * j) >= cutoffFrequency) {
                    dctCopy[i][j] = 0; 
                } else {
                    dctCopy[i][j] = dctMatrix[i][j];
                }
            }
        }
        return idct2D(dctCopy);
    }

    public BufferedImage idct2DHighPass(BufferedImage originalImage, double[][] dctMatrix, double cutoffFrequency) {
        int N = dctMatrix.length;
        double[][] dctCopy = new double[N][N];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (Math.sqrt(i * i + j * j) <= cutoffFrequency) {
                    dctCopy[i][j] = 0; 
                } else {
                    dctCopy[i][j] = dctMatrix[i][j];
                }
            }
        }
        return idct2D(dctCopy);
    }
}