package QuadTree;

import java.util.HashMap;
import java.util.Map;

public class ErrorCalculator {
    
    /**
     * API ke kelas QuadTreeBuilder
     * 1 - Variance
     * 2 - Mean Absolute Deviation (MAD)
     * 3 - Max Pixel Difference
     * 4 - Entropy
     */
    public double calculateError(int[][] image, int startX, int startY, int width, int height, int errorMethod) {
        switch (errorMethod) {
            case 1:
                return variance(image, startX, startY, width, height);
            case 2:
                return MAD(image, startX, startY, width, height);
            case 3:
                return MPD(image, startX, startY, width, height);
            case 4:
                return entropy(image, startX, startY, width, height);
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
    public double[] meanColor(int[][] image, int startX, int startY, int width, int height) {
        double[] mean = new double[3];
        int totalPixels = 0;

        for (int y = startY; y < startY + height && y < image.length; y++) {
            for (int x = startX; x < startX + width && x < image[y].length; x++) {
                int pixel = image[y][x];
                mean[0] += getColor(pixel, "red");
                mean[1] += getColor(pixel, "green");
                mean[2] += getColor(pixel, "blue");
                totalPixels++;
            }
        }

        mean[0] /= totalPixels;
        mean[1] /= totalPixels;
        mean[2] /= totalPixels;

        return mean;
    }
    
    // menghitung variansi sebuah color dalam sebuah blok
    public double[] varianceColor(int[][] image, int startX, int startY, int width, int height) {
        double[] variance = new double[3];
        double[] mean = meanColor(image, startX, startY, width, height);
        int totalPixels = 0;

        for (int y = startY; y < startY + height && y < image.length; y++) {
            for (int x = startX; x < startX + width && x < image[y].length; x++) {
                int pixel = image[y][x];
                variance[0] += Math.pow((getColor(pixel, "red")) - mean[0], 2);
                variance[1] += Math.pow((getColor(pixel, "green")) - mean[1], 2);
                variance[2] += Math.pow((getColor(pixel, "blue")) - mean[2], 2);
                totalPixels++;
            }
        }

        variance[0] /= totalPixels;
        variance[1] /= totalPixels;
        variance[2] /= totalPixels;

        return variance;
    }
    
    // menghitung variansi dalam sebuah blok
    public double variance(int[][] image, int startX, int startY, int width, int height) {
        double[] variance = varianceColor(image, startX, startY, width, height);
        return ((variance[0] + variance[1] + variance[2]) / 3);
    }

    // menghitung kovariansi sebuah color antara dua buah blok
    public double[] covarianceColor(int[][] imageX, int[][] imageY, int startX, int startY, int width, int height) {
        double[] covariance = new double[3];
        double[] meanX = meanColor(imageX, startX, startY, width, height);
        double[] meanY = meanColor(imageY, startX, startY, width, height);
        int totalPixels = 0;

        for (int y = startY; y < startY + height && y < imageX.length; y++) {
            for (int x = startX; x < startX + width && x < imageX[y].length; x++) {
                covariance[0] += (getColor(imageX[y][x], "red") - meanX[0]) * (getColor(imageY[y][x], "red") - meanY[0]);
                covariance[1] += (getColor(imageX[y][x], "green") - meanX[1]) * (getColor(imageY[y][x], "green") - meanY[1]);
                covariance[2] += (getColor(imageX[y][x], "blue") - meanX[2]) * (getColor(imageY[y][x], "blue") - meanY[2]);
                totalPixels++;
            }
        }

        covariance[0] /= totalPixels;
        covariance[1] /= totalPixels;
        covariance[2] /= totalPixels;

        return covariance;
    }

    // menghitung MAD dalam sebuah blok
    public double MAD(int[][] image, int startX, int startY, int width, int height) {
        double[] MAD = new double[3];
        double[] mean = meanColor(image, startX, startY, width, height);
        int totalPixels = 0;

        for (int y = startY; y < startY + height && y < image.length; y++) {
            for (int x = startX; x < startX + width && x < image[y].length; x++) {
                MAD[0] += Math.abs((getColor(image[y][x], "red")) - mean[0]);
                MAD[1] += Math.abs((getColor(image[y][x], "green")) - mean[1]);
                MAD[2] += Math.abs((getColor(image[y][x], "blue")) - mean[2]);
                totalPixels++;
            }
        }

        MAD[0] /= totalPixels;
        MAD[1] /= totalPixels;
        MAD[2] /= totalPixels;

        return ((MAD[0] + MAD[1] + MAD[2]) / 3);
    }

    // menghitung MPD dalam sebuah blok
    public double MPD(int[][] image, int startX, int startY, int width, int height) {
        int minRed = 255, maxRed = 0;
        int minGreen = 255, maxGreen = 0;
        int minBlue = 255, maxBlue = 0;

        for (int y = startY; y < startY + height && y < image.length; y++) {
            for (int x = startX; x < startX + width && x < image[y].length; x++) {
                int pixel = image[y][x];
                int red = getColor(pixel, "red");
                int green = getColor(pixel, "green");
                int blue = getColor(pixel, "blue");

                maxRed = Math.max(maxRed, red);
                minRed = Math.min(minRed, red);
                maxGreen = Math.max(maxGreen, green);
                minGreen = Math.min(minGreen, green);
                maxBlue = Math.max(maxBlue, blue);
                minBlue = Math.min(minBlue, blue);
            }
        }

        return (((maxRed - minRed) + (maxGreen - minGreen) + (maxBlue - minBlue)) / 3.0);
    }

    // menghitung probabilitas distribusi warna dalam sebuah blok
    public double[][] probability(int[][] image, int startX, int startY, int width, int height) {
        Map<Integer, Integer> redFrequency = new HashMap<>();
        Map<Integer, Integer> greenFrequency = new HashMap<>();
        Map<Integer, Integer> blueFrequency = new HashMap<>();
        int totalPixels = 0;

        for (int y = startY; y < startY + height && y < image.length; y++) {
            for (int x = startX; x < startX + width && x < image[y].length; x++) {
                int pixel = image[y][x];
                int redValue = getColor(pixel, "red");
                int greenValue = getColor(pixel, "green");
                int blueValue = getColor(pixel, "blue");

                redFrequency.put(redValue, redFrequency.getOrDefault(redValue, 0) + 1);
                greenFrequency.put(greenValue, greenFrequency.getOrDefault(greenValue, 0) + 1);
                blueFrequency.put(blueValue, blueFrequency.getOrDefault(blueValue, 0) + 1);
                totalPixels++;
            }
        }

        double[][] probability = new double[3][256];
        
        for (Map.Entry<Integer, Integer> e : redFrequency.entrySet()) {
            probability[0][e.getKey()] = (double) e.getValue() / totalPixels;
        }

        for (Map.Entry<Integer, Integer> e : greenFrequency.entrySet()) {
            probability[1][e.getKey()] = (double) e.getValue() / totalPixels;
        }

        for (Map.Entry<Integer, Integer> e : blueFrequency.entrySet()) {
            probability[2][e.getKey()] = (double) e.getValue() / totalPixels;
        }

        return probability;
    }

    // public double entropy(int[][] image, int startX, int startY, int width, int height) {
    //     int[] redHist = new int[256];
    //     int[] greenHist = new int[256];
    //     int[] blueHist = new int[256];
    //     int totalPixels = width * height;

    //     for (int y = startY; y < startY + height && y < image.length; y++) {
    //         for (int x = startX; x < startX + width && x < image[y].length; x++) {
    //             int pixel = image[y][x];
    //             redHist[getColor(image[y][x], "red")]++;
    //             greenHist[getColor(image[y][x], "green")]++;
    //             blueHist[getColor(pixel, "blue")]++;
    //         }
    //     }

    //     double redEntropy = 0, greenEntropy = 0, blueEntropy = 0;
    //     for (int i = 0; i < 256; i++) {
    //         if (redHist[i] > 0) {
    //             double p = (double) redHist[i] / totalPixels;
    //             redEntropy -= p * (Math.log(p) / Math.log(2));
    //         }
    //         if (greenHist[i] > 0) {
    //             double p = (double) greenHist[i] / totalPixels;
    //             greenEntropy -= p * (Math.log(p) / Math.log(2));
    //         }
    //         if (blueHist[i] > 0) {
    //             double p = (double) blueHist[i] / totalPixels;
    //             blueEntropy -= p * (Math.log(p) / Math.log(2));
    //         }
    //     }

    //     return (redEntropy + greenEntropy + blueEntropy) / 3;
    // }

    // menghitung entropy dalam sebuah blok
    public double entropy(int[][] image, int startX, int startY, int width, int height) {
        double[] entropy = new double[3];
        double[][] probability = probability(image, startX, startY, width, height);

        for (int i = 0; i < 256; i++) {
            double r = probability[0][i], g = probability[1][i], b = probability[2][i];
            
            if (r > 0) {
                entropy[0] += r * (Math.log(r) / Math.log(2));
            }

            if (g > 0) {
                entropy[1] += g * (Math.log(g) / Math.log(2));
            }

            if (b > 0) {
                entropy[2] += b * (Math.log(b) / Math.log(2));
            }
        }

        return (((entropy[0] * -1) + (entropy[1] * -1) + entropy[2] * -1) / 3) * 100 / 8;
    }

    // menghitung SSIM antara dua buah blok berukuran sama
    public double SSIM(int[][] imageX, int[][] imageY, int startX, int startY, int width, int height) {
        double[] SSIM = new double[3];
        double[] meanX = meanColor(imageX, startX, startY, width, height);
        double[] meanY = meanColor(imageY, startX, startY, width, height);
        double[] varianceX = varianceColor(imageX, startX, startY, width, height);
        double[] varianceY = varianceColor(imageY, startX, startY, width, height);
        double[] covariance = covarianceColor(imageX, imageY, startX, startY, width, height);

        // nanti konstantanya disesuaiin lagi
        double c1 = Math.pow((0.01 * 255), 2);
        double c2 = Math.pow((0.03 * 255), 2);

        for (int i = 0; i < 3; i++) {
            SSIM[i] = (((2 * meanX[i] * meanY[i]) + c1) * ((2 * covariance[i]) + c2)) /
            (((meanX[i] * meanX[i]) + (meanY[i] * meanY[i]) + c1) * (varianceX[i] + varianceY[i] + c2));
  
        }

        // nanti konstantanya disesuaiin lagi
        return (0.299 * SSIM[0] + 0.587 * SSIM[1] + 0.114 * SSIM[2]) * 100;
    }
}