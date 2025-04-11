package QuadTree;

public class QuadTreeNode {
    private int x, y;                   // koordinat kiri atas blok gambar
    private int width, height;          // ukuran blok gambar
    private int[] color;                // rata-rata warna blok (R, G, B)
    private QuadTreeNode[] children;    // children dari internal nodes

    /**
     * Konstruktor leaf nodes
     * Menyimpan informasi tentang blok gambar yang tidak dibagi lebih lanjut
     * @param x         koordinat horizontal kiri atas blok gambar
     * @param y         koordinat vertikal kiri atas blok gambar
     * @param width     lebar blok gambar
     * @param height    tinggi blok gambar
     * @param color     rata-rata warna blok gambar dalam format [Red, Green, Blue]
     */
    public QuadTreeNode(int x, int y, int width, int height, int[] color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.children = null;
    }

    /**
     * Konstruktor internal nodes
     * Node internal menyimpan referensi ke 4 anak yang membentuk bagian-bagian gambar lebih kecil
     * @param x         koordinat horizontal kiri atas blok gambar
     * @param y         koordinat vertikal kiri atas blok gambar
     * @param width     lebar blok gambar
     * @param height    tinggi blok gambar
     * @param children  array berisi 4 anak node
     */
    public QuadTreeNode(int x, int y, int width, int height, QuadTreeNode[] children) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = null;
        this.children = children;
    }

    /**
     * Mengambil koordinat X (horizontal) dari node
     * @return koordinat X dari node
     */
    public int getX() {
        return x;
    }

    /**
     * Mengambil koordinat Y (vertikal) dari node
     * @return koordinat Y dari node
     */
    public int getY() {
        return y;
    }

    /**
     * Mengambil lebar blok gambar yang diwakili oleh node
     * @return lebar blok gambar
     */
    public int getWidth() {
        return width;
    }

    /**
     * Mengambil tinggi blok gambar yang diwakili oleh node
     * @return tinggi blok gambar
     */
    public int getHeight() {
        return height;
    }

    /**
     * Mengambil warna rata-rata blok gambar untuk leaf node
     * @return array berisi warna rata-rata dalam format [Red, Green, Blue]
     */
    public int[] getColor() {
        return color;
    }

    /**
     * Mengambil referensi ke child node jika node ini adalah internal node
     * @return array berisi referensi ke 4 anak node
     */
    public QuadTreeNode[] getChildren() {
        return children;
    }

    /**
     * Memeriksa apakah node ini adalah leaf node
     * @return true jika node adalah leaf node, false jika node adalah internal node
     */
    public boolean isLeaf() {
        return children == null;
    }
}