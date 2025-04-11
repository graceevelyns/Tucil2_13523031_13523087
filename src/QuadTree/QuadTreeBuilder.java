package QuadTree;

public class QuadTreeBuilder {
    public QuadTreeNode buildQuadtree(int[][] image, int x, int y, int width, int height, int errorMethod, double threshold, int minBlockSize) {
        ErrorCalculator errorCalculator = new ErrorCalculator();
        double error = errorCalculator.calculateError(image, width, height, errorMethod);
        // butuh validasi errorMethod di antara 1 - 4
        // + special case buat SSIM

        // jika error > threshold dan ukuran blok cukup besar untuk dibagi, bagi blok menjadi 4 sub-blok
        if (error > threshold && (width * height) > minBlockSize) {
            QuadTreeNode[] children = new QuadTreeNode[4];
            int halfWidth = (int) Math.ceil(width / 2.0);
            int halfHeight = (int) Math.ceil(height / 2.0);     

            // divide
            children[0] = buildQuadtree(image, x, y, halfWidth, halfHeight, errorMethod, threshold, minBlockSize);
            children[1] = buildQuadtree(image, x + halfWidth, y, width - halfWidth, halfHeight, errorMethod, threshold, minBlockSize);
            children[2] = buildQuadtree(image, x, y + halfHeight, halfWidth, height - halfHeight, errorMethod, threshold, minBlockSize);
            children[3] = buildQuadtree(image, x + halfWidth, y + halfHeight, width - halfWidth, height - halfHeight, errorMethod, threshold, minBlockSize);

            return new QuadTreeNode(x, y, width, height, children);
        }

        // conquer (jika error < threshold atau ukuran blok terlalu kecil)
        int[] color = calculateAverageColor(image, x, y, width, height);
        return new QuadTreeNode(x, y, width, height, color);
    }

    // menghitung rata-rata RGB sebuah blok gambar
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