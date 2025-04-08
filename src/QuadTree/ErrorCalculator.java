package QuadTree;

import java.util.Map;
import java.util.HashMap;

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
    public int getColor(int[] pixel, String color) {
        switch (color) {
            case "red":
                return pixel[0];
            case "green":
                return pixel[1];
            case "blue":
                return pixel[2];
            default:
                return 0;
        }
    }

    // menghitung rata-rata sebuah color dalam sebuah blok
    public double[] meanColor(int[][] image, int width, int height) {
        double[] mean = new double[3];
        int totalPixels = height * width;

        for (int i = 0; i < totalPixels; i++) {
            mean[0] += getColor(image[i], "red");
            mean[1] += getColor(image[i], "green");
            mean[2] += getColor(image[i], "blue");
        }

        mean[0] /= totalPixels; mean[1] /= totalPixels; mean[2] /= totalPixels;
        return mean;
    }

    // menghitung variansi sebuah color dalam sebuah blok
    public double[] varianceColor(int[][] image, int width, int height) {
        double[] variance = new double[3];
        double[] mean = meanColor(image, height, width);
        int totalPixels = height * width;

        for (int i = 0; i < totalPixels; i++) {
            variance[0] += Math.pow(getColor(image[i], "red") - mean[0], 2);
            variance[1] += Math.pow(getColor(image[i], "green") - mean[1], 2);
            variance[2] += Math.pow(getColor(image[i], "blue") - mean[2], 2);
        }

        variance[0] /= totalPixels; variance[1] /= totalPixels; variance[2] /= totalPixels;
        return variance;
    }

    // menghitung variansi dalam sebuah blok
    public double variance(int[][] image, int width, int height) {
        double[] variance = varianceColor(image, height, width);

        return (variance[0] + variance[1] + variance[2]) / 3;
    }

    // menghitung kovariansi sebuah color antara dua buah blok
    public double[] covarianceColor(int[][] imageX, int[][] imageY, int width, int height) {
        double[] covariance = new double[3];
        double[] meanX = meanColor(imageX, height, width);
        double[] meanY = meanColor(imageY, height, width);
        int totalPixels = height * width;

        for (int i = 0; i < totalPixels; i++) {
            covariance[0] += (getColor(imageX[i], "red") - meanX[0]) * getColor(imageY[i], "red") - meanY[0];
            covariance[1] += (getColor(imageX[i], "green") - meanX[1]) * getColor(imageY[i], "green") - meanY[1];
            covariance[2] += (getColor(imageX[i], "blue") - meanX[2]) * getColor(imageY[i], "blue") - meanY[2];
        }

        return covariance;
    }

    // menghitung MAD dalam sebuah blok
    public double MAD(int[][] image, int width, int height) {
        double[] MAD = new double[3];
        double[] mean = meanColor(image, height, width);
        int totalPixels = height * width;

        for (int i = 0; i < totalPixels; i++) {
            MAD[0] += Math.abs(getColor(image[i], "red") - mean[0]);
            MAD[1] += Math.abs(getColor(image[i], "green") - mean[1]);
            MAD[2] += Math.abs(getColor(image[i], "blue") - mean[2]);
        }

        MAD[0] /= totalPixels; MAD[1] /= totalPixels; MAD[2] /= totalPixels;
        return (MAD[0] + MAD[1] + MAD[2]) / 3;
    }

    // menghitung MPD dalam sebuah blok
    public double MPD(int[][] image, int width, int height) {
        double[] MPD = {getColor(image[0], "red"), getColor(image[0], "red"), 
                        getColor(image[0], "green"), getColor(image[0], "green"), 
                        getColor(image[0], "blue"), getColor(image[0], "blue")};
        int totalPixels = height * width;

        for (int i = 1; i < totalPixels; i++) {
            if (getColor(image[i], "red") > MPD[0]) {
                MPD[0] = getColor(image[i], "red");
            }

            if (getColor(image[i], "red") < MPD[1]) {
                MPD[1] = getColor(image[i], "red");
            }

            if (getColor(image[i], "green") > MPD[2]) {
                MPD[2] = getColor(image[i], "green");
            }
            
            if (getColor(image[i], "green") < MPD[3]) {
                MPD[3] = getColor(image[i], "green");
            }
            
            if (getColor(image[i], "blue") > MPD[4]) {
                MPD[4] = getColor(image[i], "blue");
            }
            
            if (getColor(image[i], "blue") < MPD[5]) {
                MPD[5] = getColor(image[i], "blue");
            }
        }

        return ((MPD[0] - MPD[1]) + (MPD[2] - MPD[3]) + (MPD[4] - MPD[5])) / 3;
    }

    // menghitung probabilitas distribusi warna dalam sebuah blok
    public double[][] probability(int[][] image, int width, int height) {
        Map<Integer, Integer> redFrequency = new HashMap<>();
        Map<Integer, Integer> greenFrequency = new HashMap<>();
        Map<Integer, Integer> blueFrequency = new HashMap<>();
        int totalPixels = height * width;

        for (int i = 0; i < totalPixels; i++) {
            int redValue = getColor(image[i], "red");
            int greenValue = getColor(image[i], "green");
            int blueValue = getColor(image[i], "blue");

            redFrequency.put(redValue, redFrequency.getOrDefault(redValue, 0) + 1);
            greenFrequency.put(greenValue, greenFrequency.getOrDefault(greenValue, 0) + 1);
            blueFrequency.put(blueValue, blueFrequency.getOrDefault(blueValue, 0) + 1);
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

    // menghitung entropy dalam sebuah blok
    public double entropy(int[][] image, int width, int height) {
        double[] entropy = new double[3];
        double[][] probability = probability(image, height, width);

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

        return ((entropy[0] * -1) + (entropy[1] * -1) + entropy[2] * -1) / 3;
    }

    // menghitung SSIM antara dua buah blok berukuran sama
    public double SSIM(int[][] imageX, int[][] imageY, int width, int height) {
        double[] SSIM = new double[3];
        double[] meanX = meanColor(imageX, height, width);
        double[] meanY = meanColor(imageY, height, width);
        double[] varianceX = varianceColor(imageX, height, width);
        double[] varianceY = varianceColor(imageY, height, width);
        double[] covariance = covarianceColor(imageX, imageY, height, width);

        // nanti konstantanya disesuaiin lagi
        double c1 = Math.pow((0.01 * 255), 2);
        double c2 = Math.pow((0.03 * 255), 2);

        for (int i = 0; i < 3; i++) {
            SSIM[i] = (((2 * meanX[i] * meanY[i]) + c1) * ((2 * covariance[i]) + c2)) /
                      (Math.pow(meanX[i], 2) + Math.pow(meanY[i], 2) + c1) * (Math.pow(varianceX[i], 2) + Math.pow(varianceY[i], 2) + c2);
        }

        // nanti konstantanya disesuaiin lagi
        return 0.299 * SSIM[0] + 0.587 * SSIM[1] + 0.114 * SSIM[2];
    }
}
