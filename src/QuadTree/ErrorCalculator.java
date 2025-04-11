package QuadTree;

// import java.util.Map;
// import java.util.HashMap;

public class ErrorCalculator {
    
    /**
     * API ke kelas QuadTreeBuilder
     * 1 - Variance
     * 2 - Mean Absolute Deviation (MAD)
     * 3 - Max Pixel Difference
     * 4 - Entropy
     */
    public double calculateError(int[][] image, int width, int height, int errorMethod) {
        switch (errorMethod) {
            case 1:
                return variance(image, width, height);
            case 2:
                return MAD(image, width, height);
            case 3:
                return MPD(image, width, height);
            case 4:
                return entropy(image, width, height);
            default:
                return 0;
        }
    }

    // mengambil nilai rgb dari sebuah pixel
    public int getColor(int pixel, String color) {
        switch (color) {
            case "red":
                return (pixel >> 16) & 0xFF;
            case "green":
                return (pixel >> 8) & 0xFF;
            case "blue":
                return pixel & 0xFF;
            default:
                return 0;
        }
    }

    // menghitung rata-rata sebuah color dalam sebuah blok
    public double[] meanColor(int[][] image, int width, int height) {
        double[] mean = new double[3];
        int totalPixels = width * height;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image[y][x];
                mean[0] += (pixel >> 16) & 0xFF;
                mean[1] += (pixel >> 8) & 0xFF;
                mean[2] += pixel & 0xFF;
            }
        }

        mean[0] /= totalPixels;
        mean[1] /= totalPixels;
        mean[2] /= totalPixels;

        return mean;
    }
    
    // menghitung variansi sebuah color dalam sebuah blok
    public double[] varianceColor(int[][] image, int width, int height) {
        double[] variance = new double[3];
        double[] mean = meanColor(image, width, height);
        int totalPixels = width * height;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image[y][x];
                variance[0] += Math.pow(((pixel >> 16) & 0xFF) - mean[0], 2);
                variance[1] += Math.pow(((pixel >> 8) & 0xFF) - mean[1], 2);
                variance[2] += Math.pow((pixel & 0xFF) - mean[2], 2);
            }
        }

        variance[0] /= totalPixels;
        variance[1] /= totalPixels;
        variance[2] /= totalPixels;

        return variance;
    }
    
    // menghitung variansi dalam sebuah blok
    public double variance(int[][] image, int width, int height) {
        double[] variance = varianceColor(image, width, height);
        return (variance[0] + variance[1] + variance[2]) / 3;
    }

    // menghitung kovariansi sebuah color antara dua buah blok
    // add kode kovariansi disini

    // menghitung MAD dalam sebuah blok
    public double MAD(int[][] image, int width, int height) {
        double[] MAD = new double[3];
        double[] mean = meanColor(image, width, height);
        int totalPixels = width * height;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image[y][x];
                MAD[0] += Math.abs(((pixel >> 16) & 0xFF) - mean[0]);
                MAD[1] += Math.abs(((pixel >> 8) & 0xFF) - mean[1]);
                MAD[2] += Math.abs((pixel & 0xFF) - mean[2]);
            }
        }

        MAD[0] /= totalPixels;
        MAD[1] /= totalPixels;
        MAD[2] /= totalPixels;
        return (MAD[0] + MAD[1] + MAD[2]) / 3;
    }

    // menghitung MPD dalam sebuah blok
    public double MPD(int[][] image, int width, int height) {
        int minRed = 255, maxRed = 0;
        int minGreen = 255, maxGreen = 0;
        int minBlue = 255, maxBlue = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image[y][x];
                int red = (pixel >> 16) & 0xFF;
                int green = (pixel >> 8) & 0xFF;
                int blue = pixel & 0xFF;

                maxRed = Math.max(maxRed, red);
                minRed = Math.min(minRed, red);
                maxGreen = Math.max(maxGreen, green);
                minGreen = Math.min(minGreen, green);
                maxBlue = Math.max(maxBlue, blue);
                minBlue = Math.min(minBlue, blue);
            }
        }

        return ((maxRed - minRed) + (maxGreen - minGreen) + (maxBlue - minBlue)) / 3.0;
    }

    // menghitung probabilitas distribusi warna dalam sebuah blok
    // add kode probabilitas disini

    // menghitung entropy dalam sebuah blok
    public double entropy(int[][] image, int width, int height) {
        int[] redHist = new int[256];
        int[] greenHist = new int[256];
        int[] blueHist = new int[256];
        int totalPixels = width * height;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image[y][x];
                redHist[(pixel >> 16) & 0xFF]++;
                greenHist[(pixel >> 8) & 0xFF]++;
                blueHist[pixel & 0xFF]++;
            }
        }

        double redEntropy = 0, greenEntropy = 0, blueEntropy = 0;
        for (int i = 0; i < 256; i++) {
            if (redHist[i] > 0) {
                double p = (double) redHist[i] / totalPixels;
                redEntropy -= p * (Math.log(p) / Math.log(2));
            }
            if (greenHist[i] > 0) {
                double p = (double) greenHist[i] / totalPixels;
                greenEntropy -= p * (Math.log(p) / Math.log(2));
            }
            if (blueHist[i] > 0) {
                double p = (double) blueHist[i] / totalPixels;
                blueEntropy -= p * (Math.log(p) / Math.log(2));
            }
        }

        return (redEntropy + greenEntropy + blueEntropy) / 3;
    }

    // menghitung SSIM antara dua buah blok berukuran sama
    // add kode ssim disini
}