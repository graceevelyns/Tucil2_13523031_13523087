package QuadTree;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class QuadTreeProcessor {
    // variabel untuk menyimpan waktu proses
    private long startTime;
    private long endTime;
    
    // variabel untuk statistik pohon
    private int totalNodes = 0;
    private int treeDepth = 0;

    // metode utama untuk memproses gambar
    public void processImage(String inputImagePath, String outputImagePath, int errorMethod, double threshold, int minBlockSize) {
        try {
            // catat waktu mulai
            startTime = System.nanoTime();

            System.out.println("(/^-^)/ memulai proses kompresi gambar...");

            // baca gambar input
            BufferedImage image = ImageIO.read(new File(inputImagePath));
            int width = image.getWidth();
            int height = image.getHeight();

            System.out.println("(/^-^)/ gambar berhasil dibaca! ukuran: " + width + "x" + height);

            // konversi gambar ke matriks pixel
            int[][] imagePixels = convertImageToPixels(image, width, height);

            // bangun struktur quadtree
            System.out.println("(/^-^)/ sedang membangun quadtree...");
            QuadTreeBuilder builder = new QuadTreeBuilder();

            // untuk metode perhitungan error non-SSIM
            QuadTreeNode root;
            if (errorMethod != 5) {
                root = builder.buildQuadtree(imagePixels, 0, 0, width, height, errorMethod, threshold, minBlockSize);
            } else {
                root = builder.buildQuadtreewithSSIM(imagePixels, 0, 0, width, height, threshold, minBlockSize);
            }

            // hitung statistik pohon
            calculateTreeDepthAndNodeCount(root, 0);

            // rekonstruksi gambar dari quadtree
            System.out.println("(/^-^)/ merekonstruksi gambar...");
            BufferedImage reconstructedImage = reconstructImage(root, width, height);
            
            // simpan gambar hasil
            ImageIO.write(reconstructedImage, "png", new File(outputImagePath));

            // hitung statistik kompresi
            long originalSize = Files.size(Paths.get(inputImagePath));
            long compressedSize = Files.size(Paths.get(outputImagePath));
            double compressionPercentage = (double) compressedSize / originalSize * 100;

            // catat waktu selesai
            endTime = System.nanoTime();
            long executionTime = (endTime - startTime) / 1_000_000;

            // tampilkan hasil
            System.out.println("\n(/^-^)/ hasil kompresi \\\\(^-^\\\\)");
            System.out.println("==========================================");
            System.out.println("  waktu proses       : " + executionTime + " ms");
            System.out.println("  ukuran asli        : " + originalSize + " bytes");
            System.out.println("  ukuran kompresi    : " + compressedSize + " bytes");
            System.out.println("  rasio kompresi     : " + String.format("%.2f", compressionPercentage) + "%");
            System.out.println("  kedalaman pohon    : " + treeDepth);
            System.out.println("  jumlah node        : " + totalNodes);
            System.out.println("==========================================");
            System.out.println("\n(/^-^)/ kompresi selesai! hasil disimpan di: " + outputImagePath);

        } catch (Exception e) {
            System.out.println("ups! terjadi kesalahan saat memproses gambar:");
            e.printStackTrace();
        }
    }

    // konversi gambar ke matriks pixel
    private int[][] convertImageToPixels(BufferedImage image, int width, int height) {
        int[][] pixels = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y][x] = image.getRGB(x, y);
            }
        }
        return pixels;
    }

    // hitung kedalaman dan jumlah node pohon
    private void calculateTreeDepthAndNodeCount(QuadTreeNode node, int depth) {
        if (node == null) return;

        // update statistik
        treeDepth = Math.max(treeDepth, depth);
        totalNodes++;

        // rekursif untuk anak-anak node
        if (!node.isLeaf()) {
            for (QuadTreeNode child : node.getChildren()) {
                calculateTreeDepthAndNodeCount(child, depth + 1);
            }
        }
    }

    // rekonstruksi gambar dari quadtree
    private BufferedImage reconstructImage(QuadTreeNode root, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        reconstructImageRecursive(root, image);
        return image;
    }

    // metode rekursif untuk rekonstruksi
    private void reconstructImageRecursive(QuadTreeNode node, BufferedImage image) {
        if (node.isLeaf()) {
            // gabungkan komponen warna
            int rgb = (node.getColor()[0] << 16) | (node.getColor()[1] << 8) | node.getColor()[2];
            
            // gambar blok warna
            for (int y = node.getY(); y < node.getY() + node.getHeight(); y++) {
                for (int x = node.getX(); x < node.getX() + node.getWidth(); x++) {
                    if (x < image.getWidth() && y < image.getHeight()) {
                        image.setRGB(x, y, rgb);
                    }
                }
            }
        }
        else {
            for (QuadTreeNode child : node.getChildren()) {
                reconstructImageRecursive(child, image);
            }
        }
    }
}