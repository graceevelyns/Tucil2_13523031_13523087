package QuadTree;

public class QuadTreeBuilder {
    public QuadTreeNode buildQuadtree(int[][] image, int x, int y, int width, int height, int errorMethod, double threshold, int minBlockSize) {
        ErrorCalculator errorCalculator = new ErrorCalculator();
        double error = errorCalculator.calculateError(image, x, y, width, height, errorMethod);

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

    public QuadTreeNode buildQuadtreewithSSIM(int[][] imageInput, int x, int y, int width, int height, double threshold, int minBlockSize) {
        int[][] imageOutput = new int[imageInput.length][];
        for (int i = 0; i < imageInput.length; i++) {
            imageOutput[i] = imageInput[i].clone();
        }

        int[] color = calculateAverageColor(imageOutput, x, y, width, height);
        System.out.println("mean color: R=" + color[0] + ", G=" + color[1] + ", B=" + color[2]);
        for (int i = y; i < y + height && i < imageOutput.length; i++) {
            for (int j = x; j < x + width && j < imageOutput[i].length; j++) {
                int pixel = (255 << 24) | (color[0] << 16) | (color[1] << 8) | color[2];
                imageOutput[i][j] = pixel;
            }
        }
    
        ErrorCalculator errorCalculator = new ErrorCalculator();
        double error = errorCalculator.SSIM(imageInput, imageOutput, x, y, width, height);

        if (error < threshold && (width * height) > minBlockSize) {
            QuadTreeNode[] children = new QuadTreeNode[4];
            int halfWidth = (int) Math.ceil(width / 2.0);
            int halfHeight = (int) Math.ceil(height / 2.0);     

            // divide
            children[0] = buildQuadtreewithSSIM(imageInput, x, y, halfWidth, halfHeight, threshold, minBlockSize);
            children[1] = buildQuadtreewithSSIM(imageInput, x + halfWidth, y, width - halfWidth, halfHeight, threshold, minBlockSize);
            children[2] = buildQuadtreewithSSIM(imageInput, x, y + halfHeight, halfWidth, height - halfHeight, threshold, minBlockSize);
            children[3] = buildQuadtreewithSSIM(imageInput, x + halfWidth, y + halfHeight, width - halfWidth, height - halfHeight, threshold, minBlockSize);

            return new QuadTreeNode(x, y, width, height, children);
        }
        
        // conquer (jika error < threshold atau ukuran blok terlalu kecil)
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