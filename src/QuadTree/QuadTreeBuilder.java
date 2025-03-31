package QuadTree;

public class QuadTreeBuilder {
    /**
     * Fungsi untuk membangun QuadTree dengan metode pembagian rekursif (divide and conquer)
     * @param image             matriks piksel gambar yang akan diproses
     * @param x                 koordinat X dari kiri atas blok gambar
     * @param y                 koordinat Y dari kiri atas blok gambar
     * @param width             lebar blok gambar
     * @param height            tinggi blok gambar
     * @param errorMethod       metode perhitungan error yang digunakan
     * @param threshold         nilai ambang batas error (untuk menentukan apakah blok harus dibagi lebih lanjut atau tidak)
     * @param minBlockSize      ukuran minimum blok gambar yang tidak boleh dibagi lebih lanjut
     * @return                  QuadTreeNode yang merepresentasikan blok gambar yang sudah diproses
     */
    public QuadTreeNode buildQuadtree(int[][] image, int x, int y, int width, int height, int errorMethod, double threshold, int minBlockSize) {
        double error = ErrorCalculator.calculateError(image, x, y, width, height, errorMethod);

        // jika error > threshold dan ukuran blok cukup besar untuk dibagi, bagi blok menjadi 4 sub-blok
        if (error > threshold && (width * height) > minBlockSize) {
            QuadTreeNode[] children = new QuadTreeNode[4];
            int halfWidth = (int) Math.ceil(width / 2.0);
            int halfHeight = (int) Math.ceil(height / 2.0);        

            // divide
            children[0] = buildQuadtree(image, x, y, halfWidth, halfHeight, errorMethod, threshold, minBlockSize);                          // top-left
            children[1] = buildQuadtree(image, x + halfWidth, y, halfWidth, halfHeight, errorMethod, threshold, minBlockSize);              // top-right
            children[2] = buildQuadtree(image, x, y + halfHeight, halfWidth, halfHeight, errorMethod, threshold, minBlockSize);             // bottom-left
            children[3] = buildQuadtree(image, x + halfWidth, y + halfHeight, halfWidth, halfHeight, errorMethod, threshold, minBlockSize); // bottom-right

            return new QuadTreeNode(x, y, width, height, children);
        }

        // conquer (jika error < threshold atau ukuran blok terlalu kecil)
        int[] color = calculateAverageColor(image, x, y, width, height);
        return new QuadTreeNode(x, y, width, height, color);
    }

    /**
     * Menghitung rata-rata RGB dari sebuah blok gambar
     * @param image         matriks piksel gambar
     * @param x             koordinat kiri atas (x) blok gambar
     * @param y             koordinat kiri atas (y) blok gambar
     * @param width         lebar blok gambar
     * @param height        tinggi blok gambar
     * @return              array berisi warna rata-rata dalam format [Red, Green, Blue]
     */
    private int[] calculateAverageColor(int[][] image, int x, int y, int width, int height) {
        int[] color = new int[3];
        int totalPixels = width * height;
        for (int i = y; i < y + height; i++) {
            for (int j = x; j < x + width; j++) {
                color[0] += (image[i][j] >> 16) & 0xFF; // red
                color[1] += (image[i][j] >> 8) & 0xFF;  // green
                color[2] += image[i][j] & 0xFF;         // blue
            }
        }
        color[0] = (int) Math.round(color[0] / totalPixels);
        color[1] = (int) Math.round(color[1] / totalPixels);
        color[2] = (int) Math.round(color[2] / totalPixels);       
        
        return color;
    }
}