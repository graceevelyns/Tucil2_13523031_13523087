package QuadTree;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import com.madgag.gif.fmsware.AnimatedGifEncoder;

public class QuadTreeProcessor {
    private long startTime;
    private long endTime;
    private int totalNodes = 0;
    private int treeDepth = 0;
    private AnimatedGifEncoder AnimatedGifEncoder;
    private boolean createGif;
    private int frameCount = 0;

    /**
     * proses utama untuk membaca gambar, mengkompresinya dengan QuadTree, menyimpan hasil,
     * dan menghasilkan GIF animasi subdivisi jika diinginkan
     */
    public void processImage(String inputImagePath, String outputImagePath, int errorMethod, double threshold, int minBlockSize, String gifOutputPath) {
        this.createGif = (gifOutputPath != null && !gifOutputPath.isEmpty());

        try {
            startTime = System.nanoTime();
            System.out.println("\n(/^-^)/ memulai proses kompresi gambar...");

            BufferedImage image = ImageIO.read(new File(inputImagePath));
            int width = image.getWidth();
            int height = image.getHeight();
            System.out.println("(/^-^)/ gambar berhasil dibaca! ukuran: " + width + "x" + height);

            if (createGif) {
                System.out.println("(/^-^)/ menyiapkan pembuatan GIF animasi...");
                AnimatedGifEncoder = new AnimatedGifEncoder();
                AnimatedGifEncoder.start(gifOutputPath);
                AnimatedGifEncoder.setRepeat(0);
                AnimatedGifEncoder.setDelay(300);
            }

            int[][] imagePixels = convertImageToPixels(image, width, height);

            System.out.println("(/^-^)/ sedang membangun quadtree...");
            QuadTreeBuilder builder = new QuadTreeBuilder();
            QuadTreeNode root = builder.buildQuadtree(imagePixels, 0, 0, width, height, errorMethod, threshold, minBlockSize);
            calculateTreeDepthAndNodeCount(root, 0);

            if (createGif) {
                List<List<QuadTreeNode>> stepsByLevel = builder.getRenderSteps();
                renderGIFInSubdivisionOrder(stepsByLevel, width, height);
                AnimatedGifEncoder.finish();
                System.out.println("(/^-^)/ GIF animasi berhasil dibuat di: " + gifOutputPath);
                System.out.println("  total frame        : " + frameCount);
            }

            System.out.println("(/^-^)/ merekonstruksi gambar akhir...");
            BufferedImage reconstructedImage = reconstructImage(root, width, height);
            ImageIO.write(reconstructedImage, "png", new File(outputImagePath));

            long originalSize = Files.size(Paths.get(inputImagePath));
            long compressedSize = Files.size(Paths.get(outputImagePath));
            double compressionPercentage = (double) compressedSize / originalSize * 100;

            endTime = System.nanoTime();
            long executionTime = (endTime - startTime) / 1_000_000;

            System.out.println("\n(/^-^)/ hasil kompresi \\\\(^-^\\\\)");
            System.out.println("==========================================");
            System.out.println("  waktu proses       : " + executionTime + " ms");
            System.out.println("  ukuran asli        : " + originalSize + " bytes");
            System.out.println("  ukuran kompresi    : " + compressedSize + " bytes");
            System.out.println("  rasio kompresi     : " + String.format("%.2f", compressionPercentage) + "%");
            System.out.println("  kedalaman pohon    : " + treeDepth);
            System.out.println("  jumlah node        : " + totalNodes);
            if (createGif) {
                System.out.println("  output GIF         : " + gifOutputPath);
            }
            System.out.println("==========================================");
            System.out.println("\n(/^-^)/ kompresi selesai! hasil disimpan di: " + outputImagePath);

        } catch (Exception e) {
            System.out.println("Ups! Terjadi kesalahan saat memproses gambar:");
            e.printStackTrace();
        }
    }

    /**
     * mengubah objek BufferedImage menjadi matriks pixel (RGB integer)
     */
    private int[][] convertImageToPixels(BufferedImage image, int width, int height) {
        int[][] pixels = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y][x] = image.getRGB(x, y);
            }
        }
        return pixels;
    }

    /**
     * menghitung total node dan kedalaman maksimum dari struktur Quadtree
     */
    private void calculateTreeDepthAndNodeCount(QuadTreeNode node, int depth) {
        if (node == null) return;
        treeDepth = Math.max(treeDepth, depth);
        totalNodes++;
        if (!node.isLeaf()) {
            for (QuadTreeNode child : node.getChildren()) {
                calculateTreeDepthAndNodeCount(child, depth + 1);
            }
        }
    }

    /**
     * menghasilkan ulang gambar dari struktur Quadtree hasil kompresi
     */
    private BufferedImage reconstructImage(QuadTreeNode root, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        reconstructImageRecursive(root, image);
        return image;
    }

    /**
     * fungsi rekursif untuk menggambar warna blok-blok hasil QuadTree pada BufferedImage
     */
    private void reconstructImageRecursive(QuadTreeNode node, BufferedImage image) {
        if (node.isLeaf()) {
            int rgb = (node.getColor()[0] << 16) | (node.getColor()[1] << 8) | node.getColor()[2];
            for (int y = node.getY(); y < node.getY() + node.getHeight(); y++) {
                for (int x = node.getX(); x < node.getX() + node.getWidth(); x++) {
                    if (x < image.getWidth() && y < image.getHeight()) {
                        image.setRGB(x, y, rgb);
                    }
                }
            }
        } else {
            for (QuadTreeNode child : node.getChildren()) {
                reconstructImageRecursive(child, image);
            }
        }
    }

    /**
     * merender proses subdivisi QuadTee berdasarkan level (BFS) ke dalam GIF animasi
     */
    private void renderGIFInSubdivisionOrder(List<List<QuadTreeNode>> stepsByLevel, int width, int height) {
        BufferedImage canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int white = (255 << 16) | (255 << 8) | 255;
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                canvas.setRGB(x, y, white);

        if (createGif) {
            BufferedImage firstFrame = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            firstFrame.getGraphics().drawImage(canvas, 0, 0, null);
            AnimatedGifEncoder.addFrame(firstFrame);
            frameCount++;
        }

        for (List<QuadTreeNode> levelNodes : stepsByLevel) {
            for (QuadTreeNode node : levelNodes) {
                int[] color = node.getColor();
                if (color == null) continue;

                int rgb = (color[0] << 16) | (color[1] << 8) | color[2];

                for (int y = node.getY(); y < node.getY() + node.getHeight(); y++) {
                    for (int x = node.getX(); x < node.getX() + node.getWidth(); x++) {
                        if (x < width && y < height) {
                            canvas.setRGB(x, y, rgb);
                        }
                    }
                }
            }

            if (createGif) {
                BufferedImage frame = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                frame.getGraphics().drawImage(canvas, 0, 0, null);
                AnimatedGifEncoder.addFrame(frame);
                frameCount++;
            }
        }
    }
}