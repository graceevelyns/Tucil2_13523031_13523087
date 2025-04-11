package QuadTree;

import java.util.*;

public class QuadTreeBuilder {
    // menyimpan urutan node quadtree berdasarkan level traversal (untuk keperluan GIF)
    private final List<List<QuadTreeNode>> renderSteps = new ArrayList<>();

    // mengembalikan daftar renderSteps dalam bentuk per-level (BFS)
    public List<List<QuadTreeNode>> getRenderSteps() {
        return renderSteps;
    }

    // mengembalikan daftar renderSteps dalam bentuk list datar (flat)
    public List<QuadTreeNode> getRenderStepsFlat() {
        List<QuadTreeNode> flat = new ArrayList<>();
        for (List<QuadTreeNode> level : renderSteps) {
            flat.addAll(level);
        }
        return flat;
    }

    // membentuk quadtree berdasarkan metode error tertentu, threshold, dan ukuran minimum blok
    public QuadTreeNode buildQuadtree(int[][] image, int x, int y, int width, int height, int errorMethod, double threshold, int minBlockSize) {
        renderSteps.clear();
        return buildRecursive(image, x, y, width, height, errorMethod, threshold, minBlockSize, 0);
    }

    // fungsi rekursif utama untuk pembentukan quadtree berdasarkan error umum (bukan SSIM)
    private QuadTreeNode buildRecursive(int[][] image, int x, int y, int width, int height, int errorMethod, double threshold, int minBlockSize, int depth) {
        ErrorCalculator errorCalculator = new ErrorCalculator();
        double error = errorCalculator.calculateError(image, x, y, width, height, errorMethod);

        QuadTreeNode node;

        // jika error lebih kecil dari threshold atau blok terlalu kecil, hentikan pembagian
        if (error <= threshold || width <= minBlockSize || height <= minBlockSize) {
            int[] color = calculateAverageColor(image, x, y, width, height);
            node = new QuadTreeNode(x, y, width, height, color);
        }
        else {
            int halfWidth = width / 2;
            int halfHeight = height / 2;
            int remainingWidth = width - halfWidth;
            int remainingHeight = height - halfHeight;

            // bagi blok menjadi 4 sub-blok
            QuadTreeNode[] children = new QuadTreeNode[4];
            children[0] = buildRecursive(image, x, y, halfWidth, halfHeight, errorMethod, threshold, minBlockSize, depth + 1);
            children[1] = buildRecursive(image, x + halfWidth, y, remainingWidth, halfHeight, errorMethod, threshold, minBlockSize, depth + 1);
            children[2] = buildRecursive(image, x, y + halfHeight, halfWidth, remainingHeight, errorMethod, threshold, minBlockSize, depth + 1);
            children[3] = buildRecursive(image, x + halfWidth, y + halfHeight, remainingWidth, remainingHeight, errorMethod, threshold, minBlockSize, depth + 1);

            node = new QuadTreeNode(x, y, width, height, children);
        }

        // simpan node ke dalam renderSteps berdasarkan level traversal
        while (renderSteps.size() <= depth) {
            renderSteps.add(new ArrayList<>());
        }
        renderSteps.get(depth).add(node);

        return node;
    }

    // membentuk quadtree menggunakan metode error SSIM, dengan logika khusus perbandingan blok
    public QuadTreeNode buildQuadtreewithSSIM(int[][] imageInput, int x, int y, int width, int height, double threshold, int minBlockSize) {
        renderSteps.clear();
        return buildRecursiveWithSSIM(imageInput, x, y, width, height, threshold, minBlockSize, 0);
    }

    // fungsi rekursif utama untuk SSIM, membandingkan blok rata-rata dengan blok asli
    private QuadTreeNode buildRecursiveWithSSIM(int[][] imageInput, int x, int y, int width, int height, double threshold, int minBlockSize, int depth) {
        int[][] imageOutput = new int[imageInput.length][];
        for (int i = 0; i < imageInput.length; i++) {
            imageOutput[i] = imageInput[i].clone();
        }

        int[] color = calculateAverageColor(imageOutput, x, y, width, height);
        for (int i = y; i < y + height && i < imageOutput.length; i++) {
            for (int j = x; j < x + width && j < imageOutput[i].length; j++) {
                int pixel = (255 << 24) | (color[0] << 16) | (color[1] << 8) | color[2];
                imageOutput[i][j] = pixel;
            }
        }

        ErrorCalculator errorCalculator = new ErrorCalculator();
        double error = errorCalculator.SSIM(imageInput, imageOutput, x, y, width, height);

        QuadTreeNode node;
        // jika error cukup rendah dan ukuran blok masih bisa dibagi, lanjutkan pembagian
        if (error < threshold && (width * height) > minBlockSize) {
            int halfWidth = (int) Math.ceil(width / 2.0);
            int halfHeight = (int) Math.ceil(height / 2.0);

            QuadTreeNode[] children = new QuadTreeNode[4];
            children[0] = buildRecursiveWithSSIM(imageInput, x, y, halfWidth, halfHeight, threshold, minBlockSize, depth + 1);
            children[1] = buildRecursiveWithSSIM(imageInput, x + halfWidth, y, width - halfWidth, halfHeight, threshold, minBlockSize, depth + 1);
            children[2] = buildRecursiveWithSSIM(imageInput, x, y + halfHeight, halfWidth, height - halfHeight, threshold, minBlockSize, depth + 1);
            children[3] = buildRecursiveWithSSIM(imageInput, x + halfWidth, y + halfHeight, width - halfWidth, height - halfHeight, threshold, minBlockSize, depth + 1);

            node = new QuadTreeNode(x, y, width, height, children);
        }
        else {
            node = new QuadTreeNode(x, y, width, height, color);
        }

        // simpan node ke dalam renderSteps berdasarkan level traversal
        while (renderSteps.size() <= depth) {
            renderSteps.add(new ArrayList<>());
        }
        renderSteps.get(depth).add(node);

        return node;
    }

    // menghitung rata-rata nilai RGB pada blok (x, y, width, height)
    private int[] calculateAverageColor(int[][] image, int x, int y, int width, int height) {
        long[] sum = new long[3];
        int count = 0;

        for (int i = y; i < y + height && i < image.length; i++) {
            for (int j = x; j < x + width && j < image[i].length; j++) {
                int pixel = image[i][j];
                sum[0] += (pixel >> 16) & 0xFF;
                sum[1] += (pixel >> 8) & 0xFF;
                sum[2] += pixel & 0xFF;
                count++;
            }
        }

        return new int[] {
            (int) (sum[0] / count),
            (int) (sum[1] / count),
            (int) (sum[2] / count)
        };
    }
}